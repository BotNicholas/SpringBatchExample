package org.nicholas.spring.batchtask.config;

import org.nicholas.spring.batchtask.config.mappers.AffectFieldSetMapper;
import org.nicholas.spring.batchtask.config.processors.AffectItemProcessor;
import org.nicholas.spring.batchtask.config.processors.CountryProcessor;
import org.nicholas.spring.batchtask.config.processors.DirectionProcessor;
import org.nicholas.spring.batchtask.config.tasklets.FileMovingTasklet;
import org.nicholas.spring.batchtask.model.Affect;
import org.nicholas.spring.batchtask.model.Country;
import org.nicholas.spring.batchtask.model.Direction;
import org.nicholas.spring.batchtask.repo.AffectRepo;
import org.nicholas.spring.batchtask.repo.CountryRepo;
import org.nicholas.spring.batchtask.repo.DirectionRepo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource("classpath:files.properties")
public class MainBatchJobConfig {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DirectionRepo directionRepo;
    @Autowired
    private CountryRepo countryRepo;
    @Autowired
    private AffectRepo affectRepo;

    @Value("${file.input}")
    private String INPUT_FILE;
    @Value("${directory.exported}")
    private String EXPORTED_DIR;

    @Bean
    public Job mainJob(){
        return new JobBuilder("mainJob", jobRepository)
                .start(importCountriesStep())
                .next(importDirectionsStep())
                .next(importAffectsStep())
                .next(moveCsvFile())
                .build();
    }

//-------------------------Countries step------------------------------------
    @Bean
    public Step importCountriesStep(){
        return new StepBuilder("importCountriesStep", jobRepository)
                .<Country, Country>chunk(10000, transactionManager)
                .reader(countriesReader())
                .processor(countriesProcessor())
                .writer(countriesWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public FlatFileItemReader<Country> countriesReader() {
        FlatFileItemReader<Country> itemReader = new FlatFileItemReader<>();
        itemReader.setName("countriesItemReader");
        itemReader.setResource(new FileSystemResource(INPUT_FILE));
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(countriesLineMapper());
        return itemReader;
    }

    private DefaultLineMapper<Country> countriesLineMapper() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setIncludedFields(4);
        lineTokenizer.setNames("Country");

        BeanWrapperFieldSetMapper<Country> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Country.class);

        DefaultLineMapper<Country> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    @StepScope
    public CountryProcessor countriesProcessor(){
        return new CountryProcessor();
    }

    @Bean
    public ItemWriter<Country> countriesWriter(){
        RepositoryItemWriter<Country> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(countryRepo);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

//-------------------------Directions step------------------------------------
    @Bean
    public Step importDirectionsStep(){
        return new StepBuilder("directionsStep", jobRepository)
                .<Direction, Direction>chunk(10000, transactionManager)
                .reader(directionsReader())
                .processor(directionsProcessor())
                .writer(directionsWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public FlatFileItemReader<Direction> directionsReader() {
        FlatFileItemReader<Direction> itemReader = new FlatFileItemReader<>();
        itemReader.setName("directionsReader");
        itemReader.setResource(new FileSystemResource(INPUT_FILE));
        itemReader.setLineMapper(directionsLineMapper());
        itemReader.setLinesToSkip(1);
        return itemReader;
    }

    private DefaultLineMapper<Direction> directionsLineMapper() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames("Direction");
        lineTokenizer.setStrict(false);
        lineTokenizer.setIncludedFields(0);

        BeanWrapperFieldSetMapper<Direction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Direction.class);

        DefaultLineMapper<Direction> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    @StepScope
    public DirectionProcessor directionsProcessor() {
        return new DirectionProcessor();
    }

    @Bean
    public RepositoryItemWriter<Direction> directionsWriter(){
        RepositoryItemWriter<Direction> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(directionRepo);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

//-------------------------Affects step------------------------------------
    @Bean
    public Step importAffectsStep(){
        return new StepBuilder("importAffectsStep", jobRepository)
                .<Affect, Affect>chunk(10000, transactionManager)
                .reader(affectReader())
                .processor(affectProcessor())
                .writer(affectWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public FlatFileItemReader<Affect> affectReader(){
        FlatFileItemReader<Affect> reader = new FlatFileItemReader<>();
        reader.setName("affectReader");
        reader.setLinesToSkip(1);
        reader.setResource(new FileSystemResource(INPUT_FILE));
        reader.setLineMapper(affectLineMapper());
        return reader;
    }

    private DefaultLineMapper<Affect> affectLineMapper(){
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("Direction","Year","Date","Weekday","Country","Commodity","Transport_Mode","Measure","Value","Cumulative");

        DefaultLineMapper<Affect> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new AffectFieldSetMapper(countryRepo, directionRepo));

        return lineMapper;
    }

    @Bean
    @StepScope
    public AffectItemProcessor affectProcessor() {
        return new AffectItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<Affect> affectWriter(){
        RepositoryItemWriter<Affect> writer = new RepositoryItemWriter<>();
        writer.setRepository(affectRepo);
        writer.setMethodName("save");
        return  writer;
    }

//-------------------------File moving tasklet------------------------------------
    @Bean
    public Step moveCsvFile(){
        return new StepBuilder("moveCsvFile", jobRepository)
                .tasklet(fileMovingTasklet(), transactionManager)
                .build();
    }

    @Bean
    public FileMovingTasklet fileMovingTasklet(){
        return new FileMovingTasklet(INPUT_FILE, EXPORTED_DIR);
    }

    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(100);
        return taskExecutor;
    }
}
