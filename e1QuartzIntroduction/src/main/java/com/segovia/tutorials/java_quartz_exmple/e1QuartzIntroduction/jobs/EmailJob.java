package com.segovia.tutorials.java_quartz_exmple.e1QuartzIntroduction.jobs;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

// This is essentially just "implementing" Quartz.Job but with some extra steps
// that I will note as I learn more about it.
public class EmailJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

    }
}
