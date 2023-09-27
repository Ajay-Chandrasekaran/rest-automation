package com.spiro;

import java.util.Properties;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class CreateEmail {

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param toAddress   email address of the receiver
     * @param fromAdress email address of the sender, the mailbox account
     * @param subject          subject of the email
     * @param bodyText         body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException - if a wrongly formatted address is encountered.
     */
    public static MimeMessage createEmail(
            String toAddress,
            String fromAdress,
            String subject,
            String bodyText) throws MessagingException {
        Properties prop = new Properties();
        Session session = Session.getDefaultInstance(prop, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(fromAdress));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toAddress));
        email.setSubject(subject);
        email.setText(bodyText);

        return email;
    }
}
