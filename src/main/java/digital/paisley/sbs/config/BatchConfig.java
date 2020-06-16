package digital.paisley.sbs.config;

import digital.paisley.sbs.entities.Orders;
import digital.paisley.sbs.listeners.JobCompletionListener;
import digital.paisley.sbs.listeners.JobExceptionSkipListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


@Configuration
@EnableBatchProcessing
//@EnableScheduling
public class BatchConfig {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final JobCompletionListener listener;

    static String[] COLUMN_NAMES = {"orderId", "orderDate" ,"productName"};
    static String SQL_INSERT_QUERY = "INSERT INTO PUBLIC.ORDERS (ORDER_ID, ORDER_DATE,PRODUCT_NAME)  VALUES ( :orderId, :orderDate, :productName)";
    static int[] COLUMN_INDICES = {1, 2, 3, 4};

    @Value("classPath:/input/sales.csv")
    private Resource inputResource;

    public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, JobCompletionListener listener) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.listener = listener;
    }

    @Bean
    public Job readCSVFileJob(final Step step) {
        return jobBuilderFactory
                .get("readCSVFileJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step)
                .build();
    }

    @Bean
    public org.springframework.validation.Validator validator() {
        return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
    }

    @Bean
    public Validator<Orders> springValidator() {
        SpringValidator<Orders> springValidator = new SpringValidator<>();
        springValidator.setValidator(validator());
        return springValidator;
    }

    @Bean
    public Step step(final ItemWriter<Orders> writer) throws Exception {
        return stepBuilderFactory
                .get("step")
                .<Orders, Orders>chunk(5)
                .reader(reader())
                //.processor(beanValidatingItemProcessor())
                .processor(processor())
                .writer(writer)
                .listener(new JobExceptionSkipListener())
                //.faultTolerant()
                .exceptionHandler(new RepeatExceptionHandler())
                .build();
    }

    @Bean
    public ItemProcessor<Orders, Orders> processor() throws Exception {
        ValidatingItemProcessor<Orders> validatingItemProcessor = new ValidatingItemProcessor<>(springValidator());
        validatingItemProcessor.setFilter(false);
        validatingItemProcessor.afterPropertiesSet();
        return validatingItemProcessor;

    }

    @Bean("readerService")
    public FlatFileItemReader<Orders> reader() {
        FlatFileItemReader<Orders> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper(lineMapper());
        itemReader.setLinesToSkip(1);
        itemReader.setResource(inputResource);
        return itemReader;
    }

    @Bean
    public LineMapper<Orders> lineMapper() {
        DefaultLineMapper<Orders> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(COLUMN_NAMES);
        lineTokenizer.setIncludedFields(COLUMN_INDICES);
        BeanWrapperFieldSetMapper<Orders> mapper = new BeanWrapperFieldSetMapperCustom<>();
        mapper.setTargetType(Orders.class);
        mapper.setDistanceLimit(0);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(mapper);
        return lineMapper;
    }

    @Bean("writerService")
    public JdbcBatchItemWriter<Orders> writer(DataSourceConfiguration dataSource) {
        return new JdbcBatchItemWriterBuilder<Orders>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(SQL_INSERT_QUERY)
                .dataSource(dataSource.dataSource())
                .build();
    }

}
