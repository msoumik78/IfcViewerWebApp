package util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

public class MailSendUtilUsingGmail {

    public static String gmailAdminUsername = null;
    public static String gmailAdminPassword = null;

    static {
        try (InputStream input = MailSendUtilUsingGmail.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
            }
            prop.load(input);
            gmailAdminUsername = prop.getProperty("gmail.admin.username");
            gmailAdminPassword = prop.getProperty("gmail.admin.password");

        } catch (Exception ex) {
            System.out.println("!!!!!!!!!!!Exception encountered while loading application.propeerties !!!!!!!!!!!!!!!  :" + ex.getCause());
        }
    }

    public static void sendMail(String fromEmail, String toEmail, String subject, String body){
        Session session = initialize();
        sendMail(session, fromEmail, toEmail, subject, body);
    }

    private static void sendMail(Session session, String fromEmail, String toEmail, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static Session initialize() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        return Session.getInstance(prop,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(gmailAdminUsername, gmailAdminPassword);
                    }
                });
    }
}
