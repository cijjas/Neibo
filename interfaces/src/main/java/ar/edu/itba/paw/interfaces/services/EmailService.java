package ar.edu.itba.paw.interfaces.services;


import ar.edu.itba.paw.enums.UserRole;

import java.util.Map;

public interface EmailService {

    public void sendSimpleMessage(String to, String subject, String text);

    public void sendHtmlMessage(String to, String subject, Map<String, Object> variables, String template);

    public void sendMessageUsingThymeleafTemplate(String to, String subject, String templateModel, Map<String, Object> variables);

    public void sendNewUserMail(long neighborhoodId, String userName, UserRole role);

    public void sendWeeklyEventNotifications();

    }
