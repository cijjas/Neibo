package ar.edu.itba.paw.services.email;

import ar.edu.itba.paw.interfaces.exceptions.MailingException;
import ar.edu.itba.paw.interfaces.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

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
        }
        catch (MessagingException e) {
            LOGGER.error("Error whilst sending HTML message to {}", to);
            throw new MailingException("An error occurred whilst sending the mail");
        }
    }

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Override
    @Async
    public void sendMessageUsingThymeleafTemplate(String to, String subject, String templateModel, Map<String, Object> variables) {
        LOGGER.info("Sending message with Thymeleaf Template to {}", to);

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(null);
        String htmlBody = thymeleafTemplateEngine.process(templateModel, thymeleafContext);

        sendHtmlMessage(to, subject, variables, templateModel);
    }
}