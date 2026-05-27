package com.example.library.web;

import com.example.library.batch.BookImportJobLauncher;
import org.springframework.batch.core.JobExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/batch")
public class BatchController {

    private final BookImportJobLauncher launcher;

    public BatchController(BookImportJobLauncher launcher) {
        this.launcher = launcher;
    }

    @PostMapping("/book-import")
    public ResponseEntity<Map<String, Object>> runBookImport() throws Exception {
        JobExecution execution = launcher.launch();
        return ResponseEntity.accepted().body(Map.of(
                "jobId", execution.getJobId(),
                "status", execution.getStatus().name(),
                "exitCode", execution.getExitStatus().getExitCode()
        ));
    }
}
