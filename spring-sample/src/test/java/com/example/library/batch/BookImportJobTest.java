package com.example.library.batch;

import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@TestPropertySource(properties = "spring.batch.job.enabled=false")
@Import(BookImportJobTest.TestConfig.class)
class BookImportJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private Job bookImportJob;

    @Autowired
    private BookRepository bookRepository;

    @AfterEach
    void cleanup() {
        jobRepositoryTestUtils.removeJobExecutions();
        bookRepository.deleteAll();
    }

    @Test
    void csvファイルの全書籍がインポートされる() throws Exception {
        jobLauncherTestUtils.setJob(bookImportJob);

        JobExecution execution = jobLauncherTestUtils.launchJob();

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(bookRepository.count()).isEqualTo(6L);
    }

    @Test
    void 既存ISBNはスキップされる() throws Exception {
        jobLauncherTestUtils.setJob(bookImportJob);
        jobLauncherTestUtils.launchJob();
        long firstCount = bookRepository.count();

        JobExecution second = jobLauncherTestUtils.launchJob();

        assertThat(second.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(bookRepository.count()).isEqualTo(firstCount);
    }

    static class TestConfig {
    }
}
