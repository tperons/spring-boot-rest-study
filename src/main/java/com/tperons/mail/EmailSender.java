package com.tperons.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.tperons.config.EmailConfig;
import com.tperons.exception.EmailException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);
    private final JavaMailSender javaMailSender;

    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(EmailConfig config, String to, String subject, String body, File attachment) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(config.getUsername());
            helper.setTo(getRecipients(to).toArray(new InternetAddress[0]));
            helper.setSubject(subject);
            helper.setText(body, true);

            if (attachment != null) {
                helper.addAttachment(attachment.getName(), attachment);
            }

            javaMailSender.send(message);
            logger.info("E-mail sent to {} with the subject '{}'", to, subject);
        } catch (MessagingException e) {
            throw new EmailException("Error sending the e-mail.", e);
        }
    }

    private ArrayList<InternetAddress> getRecipients(String to) {
        String toWithoutSpaces = to.replaceAll("\\s", "");
        StringTokenizer tokenizer = new StringTokenizer(toWithoutSpaces, ";");
        ArrayList<InternetAddress> recipientsList = new ArrayList<>();
        while (tokenizer.hasMoreElements()) {
            try {
                recipientsList.add(new InternetAddress(tokenizer.nextElement().toString()));
            } catch (AddressException e) {
                throw new EmailException("Error parsing e-mail request.", e);
            }
        }
        return recipientsList;
    }
}
