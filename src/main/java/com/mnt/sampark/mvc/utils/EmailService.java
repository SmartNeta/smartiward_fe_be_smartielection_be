/**
 *
 */
package com.mnt.sampark.mvc.utils;

import java.io.File;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.mnt.sampark.core.db.model.User;
import com.mnt.sampark.core.db.model.UserDetail;
import com.mnt.sampark.modules.mdm.db.domain.Citizen;
import com.mnt.sampark.modules.mdm.db.domain.Complaint;
import org.springframework.web.multipart.MultipartFile;

@Component
@Aspect
public class EmailService {

    private static final String username = "contact@smartneta.com";
    private static final String password = "SmartNeta123#";
    private static final String host = "mail.smartneta.com";
    private static final String port = "587";
    private static final Properties properties = System.getProperties();
    private static Session session = null;

    public static class SmtpAuthenticator extends Authenticator {

        public SmtpAuthenticator() {
            super();
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }

    static {
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.port", port);
        session = Session.getInstance(properties, new SmtpAuthenticator());
    }

    public void sendComplaintNotificationEmail(String loginUrl, Complaint complaint, File file) {
        UserDetail userDetail = complaint.getUser();
        final MimeMessage mimeMessage = new MimeMessage(session);
        try {
            Citizen citizen = complaint.getCitizen();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            String htmlContent = "<html><body> <center><h1> SamrtiWard </h1><h3> Welcome to SamrtiWard </h3></center>"
                    + " <p><b>Hello, " + userDetail.getFirstName() + " " + userDetail.getLastName() + " " + "</b></p> "
                    + " <p>New complaint raised. Please kindly check it.</p>"
                    + (citizen == null ? "" : " <p><b>Citizen Name:</b> " + citizen.getFirstName() + " " + citizen.getFamilyName() + "</p>")
                    + " <p><b>Complaint Number: </b> " + complaint.getIncidentId() + "</p>"
                    + " <p><b>Complaint Text:</b> " + complaint.getComplaint() + "</p>"
                    + "<br/><p>Please <a href=" + loginUrl + "> click here </a> to login </p> </body></html>";
            helper.setText(htmlContent, true);
            helper.setTo(userDetail.getEmail());
            helper.setFrom(new InternetAddress(username));
            helper.setSubject("SamrtiWard - Complaint");
            if (Objects.nonNull(file)) {
                helper.addAttachment(file.getName(), file);
            }
            new Thread(() -> {
                try {
                    Transport.send(mimeMessage);
                    System.err.println("\nMail send to :: " + userDetail.getEmail());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            String msg = "New complaint raised " + complaint.getIncidentId();
            SMSService.send(userDetail.getPhone(), msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendComplaintUpdatedNotificationEmail(String loginUrl, Complaint complaint, File file) {
        UserDetail userDetail = complaint.getUser();
        final MimeMessage mimeMessage = new MimeMessage(session);
        try {
            Citizen citizen = complaint.getCitizen();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            String htmlContent = "<html><body> <center><h1> SamrtiWard </h1><h3> Welcome to SamrtiWard </h3></center>"
                    + " <p><b>Hello, " + userDetail.getFirstName() + " " + userDetail.getLastName() + " " + "</b></p> "
                    + " <p>Complaint " + complaint.getIncidentId() + " updated. Please kindly check it.</p>"
                    + (citizen == null ? "" : " <p><b>Citizen Name:</b> " + citizen.getFirstName() + " " + citizen.getFamilyName() + "</p>")
                    + " <p><b>Complaint Status:</b> " + complaint.getStatus() + "</p>"
                    + " <p><b>Complaint Text:</b> " + complaint.getComplaint() + "</p>"
                    + " <br/><p>Please <a href=" + loginUrl + "> click here </a> to login </p> </body></html>";
            helper.setText(htmlContent, true);
            helper.setTo(userDetail.getEmail());
            helper.setFrom(new InternetAddress(username));
            helper.setSubject("SamrtiWard - Complaint");
            if (Objects.nonNull(file)) {
                helper.addAttachment(file.getName(), file);
            }
            new Thread(() -> {
                try {
                    Transport.send(mimeMessage);
                    System.err.println("\nMail send to :: " + userDetail.getEmail());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            String msg = "Complaint " + complaint.getIncidentId() + " updated. complaint status is  " + complaint.getStatus();
            SMSService.send(userDetail.getPhone(), msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNewUserNotification(String email, String loginUrl, UserDetail userDetail) throws MessagingException {
        final MimeMessage message = new MimeMessage(session);
        User user = userDetail.getUser();
        message.setFrom(new InternetAddress(username));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject("An administrator created an account for you at SamrtiWard.");
        String htmlContent = "<html><body> <center><h1> SamrtiWard </h1><h3> Welcome to SamrtiWard </h3></center>"
                + " <p><b>Hello, " + userDetail.getFirstName() + " " + userDetail.getLastName() + " " + "</b></p> "
                + " <p><b>Username:</b> " + user.getUsername() + "</p><p><b>Password:</b> " + user.getPassword() + "</p> "
                + " <br/><p>Please <a href=" + loginUrl + "> click here </a> to login </p> </body></html>";
        message.setContent(htmlContent, "text/html");
        new Thread(() -> {
            try {
                Transport.send(message);
                System.err.println("\nMail send to :: " + userDetail.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void sendMailChartFile(String emailId, File file[]) {
        final MimeMessage mimeMessage = new MimeMessage(session);
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            String htmlContent = "<html><body> "
                    + " <p><b>Hello,</b></p> "
                    + " <p>Please find attachment's.</p></body></html>";
            helper.setText(htmlContent, true);
            helper.setTo(emailId);
            helper.setFrom(new InternetAddress(username));
            helper.setSubject("Voter Profile Charts");
            for (int i = 0; i < file.length; i++) {
                if (Objects.nonNull(file[i])) {
                    helper.addAttachment(file[i].getName(), file[i]);
                }
            }
            new Thread(() -> {
                try {
                    Transport.send(mimeMessage);
                    System.err.println("\nMail send to :: " + emailId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            String msg = "Charts for Citizens ";
            SMSService.send(emailId, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
