package ar.edu.itba.paw.interfaces.services;


import java.util.Map;

public interface EmailService {
    public void sendSimpleMessage(String to, String subject, String text);

    public void sendHtmlMessage(String to, String subject, Map<String, Object> variables, String template) ;

    public void sendMessageUsingThymeleafTemplate(String to, String subject, String templateModel, Map<String, Object> variables) ;
}
