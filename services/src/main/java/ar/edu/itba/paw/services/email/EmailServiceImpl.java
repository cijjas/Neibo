package ar.edu.itba.paw.services.email;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.exceptions.MailingException;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.User;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Scheduled(cron = "0 00 09 ? * MON") // CRON expression for weekly on Mondays at 9 AM
    public void sendWeeklyEventNotifications() {
        // Fetch the list of neighborhoods
        List<Neighborhood> neighborhoods = neighborhoodDao.getNeighborhoods();

        // Iterate through each neighborhood
        for (Neighborhood neighborhood : neighborhoods) {
            long neighborhoodId = neighborhood.getNeighborhoodId();

            // Fetch events for the specified neighborhood within the upcoming week
            Date startDate = new Date(System.currentTimeMillis());
            long oneWeekInMillis = 7L * 24L * 60L * 60L * 1000L; // One week in milliseconds
            Date endDate = new Date(startDate.getTime() + oneWeekInMillis);

            List<Event> events = eventDao.getEventsByNeighborhoodIdAndDateRange(neighborhoodId, startDate, endDate);

            // Iterate through the events and send notifications to subscribed users
            for (Event event : events) {
                List<User> subscribedUsers = userDao.getEventUsers(event.getEventId());

                for (User user : subscribedUsers) {
                    String to = user.getMail(); // Get the user's email
                    String subject = "Upcoming Event Notification";
                    String message = "Hey " + user.getName() + " " + user.getSurname() + "you are subscribed to an upcoming event in " + neighborhood.getName() + "!\n"
                            + "Event details: " + event.getDescription() + " on " + event.getDate();

                    // Send the email using your email service (e.g., emailService.sendSimpleMessage)
                    sendSimpleMessage(to, subject, message);
                }
            }
        }
    }


}