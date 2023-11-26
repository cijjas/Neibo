package ar.edu.itba.paw.interfaces.services;


import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.MainEntities.Event;
import ar.edu.itba.paw.models.MainEntities.Post;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;

import java.util.List;
import java.util.Map;

public interface EmailService {

    public void sendNewUserMail(long neighborhoodId, String userName, UserRole role);

    public void sendEventMail(Event event, String customMessage, List<User> receivers);

    public void sendWeeklyEventNotifications();

    public void sendDailyEventNotifications();

    public void sendNewAmenityMail(long neighborhoodId, String amenityName, String amenityDescription, List<User> receivers);

    public void sendAnnouncementMail(Post post, List<User> receivers);

    public void sendNewCommentMail(Post post, List<User> receivers);

    public void sendInquiryMail(User receiver, Product product, String message, boolean reply);

    public void sendNewRequestMail(Product product, User sender, String message);

    public void sendVerifiedNeighborMail(User user, String neighborhoodName);

    }
