package org.nicholas.spring.batchtask.config;

import org.nicholas.spring.batchtask.config.extractors.AffectFieldExtractor;
import org.nicholas.spring.batchtask.config.processors.ExportAffectsItemProcessor;
import org.nicholas.spring.batchtask.model.Affect;
import org.nicholas.spring.batchtask.repo.AffectRepo;
import org.nicholas.spring.batchtask.repo.CountryRepo;
import org.nicholas.spring.batchtask.repo.DirectionRepo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

@Configuration
@PropertySource("classpath:files.properties")
//@EnableBatchProcessing
public class SecondBatchJobConfig {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private CountryRepo countryRepo;
    @Autowired
    private DirectionRepo directionRepo;
    @Autowired
    private AffectRepo affectRepo;

    @Value("${file.output}")
    private String FILE_OUT;

    @Bean
    public Job saveToFileJob(){
        return new JobBuilder("saveToFileJob", jobRepository)
                .start(exportDataToCsv())
                .build();
    }

    @Bean
    public Step exportDataToCsv(){
        return new StepBuilder("exportDataToCsv", jobRepository)
                .<Affect, Affect>chunk(10000, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public RepositoryItemReader<Affect> reader(){
        RepositoryItemReader<Affect> dbReader = new RepositoryItemReader<>();
        dbReader.setName("dbReader");
        dbReader.setRepository(affectRepo);
        dbReader.setMethodName("findAll");
        dbReader.setPageSize(1000);

        HashMap<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        dbReader.setSort(sorts);

        return dbReader;
    }

    @Bean
    @StepScope
    public ExportAffectsItemProcessor processor(){
        return new ExportAffectsItemProcessor();
    }

    @Bean
    public FlatFileItemWriter<Affect> writer(){
        FlatFileItemWriter<Affect> flatFileItemWriter = new FlatFileItemWriter<>();
        flatFileItemWriter.setName("exportFlatFileItemWriter");
        flatFileItemWriter.setResource(new FileSystemResource(FILE_OUT));
        flatFileItemWriter.setLineAggregator(lineAgregator());
        flatFileItemWriter.setHeaderCallback(writer -> writer.write("Direction,Year,Date,Weekday,Country,Commodity,Transport_Mode,Measure,Value,Cumulative"));
        return flatFileItemWriter;
    }

    private DelimitedLineAggregator<Affect> lineAgregator(){
        DelimitedLineAggregator<Affect> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(new AffectFieldExtractor());
        return lineAggregator;
    }

//    Will be useful for simple table. Our table has relations
//    private BeanWrapperFieldExtractor<Affect> fieldExtractor(){
//        BeanWrapperFieldExtractor<Affect> fieldExtractor = new BeanWrapperFieldExtractor<>();
//        fieldExtractor.setNames(fields...);
//    }


    @Bean
    public SimpleAsyncTaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(100);
        return taskExecutor;
    }
}
