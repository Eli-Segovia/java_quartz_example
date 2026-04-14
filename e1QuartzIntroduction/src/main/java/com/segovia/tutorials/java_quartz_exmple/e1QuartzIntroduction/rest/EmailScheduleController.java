package com.segovia.tutorials.java_quartz_exmple.e1QuartzIntroduction.rest;

import com.segovia.tutorials.java_quartz_exmple.e1QuartzIntroduction.jobs.EmailJob;
import com.segovia.tutorials.java_quartz_exmple.e1QuartzIntroduction.payload.EmailRequest;
import com.segovia.tutorials.java_quartz_exmple.e1QuartzIntroduction.payload.EmailResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
public class EmailScheduleController {

    // I guess the Spring Boot Quartz just plugs one in here...
    @Autowired
    private Scheduler scheduler;

    @PostMapping("/schedule/email")
    public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest request) {
        try{
            ZonedDateTime zonedDateTime = ZonedDateTime.of(request.getDateTime(), request.getTimeZone());

            // cannot schedule in the past
            if (zonedDateTime.isBefore(ZonedDateTime.now())) {
                throw new SchedulerException("DateTime must be after current time");
            }

            // Create Job and Trigger detailed below
            JobDetail emailJobDetail = buildJobDetailForEmailRequest(request);
            Trigger triggerForEmailRequest = buildTriggerForEmailRequest(emailJobDetail, zonedDateTime);

            // Schedule the Job with the Trigger
            scheduler.scheduleJob(emailJobDetail, triggerForEmailRequest);

            // create the response
            EmailResponse successResponse = new EmailResponse(true,
                    emailJobDetail.getKey().getName(),
                    emailJobDetail.getKey().getGroup(),
                    "Email Sent Successfully");

            // return 200 code and the Body
            return ResponseEntity.ok(successResponse);

        } catch (SchedulerException schedulerException) {
            log.error("Error while scheduling email: ", schedulerException);
            EmailResponse emailResponse= new EmailResponse(false, "Error while scheduling email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(emailResponse);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<String> getApiTest() {
        return ResponseEntity.ok("Test Pass");
    }

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
        // First we tell it which class is going to execute the business logic when the Job is Triggered. EmailJob
        // withIdentity -> the Identity should be a random unique ID and in the group "email-jobs"
        // withDescription -> the description could be whatever, I guess
        // usingJobData -> the JobData is the jobDataMap we created above
        // store Durably tells quartz to store this even without triggers in the DB
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();

    }


    private Trigger buildTriggerForEmailRequest(JobDetail jobDetail, ZonedDateTime startAt) {
        // forJob -> point to the job we are passing in
        // withIdentity -> just use the jobKey and the triggers will go to "email-triggers group
        // description is whatever
        // startAt starts the trigger at given time
        // withSchedule -> honestly idk. It returns a simpleSchedule Object that is able to handle misfires...

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
