package ar.edu.itba.paw.services.email;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.exceptions.MailingException;
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
import java.sql.Date;
import java.util.*;

@EnableScheduling
@Component
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
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
        String htmlBody = thymeleafTemplateEngine.process(templateModel, thymeleafContext);

        sendHtmlMessage(to, subject, variables, templateModel, language);
    }

    @Override
    @Async
    public void sendNewUserMail(long neighborhoodId, String userName, UserRole role) {
        LOGGER.info("Sending New User message to {}", userName);

        Map<String, Object> variables = new HashMap<>();
        List<User> admins = userDao.getUsersByCriteria(UserRole.ADMINISTRATOR, neighborhoodId, 1, 10);
        Neighborhood neighborhood = neighborhoodDao.findNeighborhoodById(neighborhoodId).orElse(null);
        assert neighborhood != null;

        for (User admin : admins) {
            Locale locale = admin.getLanguage() == Language.ENGLISH? Locale.US : new Locale("es", "AR");

            variables.put("joinerOccupation", emailMessageSource.getMessage(role.toString().toLowerCase(), null, locale));
            variables.put("name", admin.getName());
            variables.put("joinerName", userName);
            variables.put("neighborhood", neighborhood.getName());
            variables.put("urlpath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/admin/unverified");

            sendMessageUsingThymeleafTemplate(admin.getMail(), "subject.new.user", "newNeighbor-template_en.html", variables, admin.getLanguage());
        }

    }

    @Override
    @Async
    public void sendEventMail(Event event, String customMessage, List<User> receivers) {
        for(User user : receivers) {
            boolean isEnglish = user.getLanguage() == Language.ENGLISH;
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getName());
            variables.put("message", emailMessageSource.getMessage(customMessage, null, isEnglish? Locale.US : new Locale("es", "AR")));
            variables.put("eventPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/events/" + event.getEventId());
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
        // Fetch the list of neighborhoods
        List<Neighborhood> neighborhoods = neighborhoodDao.getNeighborhoods();

        for (Neighborhood neighborhood : neighborhoods) {
            long neighborhoodId = neighborhood.getNeighborhoodId();

            // Fetch events for the specified neighborhood within the upcoming week
            Date startDate = new Date(System.currentTimeMillis());
            long oneWeekInMillis = 7L * 24L * 60L * 60L * 1000L; // One week in milliseconds
            Date endDate = new Date(startDate.getTime() + oneWeekInMillis);

            List<Event> events = eventDao.getEventsByNeighborhoodIdAndDateRange(neighborhoodId, startDate, endDate);

            // Create a map to accumulate events for each subscribed user
            Map<User, Set<Event>> userEventsMap = new HashMap<>();

            // Iterate through the events and collect them for each subscribed user
            for (Event event : events) {
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
                String subject;
                String template;
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
                variables.put("eventsPath", "pawserver.it.itba.edu.ar/paw-2023b-02/calendar");

                sendMessageUsingThymeleafTemplate(to, "subject.upcoming.events", "events-template_en.html", variables, user.getLanguage());

            }
        }
    }

    @Override
    @Async
    @Scheduled(cron = "0 0 9 ? * *") // CRON expression for weekly on Mondays at 8 PM
    public void sendDailyEventNotifications() {
        // Fetch the list of neighborhoods
        List<Neighborhood> neighborhoods = neighborhoodDao.getNeighborhoods();

        for (Neighborhood neighborhood : neighborhoods) {
            long neighborhoodId = neighborhood.getNeighborhoodId();

            // Calculate the date for the next day
            Date today = new Date(System.currentTimeMillis());
            long oneDayInMillis = 24L * 60L * 60L * 1000L; // One day in milliseconds
            Date nextDay = new Date(today.getTime() + oneDayInMillis);

            // Fetch events for the specified neighborhood happening in the next day
            List<Event> events = eventDao.getEventsByNeighborhoodIdAndDateRange(neighborhoodId, nextDay, nextDay);

            // Create a map to accumulate events for each subscribed user
            Map<User, Set<Event>> userEventsMap = new HashMap<>();

            // Iterate through the events and collect them for each subscribed user
            for (Event event : events) {
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
                variables.put("eventsPath", "pawserver.it.itba.edu.ar/paw-2023b-02/calendar");
                variables.put("events", message.toString());

                sendMessageUsingThymeleafTemplate(user.getMail(), "subject.tomorrows.events", "events-template_en.html", variables, user.getLanguage());
            }
        }
    }

    @Override
    @Async
    public void sendNewAmenityMail(long neighborhoodId, String amenityName, String amenityDescription, List<User> receivers) {
        for(User user : receivers) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getName());
            variables.put("amenityPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/events/reservations");
            variables.put("amenityName", amenityName);
            variables.put("amenityDescription", amenityDescription);
            sendMessageUsingThymeleafTemplate(user.getMail(), "subject.new.amenity", "new-amenity-template_en.html", variables, user.getLanguage());
        }
    }

    @Override
    @Async
    public void sendAnnouncementMail(Post post, List<User> receivers) {
        for (User n : receivers) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", n.getName());
            vars.put("postTitle", post.getTitle());
            vars.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
            sendMessageUsingThymeleafTemplate(n.getMail(), "subject.new.announcement", "announcement-template_en.html", vars, n.getLanguage());
        }
    }

    @Override
    @Async
    public void sendNewCommentMail(Post post, List<User> receivers) {
        User user = post.getUser();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("postTitle", post.getTitle());
        variables.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
        sendMessageUsingThymeleafTemplate(user.getMail(), "subject.new.comment", "comment-template_en.html", variables, user.getLanguage());

        for (User n : receivers) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", n.getName());
            vars.put("postTitle", post.getTitle());
            vars.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
            sendMessageUsingThymeleafTemplate(n.getMail(), "subject.new.comment", "comment-template_en.html", vars, n.getLanguage());
        }
    }

    @Override
    @Async
    public void sendInquiryMail(User receiver, Product product, String message, boolean reply) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", receiver.getName());
        variables.put("productName", product.getName());
        variables.put("message", message);
        variables.put("productPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/marketplace/" + product.getProductId());
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
        variables.put("productPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/marketplace/" + product.getProductId());
        sendMessageUsingThymeleafTemplate(receiver.getMail(), "subject.new.request", "request-template_en.html", variables, receiver.getLanguage());
    }

    @Override
    @Async
    public void sendVerifiedNeighborMail(User user, String neighborhoodName) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("neighborhood", neighborhoodName);
        vars.put("loginPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/");
        sendMessageUsingThymeleafTemplate(user.getMail(), "subject.verification", "verification-template_en.html", vars, user.getLanguage());
    }
}