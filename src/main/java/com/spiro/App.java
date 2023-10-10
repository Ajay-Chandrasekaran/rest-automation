package com.spiro;

/**
 * Test REST Endpoints
 */
public class App {
    public static void main(String[] args) {
        /*
         * Execute from exec-maven plugin for emailing test report
         */
        MailService.sendMail();
    }
}
