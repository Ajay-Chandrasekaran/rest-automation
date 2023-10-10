package com.spiro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


public final class MailService {

    private static final Logger logger = LogManager.getLogger();

    private MailService() {
    }

    public static void sendMail() {
        final String to = "wow";
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
        logger.info("Seding email to {}", to);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Energy plan test report");

            String content = parseReportToString();
            message.setContent(content, "text/html");
            logger.info("Email message created.");

            Transport.send(message);
            logger.info("Sent message successfully.");
        } catch (MessagingException mex) {
            logger.error(mex.getMessage(), mex);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static String parseReportToString() throws IOException {
        final String REPORT = "target/surefire-reports/emailable-report.html";

        return new String(Files.readAllBytes(Path.of(REPORT)));
    }
}
