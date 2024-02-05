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
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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
@EnableBatchProcessing //needed to initialize all batch metadata
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
//                .next(moveCsvFile())
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
        DefaultLineMapper<Country> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setIncludedFields(4);
        lineTokenizer.setNames("Country");


        BeanWrapperFieldSetMapper<Country> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Country.class);

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

//        return CountryRepo::saveAll;

        return itemWriter;
    }

    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(100);
        return taskExecutor;
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
        DefaultLineMapper<Direction> lineMapper = new DefaultLineMapper<>();//Needed to map lines to objects

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(); //Needed to split line
        lineTokenizer.setDelimiter(","); //will split line on 10 tokens
        lineTokenizer.setNames("Direction"); //we need only "Direction" column from a CSV line, so we specify its name
        lineTokenizer.setStrict(false); //here we say, that wi won't use all the tokens
        lineTokenizer.setIncludedFields(0);//we need only "Direction" column from a CSV line, so we specify its index
        /*
        * По умолчанию, DelimitedLineTokenizer работает в строгом режиме (setStrict(true)), ожидая,
        * что количество полей в строке строго соответствует количеству указанных имен.
        * Это означает, что если количество полей в строке не совпадает с количеством имен, указанных через setNames,
        * токенизатор сгенерирует исключение.
        *
        *
        *
        * Если вам нужно извлечь только определённые столбцы из CSV файла и столбец "Country" не является первым
        * столбцом, вы можете использовать несколько подходов.
        *
        *   Использование индексов столбцов в DelimitedLineTokenizer: Укажите индексы столбцов, которые вам нужны,
        * используя метод setIncludedFields. Это позволит считывать только определённые столбцы.
        */

     /*
     * хорошо, вопрос: Почему мы не указываем названия всех колонок, а затем id той колонки , которую хотим извлеч, а именно и название и id нужной колонки?!
ChatGPT
Отличный вопрос! Это связано с тем, как DelimitedLineTokenizer и BeanWrapperFieldSetMapper работают вместе в Spring Batch для обработки данных из CSV файла.

DelimitedLineTokenizer: Этот компонент отвечает за разделение строки на отдельные поля. Он не знает о структуре вашего целевого объекта (Country в вашем случае). Его задача — просто правильно разделить строку на поля.

Когда вы указываете setNames, вы говорите токенизатору, какие имена столбцов будут использоваться в FieldSet, который он создает. Это полезно, если вы собираетесь обрабатывать все или многие столбцы.
Когда вы используете setIncludedFields, вы указываете, какие конкретные поля (по индексам) вы хотите извлечь из строки. Это полезно, когда вам нужны только определенные столбцы, и вы не хотите тратить ресурсы на обработку всех столбцов.
BeanWrapperFieldSetMapper: Этот компонент отвечает за преобразование FieldSet (полученного от LineTokenizer) в объект вашего целевого класса (Country). Он использует имена, указанные в FieldSet, чтобы найти и установить соответствующие свойства в вашем объекте.

В вашем случае, вы работаете только с одним столбцом (Country), и вам не нужно извлекать или обрабатывать другие столбцы. Поэтому:

Вы указываете setIncludedFields(new int[] {4}), чтобы извлечь только пятый столбец (с индексом 4, поскольку индексация начинается с 0).
Вы указываете setNames(new String[] {"Country"}), чтобы FieldSet, созданный DelimitedLineTokenizer, содержал только одно поле с именем Country.
BeanWrapperFieldSetMapper затем использует это имя (Country) для сопоставления значения из FieldSet с свойством countryName в вашем классе Country.
Это эффективный способ обрабатывать только данные, которые вам действительно нужны, особенно когда вы работаете с большими CSV файлами и хотите оптимизировать производительность, избегая ненужной обработки столбцов.

User
то есть setNames указывает, какие поля должны присутствовать в конечном FieldSet, а вот  setIncludedFields - какой по индексу кусок разделенной строки ложить в качестве значения, так?
ChatGPT
Да, именно так!

setNames в DelimitedLineTokenizer определяет имена полей в FieldSet. Эти имена используются для маппинга каждого извлеченного поля со значением в FieldSet. Если вы работаете со всеми столбцами, вы перечисляете все имена, чтобы FieldSet знал, как называется каждый столбец.

setIncludedFields позволяет вам указать, какие конкретно индексы (позиции столбцов в строке) нужно извлекать из строки. Это уменьшает количество обрабатываемых данных, если вы не заинтересованы во всех столбцах.

Когда DelimitedLineTokenizer обрабатывает строку:

Он разделяет строку на отдельные значения на основе указанного разделителя (запятой в вашем случае).
Затем он создает FieldSet, содержащий только те поля, индексы которых указаны в setIncludedFields.
При создании FieldSet, он использует имена, указанные в setNames, чтобы ассоциировать каждое извлеченное значение с соответствующим именем поля.
После этого, BeanWrapperFieldSetMapper использует FieldSet для заполнения свойств объекта Country. Он ищет в объекте Country свойства с именами, соответствующими именам в FieldSet, и устанавливает их значениями, полученными из FieldSet.

Таким образом, вы эффективно извлекаете и обрабатываете только те данные, которые вам действительно нужны.
     *
     * */



        BeanWrapperFieldSetMapper<Direction> fieldSetMapper = new BeanWrapperFieldSetMapper<>(); //This class will map our row column to an object
        fieldSetMapper.setTargetType(Direction.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

//    @Scope(scopeName = "step", proxyMode = )
    @Bean
    @StepScope
    public DirectionProcessor directionsProcessor() {
        return new DirectionProcessor();
    }

    @Bean
    public RepositoryItemWriter<Direction> directionsWriter(){
        RepositoryItemWriter<Direction> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(directionRepo); //Specify object's repository
        itemWriter.setMethodName("save"); //Specify the method, that will save the object

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
        DefaultLineMapper<Affect> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();

        tokenizer.setDelimiter(",");
        tokenizer.setNames("Direction","Year","Date","Weekday","Country","Commodity","Transport_Mode","Measure","Value","Cumulative");

//        Will work only if object is simple. In my case My object contains Relations and date, so I realize my own fieldSetMapper
//        BeanWrapperFieldSetMapper<Affect> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
//        fieldSetMapper.setTargetType(Affect.class);
//        lineMapper.setFieldSetMapper(fieldSetMapper);

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
}
