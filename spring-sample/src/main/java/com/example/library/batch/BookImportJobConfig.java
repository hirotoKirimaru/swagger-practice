package com.example.library.batch;

import com.example.library.domain.Book;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BookImportJobConfig {

    public static final String JOB_NAME = "bookImportJob";
    private static final int CHUNK_SIZE = 100;

    @Bean
    public FlatFileItemReader<BookCsvRecord> bookCsvReader(
            @Value("${library.batch.import-file}") Resource importFile) {
        return new FlatFileItemReaderBuilder<BookCsvRecord>()
                .name("bookCsvReader")
                .resource(importFile)
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("isbn", "title", "author")
                .targetType(BookCsvRecord.class)
                .build();
    }

    @Bean
    public JpaItemWriter<Book> bookJpaWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Book>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step bookImportStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               FlatFileItemReader<BookCsvRecord> bookCsvReader,
                               BookItemProcessor bookItemProcessor,
                               JpaItemWriter<Book> bookJpaWriter) {
        return new StepBuilder("bookImportStep", jobRepository)
                .<BookCsvRecord, Book>chunk(CHUNK_SIZE, transactionManager)
                .reader(bookCsvReader)
                .processor(bookItemProcessor)
                .writer(bookJpaWriter)
                .faultTolerant()
                .skipLimit(10)
                .skip(IllegalArgumentException.class)
                .build();
    }

    @Bean
    public Job bookImportJob(JobRepository jobRepository, Step bookImportStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(bookImportStep)
                .build();
    }
}
