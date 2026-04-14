package com.segovia.tutorials.java_quartz_exmple.e1QuartzIntroduction.jobs;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

// This is essentially just "implementing" Quartz.Job but with some extra steps
// that I will note as I learn more about it.

// Make this a component to let Quartz/Spring be aware of it ?
@Component
public class EmailJob extends QuartzJobBean {

    @Autowired
    private JavaMailSender mailSender;     // autowired from the amil starter bull

    @Autowired
    private MailProperties mailProperties; // autowired from the email starter bull


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        // jobExecutionContext gives us some info about the Job :)
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String recipientEmail = jobDataMap.getString("email");

        System.out.printf(
                        "==============================\n" +
                        "        Email Preview\n" +
                        "==============================\n" +
                        "To      : %s\n" +
                        "Subject : %s\n" +
                        "--------------------------------\n" +
                        "%s\n" +
                        "==============================\n",
                recipientEmail,
                subject,
                body
        );

        sendMail(mailProperties.getUsername(), recipientEmail, subject, body);
    }


    private void sendMail(String fromEmail, String toEmail, String subject, String body) {
        try {

            // Boilerplate that creates an email message. I won't even bother to go through it
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);

            mailSender.send(message);

        } catch(MessagingException messagingException) {
            System.out.println(messagingException);
        }
    }
}
