package com.spiro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public final class MailService {

    private MailService() {
    }

    public static void sendMail() {
        final String to = "";
        final String from = "integration.test.runner@gmail.com";
        final String username = from.split("@")[0];
        final String pwd = "";

        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, pwd);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Energy plan test report");

            String content = parseReportToString();
            message.setContent(content, "text/html");
            System.out.println("sending...");
            Transport.send(message);

            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String parseReportToString() throws IOException {
        final String REPORT = "target/surefire-reports/emailable-report.html";

        return new String(Files.readAllBytes(Path.of(REPORT)));
    }
}
