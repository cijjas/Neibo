package ar.edu.itba.paw.interfaces.services;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

import java.util.Map;

public interface EmailService {

    public void sendSimpleMessage(String to, String subject, String text);
    public void sendHtmlMessage(String to, String subject, Map<String, Object> variables) throws MessagingException;
    public void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String, Object> templateModel, Map<String, Object> variables) throws MessagingException;

    }
