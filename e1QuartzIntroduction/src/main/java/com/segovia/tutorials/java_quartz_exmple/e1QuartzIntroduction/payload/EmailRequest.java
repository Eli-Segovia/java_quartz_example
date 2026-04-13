package com.segovia.tutorials.java_quartz_exmple.e1QuartzIntroduction.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * This represents the emails we will be working with
 * doing some data validation with jakarta validation annotations.
 *
 * Basically we will be sending an email with the following
 * members, and then the email will send back an @EmailResponse
 */
// Automatically generates getters and setters
@Getter
@Setter
public class EmailRequest {

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String body;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private ZoneId timeZone;
}
