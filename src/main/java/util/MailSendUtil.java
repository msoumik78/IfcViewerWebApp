package util;

public class MailSendUtil {
    public static void sendMail(String emailProvider, String fromEmail, String toMail, String mailSubject, String mailBody){
        MailSendUtilUsingGmail.sendMail(fromEmail,toMail,mailSubject, mailBody);
    }
}
