package ar.edu.itba.paw.interfaces.services;


import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.User;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface EmailService {

    void sendNewUserMail(long neighborhoodId, String userName, UserRole role);

    void sendEventMail(Event event, String customMessage, List<User> receivers);

    @Async
    void sendBatchAnnouncementMail(Post post, long neighborhoodId);

    void sendBatchEventMail(Event event, String customMessage, long neighborhoodId);

    void sendBatchNewAmenityMail(long neighborhoodId, String amenityName, String amenityDescription);

    void sendWeeklyEventNotifications();

    void sendDailyEventNotifications();

    void sendNewAmenityMail(long neighborhoodId, String amenityName, String amenityDescription, List<User> receivers);

    void sendAnnouncementMail(Post post, List<User> receivers);

    void sendInquiryMail(User receiver, Product product, String message, boolean reply);

    void sendNewRequestMail(Product product, User sender, String message);

    void sendVerifiedNeighborMail(User user);
}
