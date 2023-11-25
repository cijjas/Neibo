package ar.edu.itba.paw.interfaces.services;


import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.MainEntities.Event;
import ar.edu.itba.paw.models.MainEntities.Post;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;

import java.util.List;
import java.util.Map;

public interface EmailService {

    void sendSimpleMessage(String to, String subject, String text);

    void sendHtmlMessage(String to, String subject, Map<String, Object> variables, String template);

    void sendMessageUsingThymeleafTemplate(String to, String subject, String templateModel, Map<String, Object> variables);

    void sendNewUserMail(long neighborhoodId, String userName, UserRole role);

    void sendEventMail(Event event, String message_en, String message_es, List<User> receivers);

    void sendWeeklyEventNotifications();

    void sendDailyEventNotifications();

    void sendNewAmenityMail(long neighborhoodId, String amenityName, String amenityDescription, List<User> receivers);

    void sendAnnouncementMail(Post post, List<User> receivers);

    void sendNewCommentMail(Post post, List<User> receivers);

    void sendInquiryMail(User receiver, Product product, String message, boolean reply);

    void sendNewRequestMail(Product product, User sender, String message);

    void sendVerifiedNeighborMail(User user, String neighborhoodName);

    }
