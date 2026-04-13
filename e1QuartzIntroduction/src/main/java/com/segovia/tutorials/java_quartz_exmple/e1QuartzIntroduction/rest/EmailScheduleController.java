package com.segovia.tutorials.java_quartz_exmple.e1QuartzIntroduction.rest;

import com.segovia.tutorials.java_quartz_exmple.e1QuartzIntroduction.jobs.EmailJob;
import com.segovia.tutorials.java_quartz_exmple.e1QuartzIntroduction.payload.EmailRequest;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import java.util.UUID;

public class EmailScheduleController {



    private JobDetail buildJobDetailForEmailRequest(EmailRequest emailRequest) {
        // JobDetail is stored in the database with a field called "Job_Data"
        // Job_Data is just some extra data that the JobDetail should contain

        // It is stored as a collection of K-V pairs, hence a map is being used.
        // Since this Job is for EmailRequest, we will put some email request
        // data in the Job_Data map.
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", emailRequest.getEmail());
        jobDataMap.put("subject", emailRequest.getSubject());
        jobDataMap.put("body", emailRequest.getBody());

        // Then we use the JobBuilder builder pattern to return the Job Details
        // the Identity should be a random unique ID and in the group "email-jobs"
        // the description could be whatever, I guess
        // the JobData is the jobDataMap we created above
        // store Durably tells quartz to store this even without triggers in the DB
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();

    }
}
