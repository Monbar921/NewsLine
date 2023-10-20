package com.broker.service;

import com.broker.dto.TokenDTO;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.broker.config.RabbitMQConfig.EMAIL_NOTIFICATION_QUEUE;
import static com.broker.config.RabbitMQConfig.QUEUE_RECOVER_NAME;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
public class MailService {
    private Properties prop;
    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.email}")
    private String emailFrom;
    @Value("${activate-url}")
    private String activateURL;
    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.ratelimit.time.minutes}")
    private int rateLimitTime;
    @Value("${mail.ratelimit.count}")
    private int rateLimitCount;
//    @Value("${rabbitmq.concurrency.number}")
//    private String CONCURRENCY_NUMBER;
    private TimedSemaphore semaphore;


    @RabbitListener(queues = {EMAIL_NOTIFICATION_QUEUE})
    public void sendToken(TokenDTO token) throws MessagingException {
        try {
            semaphore.acquire();

            if (token == null || token.value() == null) {
                throw new MessagingException("Give me token");
            }
            if (token.email() == null) {
                throw new MessagingException("Give me email");
            }
            String message = String.format("%s/%s", activateURL, token.value());
            System.out.println(message);
            sendEmail(token.email(), "Activation on auth server", message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void initProperties() {
        prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.ssl.trust", host);

        semaphore = new TimedSemaphore(rateLimitTime, TimeUnit.MINUTES, rateLimitCount);
    }

    private void sendEmail(String emailTo, String subject, String msg) throws MessagingException {
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailFrom));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
        message.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }


}
