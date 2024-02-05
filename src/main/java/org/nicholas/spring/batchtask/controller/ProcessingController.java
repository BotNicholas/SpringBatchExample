package org.nicholas.spring.batchtask.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/process")
public class ProcessingController {
    @Autowired
    private JobLauncher launcher;

    @Autowired
    @Qualifier("mainJob")
    private Job mainJob;

    @Autowired
    @Qualifier("saveToFileJob")
    private Job saveToFileJob;

    @PostMapping("/main")
    public ResponseEntity<String> export() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        launcher.run(mainJob, jobParameters());

        return new ResponseEntity<String>("Data has been successfully transferred!", HttpStatus.OK);
    }

    @PostMapping("/second")
    public ResponseEntity<String> saveToFile() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        launcher.run(saveToFileJob, jobParameters());

        return new ResponseEntity<>("All data has been saved successfully!", HttpStatus.OK);
    }

    private JobParameters jobParameters(){
        JobParametersBuilder parameters = new JobParametersBuilder();
        parameters.addLong("startAt", System.currentTimeMillis()); //here we have to pass something. It does not even matter what

        return parameters.toJobParameters();
    }
}
