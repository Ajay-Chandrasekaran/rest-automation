package com.spiro;

import java.io.IOException;
import java.security.GeneralSecurityException;
import jakarta.mail.MessagingException;

/**
 * Emails the sure fire report generated
 */
public class App {
    public static void main(String[] args) throws IOException, MessagingException, GeneralSecurityException {
        String fromAddress = "integration.test.runner@gmail.com";
        String toAddress = "ajaychandrasekaran99@gmail.com";

        SendMessage.sendMessage(fromAddress, toAddress);
    }
}
