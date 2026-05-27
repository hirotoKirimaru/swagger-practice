package com.example.library.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
public class BookImportJobLauncher {

    private final JobLauncher jobLauncher;
    private final Job bookImportJob;

    public BookImportJobLauncher(JobLauncher jobLauncher, Job bookImportJob) {
        this.jobLauncher = jobLauncher;
        this.bookImportJob = bookImportJob;
    }

    public JobExecution launch() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis())
                .toJobParameters();
        return jobLauncher.run(bookImportJob, parameters);
    }
}
