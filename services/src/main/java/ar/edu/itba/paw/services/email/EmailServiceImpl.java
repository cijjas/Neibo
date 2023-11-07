package ar.edu.itba.paw.services.email;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.exceptions.MailingException;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.MainEntities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
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

import java.sql.Date;

@EnableScheduling
@Component
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final UserDao userDao;
    private final NeighborhoodDao neighborhoodDao;
    private final EventDao eventDao;

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

    @Override
    @Async
    public void sendSimpleMessage(
            String to, String subject, String text) {
        LOGGER.info("Sending simple message to {}", to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("neibonotifs@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    @Async
    //Can add a third parameter to receive a specific template
    public void sendHtmlMessage(String to, String subject, Map<String, Object> variables, String templateModel) {
        LOGGER.info("Sending HTML message to {}", to);
        try {
            final Context context = new Context();

            context.setVariables(variables);

            final String htmlContext = this.thymeleafTemplateEngine.process(templateModel, context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContext, true);
            emailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Error whilst sending HTML message to {}", to);
            throw new MailingException("An error occurred whilst sending the mail");
        }
    }

    @Override
    @Async
    public void sendMessageUsingThymeleafTemplate(String to, String subject, String templateModel, Map<String, Object> variables) {
        LOGGER.info("Sending message with Thymeleaf Template to {}", to);

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(null);
        String htmlBody = thymeleafTemplateEngine.process(templateModel, thymeleafContext);

        sendHtmlMessage(to, subject, variables, templateModel);
    }

    @Override
    public void sendNewUserMail(long neighborhoodId, String userName, UserRole role) {
        LOGGER.info("Sending New User message to {}", userName);

        Map<String, Object> variables = new HashMap<>();
        List<User> admins = userDao.getUsersByCriteria(UserRole.ADMINISTRATOR, neighborhoodId, 1, 10);
        Neighborhood neighborhood = neighborhoodDao.findNeighborhoodById(neighborhoodId).orElse(null);
        assert neighborhood != null;

        for (User admin : admins) {
            boolean isEnglish = admin.getLanguage() == Language.ENGLISH;

            String joinerType = (role == UserRole.NEIGHBOR) ? "neighbor" : "worker";

            if (!isEnglish) {
                joinerType = (joinerType.equals("neighbor")) ? "vecino" : "trabajador";
            }

            variables.put("joinerOccupation", joinerType);
            variables.put("name", admin.getName());
            variables.put("joinerName", userName);
            variables.put("neighborhood", neighborhood.getName());
            variables.put("urlpath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/admin/unverified");

            sendMessageUsingThymeleafTemplate(admin.getMail(), isEnglish ? "New User" : "Nuevo Usuario", isEnglish ? "newNeighbor-template_en.html" : "newNeighbor-template_es.html", variables);
        }

    }

    @Override
    public void sendEventMail(Event event, String message_en, String message_es, List<User> receivers) {
        for(User user : receivers) {
            boolean isEnglish = user.getLanguage() == Language.ENGLISH;
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getName());
            variables.put("message", isEnglish? message_en : message_es);
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
            sendMessageUsingThymeleafTemplate(user.getMail(), isEnglish ? "Event" : "Evento", isEnglish ? "new-event-template_en.html" : "new-event-template_es.html", variables);
        }
    }

    @Override
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
                if(isEnglish) {
                    subject = "Upcoming Events on " + neighborhood.getName();
                    variables.put("time","next week!");
                    template = "events-template_en.html";

                    for (Event event : subscribedEvents) {
                        message.append(event.getName())
                                .append("\n")
                                .append("Event details: ")
                                .append(event.getDescription())
                                .append(" on ")
                                .append(event.getDate())
                                .append("\n\n");
                    }
                } else {
                    subject = "Próximos Eventos en " + neighborhood.getName();
                    variables.put("time", "esta semana!");
                    template = "events-template_es.html";

                    for (Event event : subscribedEvents) {
                        message.append(event.getName())
                                .append("\n")
                                .append("Detalles del evento: ")
                                .append(event.getDescription())
                                .append(" en ")
                                .append(event.getDate())
                                .append("\n\n");
                    }
                }

                variables.put("events", message.toString());
                variables.put("name", user.getName());
                variables.put("eventsPath", "pawserver.it.itba.edu.ar/paw-2023b-02/calendar");

                sendMessageUsingThymeleafTemplate(to, subject, template, variables);
            }
        }
    }

    @Override
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

                String to = user.getMail(); // Get the user's email
                String subject;
                String template;
                StringBuilder message = new StringBuilder("\n");

                if(isEnglish) {
                    subject = "Tomorrow's Events in " + neighborhood.getName();
                    variables.put("time", "tomorrow!");
                    template = "events-template_en.html";

                    for (Event event : subscribedEvents) {
                        message.append(event.getName())
                                .append("\n")
                                .append("Event details: ")
                                .append(event.getDescription())
                                .append(" from ")
                                .append(event.getStartTime())
                                .append(" to ")
                                .append(event.getEndTime())
                                .append("\n\n");
                    }
                } else {
                    subject = "Los Eventos de Mañana en " + neighborhood.getName();
                    variables.put("time", "mañana!");
                    template = "events-template_es.html";

                    for (Event event : subscribedEvents) {
                        message.append(event.getName())
                                .append("\n")
                                .append("Detalles del evento: ")
                                .append(event.getDescription())
                                .append(" desde ")
                                .append(event.getStartTime())
                                .append(" hasta ")
                                .append(event.getEndTime())
                                .append("\n\n");
                    }
                }
                variables.put("name", user.getName());
                variables.put("eventsPath", "pawserver.it.itba.edu.ar/paw-2023b-02/calendar");
                variables.put("events", message.toString());

                sendMessageUsingThymeleafTemplate(to, subject, template, variables);
            }
        }
    }

    @Override
    public void sendNewAmenityMail(long neighborhoodId, String amenityName, String amenityDescription, List<User> receivers) {
        for(User user : receivers) {
            boolean isEnglish = user.getLanguage() == Language.ENGLISH;
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getName());
            variables.put("amenityPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/events/reservations");
            variables.put("amenityName", amenityName);
            variables.put("amenityDescription", amenityDescription);
            sendMessageUsingThymeleafTemplate(user.getMail(), isEnglish ? "New Amenity" : "Nueva Amenidad", isEnglish ? "new-amenity-template_en.html" : "new-amenity-template_es.html", variables);
        }
    }

    @Override
    public void sendAnnouncementMail(Post post, List<User> receivers) {
        for (User n : receivers) {
            boolean isEnglish = n.getLanguage() == Language.ENGLISH;
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", n.getName());
            vars.put("postTitle", post.getTitle());
            vars.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
            sendMessageUsingThymeleafTemplate(n.getMail(), isEnglish ? "New Announcement" : "Nuevo Anuncio", isEnglish ? "announcement-template_en.html" : "announcement-template_es.html", vars);
        }
    }

    @Override
    public void sendNewCommentMail(Post post, List<User> receivers) {
        User user = post.getUser();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("postTitle", post.getTitle());
        variables.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
        boolean isEnglish = user.getLanguage() == Language.ENGLISH;
        sendMessageUsingThymeleafTemplate(user.getMail(), isEnglish ? "New comment" : "Nuevo Comentario", isEnglish ? "comment-template_en.html" : "comment-template_es.html", variables);

        for (User n : receivers) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", n.getName());
            vars.put("postTitle", post.getTitle());
            vars.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
            sendMessageUsingThymeleafTemplate(user.getMail(), isEnglish ? "New comment" : "Nuevo Comentario", isEnglish ? "comment-template_en.html" : "comment-template_es.html", vars);
        }
    }

    @Override
    public void sendInquiryMail(User receiver, Product product, String message, boolean reply) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", receiver.getName());
        variables.put("productName", product.getName());
        variables.put("message", message);
        variables.put("productPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/marketplace/" + product.getProductId());
        boolean isEnglish = receiver.getLanguage() == Language.ENGLISH;
        if(isEnglish) {
            variables.put("customMessage", reply ? "Your inquiry has been replied " : "You have a new inquiry ");
            variables.put("replyOrMessage", reply ? "The reply: " : "The message: ");
            sendMessageUsingThymeleafTemplate(receiver.getMail(), reply ? "Response to Inquiry" : "New Inquiry", "inquiry-template_en.html", variables);
        } else {
            variables.put("customMessage", reply ? "Has recibido una respuesta a tu consulta " : "Tienes una nueva consulta ");
            variables.put("replyOrMessage", reply ? "La respuesta: " : "El mensaje: ");
            sendMessageUsingThymeleafTemplate(receiver.getMail(), reply ? "Respuesta a Consulta" : "Nueva Consulta", "inquiry-template_es.html", variables);
        }
    }

    @Override
    public void sendNewRequestMail(Product product, User sender, String message) {
        User receiver = product.getSeller();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", receiver.getName());
        variables.put("productName", product.getName());
        variables.put("senderName", sender.getName());
        variables.put("senderSurname", sender.getSurname());
        variables.put("message", message);
        variables.put("productPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/marketplace/" + product.getProductId());
        boolean isEnglish = receiver.getLanguage() == Language.ENGLISH;
        sendMessageUsingThymeleafTemplate(receiver.getMail(), isEnglish ? "New Request" : "Nueva Solicitud", isEnglish ? "request-template_en.html" : "request-template_es.html", variables);
    }

    @Override
    public void sendVerifiedNeighborMail(User user, String neighborhoodName) {
        boolean isEnglish = user.getLanguage() == Language.ENGLISH;
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("neighborhood", neighborhoodName);
        vars.put("loginPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/");
        sendMessageUsingThymeleafTemplate(user.getMail(), isEnglish ? "Verification" : "Verificación", isEnglish? "verification-template_en.html" : "verification-template_es.html", vars);
    }
}