package ar.edu.itba.paw.interfaces.services;

public interface EmailService {

    public void sendSimpleMessage(String to, String subject, String text);

}
