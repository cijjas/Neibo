package ar.edu.itba.paw.services.email;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.exceptions.MailingException;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@EnableScheduling
@Component
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 500;
    private static final String BASE_URL = "http://pawserver.it.itba.edu.ar/paw-2023b-02/";
    private final UserDao userDao;
    private final NeighborhoodDao neighborhoodDao;
    private final EventDao eventDao;

    @Autowired
    @Qualifier("emailMessageSource")
    private MessageSource emailMessageSource;

    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    public EmailServiceImpl(UserDao userDao, NeighborhoodDao neighborhoodDao, EventDao eventDao) {
        this.userDao = userDao;
        this.neighborhoodDao = neighborhoodDao;
        this.eventDao = eventDao;
    }

    private void sendHtmlMessage(String to, String subject, Map<String, Object> variables, String templateModel, Language language) {
        LOGGER.info("Sending HTML message to {}", to);
        try {
            final Context context = new Context();
            String subjectMessage;

            context.setVariables(variables);
            if(language == Language.ENGLISH) {
                context.setLocale(new Locale("en", "US"));
                subjectMessage = emailMessageSource.getMessage(subject, null, Locale.US);
            } else {
                context.setLocale(new Locale("es", "AR"));
                subjectMessage = emailMessageSource.getMessage(subject, null, new Locale("es", "AR"));
            }

            final String htmlContext = this.thymeleafTemplateEngine.process(templateModel, context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subjectMessage);
            helper.setText(htmlContext, true);
            emailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Error whilst sending HTML message to {}", to);
            throw new MailingException("An error occurred whilst sending the mail");
        }
    }

    private void sendMessageUsingThymeleafTemplate(String to, String subject, String templateModel, Map<String, Object> variables, Language language) {
        LOGGER.info("Sending message with Thymeleaf Template to {}", to);

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(null);

        sendHtmlMessage(to, subject, variables, templateModel, language);
    }

    @Override
    @Async
    public void sendNewUserMail(long neighborhoodId, String userName, UserRole role) {
        LOGGER.info("Sending New User message to {}", userName);

        Map<String, Object> variables = new HashMap<>();
        // There is only one admin per neighborhood, maybe using size 1 could be better?
        List<User> admins = userDao.getUsers(neighborhoodId, (long) UserRole.ADMINISTRATOR.getId(), 1, 10);
        // This logic is scuffed
        Neighborhood neighborhood = neighborhoodDao.findNeighborhood(neighborhoodId).orElse(null);
        assert neighborhood != null;

        for (User admin : admins) {
            Locale locale = admin.getLanguage() == Language.ENGLISH? Locale.US : new Locale("es", "AR");

            variables.put("joinerOccupation", emailMessageSource.getMessage(role.toString().toLowerCase(), null, locale));
            variables.put("name", admin.getName());
            variables.put("joinerName", userName);
            variables.put("neighborhood", neighborhood.getName());
            variables.put("urlpath", BASE_URL + "admin/unverified");

            sendMessageUsingThymeleafTemplate(admin.getMail(), "subject.new.user", "newNeighbor-template_en.html", variables, admin.getLanguage());
        }

    }

    @Override
    @Async
    public void sendBatchEventMail(Event event, String customMessage, long neighborhoodId) {
        sendBatchEmails(neighborhoodId, (users) -> sendEventMail(event, customMessage, users));
    }

    @Override
    @Async
    public void sendEventMail(Event event, String customMessage, List<User> receivers) {
        for(User user : receivers) {
            boolean isEnglish = user.getLanguage() == Language.ENGLISH;
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getName());
            variables.put("message", emailMessageSource.getMessage(customMessage, null, isEnglish? Locale.US : new Locale("es", "AR")));
            variables.put("eventPath", BASE_URL + "events/" + event.getEventId());
            StringBuilder message = new StringBuilder("\n");
            message.append(event.getName())
                    .append("\n")
                    .append(event.getDescription())
                    .append("\n")
                    .append(event.getDate())
                    .append("\n")
                    .append(event.getStartTime())
                    .append(" - ")
                    .append(event.getEndTime())
                    .append("\n\n");
            variables.put("event", message);
            sendMessageUsingThymeleafTemplate(user.getMail(), "subject.event", "new-event-template_en.html", variables, user.getLanguage());
        }
    }

    @Override
    @Async
    @Scheduled(cron = "0 0 9 ? * MON") // CRON expression for weekly on Mondays at 8 PM
    public void sendWeeklyEventNotifications() {
        int page = 1;
        int size = 10;
        List<Neighborhood> neighborhoods;

        do {
            neighborhoods = neighborhoodDao.getNeighborhoods(null, null, null, page, size);

            // Fetch events for the specified neighborhood within the upcoming week
            Date currentDate = new Date(System.currentTimeMillis());

            // Calculate one day in milliseconds
            long oneDayInMillis = 24L * 60L * 60L * 1000L;

            List<Event> allEvents = new ArrayList<>();

            // Iterate through each day of the week
            for (int i = 0; i < 7; i++) {
                Date date = new Date(currentDate.getTime() + i * oneDayInMillis);

                for (Neighborhood neighborhood : neighborhoods) {
                    List<Event> dayEvents;
                    int eventPage = 1;
                    int eventSize = 10;
                    do {
                        // Fetch events for the specified day with pagination
                        dayEvents = eventDao.getEvents(neighborhood.getNeighborhoodId(), date, eventPage, eventSize);
                        allEvents.addAll(dayEvents);
                        eventPage++;
                    } while (dayEvents.size() == eventSize); // Continue fetching next page if the current page is full
                }
            }

            // Create a map to accumulate events for each subscribed user
            Map<User, Set<Event>> userEventsMap = new HashMap<>();

            // Iterate through the events and collect them for each subscribed user
            for (Event event : allEvents) {
                List<User> subscribedUsers = userDao.getEventUsers(event.getEventId());

                for (User user : subscribedUsers) {
                    userEventsMap
                            .computeIfAbsent(user, k -> new HashSet<>())
                            .add(event);
                }
            }

            // Iterate through users and send a single email with all their subscribed events
            for (Map.Entry<User, Set<Event>> entry : userEventsMap.entrySet()) {
                User user = entry.getKey();
                Set<Event> subscribedEvents = entry.getValue();
                Map<String, Object> variables = new HashMap<>();
                boolean isEnglish = user.getLanguage() == Language.ENGLISH;

                String to = user.getMail(); // Get the user's email
                StringBuilder message = new StringBuilder("\n");

                // Append event details for all subscribed events
                Locale locale = isEnglish? Locale.US : new Locale("es", "AR");
                variables.put("time", emailMessageSource.getMessage("event.time", null, locale));
                for (Event event : subscribedEvents) {
                    message.append(event.getName())
                            .append("\n")
                            .append(emailMessageSource.getMessage("event.details", null, locale))
                            .append(" ")
                            .append(event.getDescription())
                            .append(" ")
                            .append(emailMessageSource.getMessage("event.on", null, locale))
                            .append(" ")
                            .append(event.getDate())
                            .append("\n\n");
                }

                variables.put("events", message.toString());
                variables.put("name", user.getName());
                variables.put("eventsPath", BASE_URL + "calendar");

                sendMessageUsingThymeleafTemplate(to, "subject.upcoming.events", "events-template_en.html", variables, user.getLanguage());
            }
            page++;
        } while (neighborhoods.size() == size);
    }

    @Override
    @Async
    @Scheduled(cron = "0 0 9 ? * *") // CRON expression for weekly on Mondays at 8 PM
    public void sendDailyEventNotifications() {
        int page = 1;
        int size = 10;
        List<Neighborhood> neighborhoods;

        do {
            neighborhoods = neighborhoodDao.getNeighborhoods(null, null, null, page, size);

            // Calculate the date for the next day
            Date today = new Date(System.currentTimeMillis());
            long oneDayInMillis = 24L * 60L * 60L * 1000L; // One day in milliseconds
            Date nextDay = new Date(today.getTime() + oneDayInMillis);

            List<Event> allEvents = new ArrayList<>();
            List<Event> events;

            for (Neighborhood neighborhood : neighborhoods) {
                int eventPage = 1;
                int eventSize = 10;
                do {
                    // Fetch events for the next day with pagination
                    events = eventDao.getEvents(neighborhood.getNeighborhoodId(), nextDay, eventPage, eventSize);
                    allEvents.addAll(events);
                    eventPage++;
                } while (events.size() == eventSize); // Continue fetching next page if the current page is full
            }

            // Create a map to accumulate events for each subscribed user
            Map<User, Set<Event>> userEventsMap = new HashMap<>();

            // Iterate through the events and collect them for each subscribed user
            for (Event event : allEvents) {
                Set<User> attendees = event.getAttendees();

                for (User user : attendees) {
                    userEventsMap
                            .computeIfAbsent(user, k -> new HashSet<>())
                            .add(event);
                }
            }

            // Iterate through users and send a single email with all their subscribed events
            for (Map.Entry<User, Set<Event>> entry : userEventsMap.entrySet()) {
                User user = entry.getKey();
                Set<Event> subscribedEvents = entry.getValue();
                Map<String, Object> variables = new HashMap<>();
                boolean isEnglish = user.getLanguage() == Language.ENGLISH;

                StringBuilder message = new StringBuilder("\n");

                Locale locale = isEnglish? Locale.US : new Locale("es", "AR");
                variables.put("time", emailMessageSource.getMessage("event.tomorrow.time", null, locale));
                for (Event event : subscribedEvents) {
                    message.append(event.getName())
                            .append("\n")
                            .append(emailMessageSource.getMessage("event.details", null, locale))
                            .append(" ")
                            .append(event.getDescription())
                            .append(" ")
                            .append(emailMessageSource.getMessage("from", null, locale))
                            .append(event.getStartTime())
                            .append(" ")
                            .append(emailMessageSource.getMessage("to", null, locale))
                            .append(" ")
                            .append(event.getEndTime())
                            .append("\n\n");
                }

                variables.put("name", user.getName());
                variables.put("eventsPath", BASE_URL + "calendar");
                variables.put("events", message.toString());

                sendMessageUsingThymeleafTemplate(user.getMail(), "subject.tomorrows.events", "events-template_en.html", variables, user.getLanguage());
            }
            page++;
        } while (neighborhoods.size() == size);
    }

    @Override
    @Async
    public void sendBatchNewAmenityMail(long neighborhoodId, String amenityName, String amenityDescription) {
        sendBatchEmails(neighborhoodId, (users) -> sendNewAmenityMail(neighborhoodId, amenityName, amenityDescription, users));
    }

    @Override
    @Async
    public void sendNewAmenityMail(long neighborhoodId, String amenityName, String amenityDescription, List<User> receivers) {
        for(User user : receivers) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getName());
            variables.put("amenityPath", BASE_URL + "events/reservations");
            variables.put("amenityName", amenityName);
            variables.put("amenityDescription", amenityDescription);
            sendMessageUsingThymeleafTemplate(user.getMail(), "subject.new.amenity", "new-amenity-template_en.html", variables, user.getLanguage());
        }
    }

    @Override
    @Async
    public void sendBatchAnnouncementMail(Post post, long neighborhoodId) {
        sendBatchEmails(neighborhoodId, (users) -> sendAnnouncementMail(post, users));
    }

    @Override
    @Async
    public void sendAnnouncementMail(Post post, List<User> receivers) {
        for (User n : receivers) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", n.getName());
            vars.put("postTitle", post.getTitle());
            vars.put("postPath", BASE_URL + "posts/" + post.getPostId());
            sendMessageUsingThymeleafTemplate(n.getMail(), "subject.new.announcement", "announcement-template_en.html", vars, n.getLanguage());
        }
    }

    @Override
    @Async
    public void sendInquiryMail(User receiver, Product product, String message, boolean reply) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", receiver.getName());
        variables.put("productName", product.getName());
        variables.put("message", message);
        variables.put("productPath", BASE_URL + "marketplace/" + product.getProductId());
        String customMessage = reply? "inquiry.replied" : "inquiry.new";
        String replyOrMessage = reply? "inquiry.the.reply" : "inquiry.the.message";
        Locale locale = receiver.getLanguage() == Language.ENGLISH? Locale.US : new Locale("es", "AR");

        variables.put("customMessage", emailMessageSource.getMessage(customMessage, null, locale));
        variables.put("replyOrMessage", emailMessageSource.getMessage(replyOrMessage, null, locale));
        sendMessageUsingThymeleafTemplate(receiver.getMail(), reply ? "subject.reply.inquiry" : "subject.new.inquiry", "inquiry-template_en.html", variables, receiver.getLanguage());

    }

    @Override
    @Async
    public void sendNewRequestMail(Product product, User sender, String message) {
        User receiver = product.getSeller();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", receiver.getName());
        variables.put("productName", product.getName());
        variables.put("senderName", sender.getName());
        variables.put("senderSurname", sender.getSurname());
        variables.put("message", message);
        variables.put("productPath", BASE_URL + "marketplace/" + product.getProductId());
        sendMessageUsingThymeleafTemplate(receiver.getMail(), "subject.new.request", "request-template_en.html", variables, receiver.getLanguage());
    }

    @Override
    @Async
    public void sendVerifiedNeighborMail(User user) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("neighborhood", user.getNeighborhood().getName());
        vars.put("loginPath", BASE_URL);
        sendMessageUsingThymeleafTemplate(user.getMail(), "subject.verification", "verification-template_en.html", vars, user.getLanguage());
    }

    private void sendBatchEmails(long neighborhoodId, Consumer<List<User>> emailSender) {
        int page = DEFAULT_PAGE;
        int size = DEFAULT_SIZE;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        List<User> users;
        do {
            users = userDao.getUsers(neighborhoodId, (long) UserRole.NEIGHBOR.getId(), page, size);

            if (!users.isEmpty()) {
                // Create a final copy of users to use inside the lambda
                final List<User> batch = new ArrayList<>(users);

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> emailSender.accept(batch));
                futures.add(future);
            }
            page++;
        } while (users.size() == size);

        // Wait for all batches to complete asynchronously
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}