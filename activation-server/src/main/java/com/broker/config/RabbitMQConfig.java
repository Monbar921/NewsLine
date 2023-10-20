package com.broker.config;

import com.broker.service.MailService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitMQConfig {

    public final static String EMAIL_NOTIFICATION_QUEUE = "email.notification.q";
    public final static String QUEUE_RECOVER_NAME = "recover.email.notification.q";
    public final static String TOPIC_EXCHANGE_NAME = "email.topic.notification.exchange";
    public static final String BINDING_NOTIFICATION_PATTERN = "*.notification";

    @Value("${rabbitmq.durable}")
    private boolean DURABLE;
    @Value("${rabbitmq.backoff.max.attempts}")
    private int MAX_ATTEMPTS;
    @Value("${rabbitmq.backoff.initial.interval.ms}")
    private long INITIAL_INTERVAL_MS;
    @Value("${rabbitmq.backoff.max.interval.ms}")
    private long MAX_INTERVAL_MS;
    @Value("${rabbitmq.backoff.multiplier}")
    private double MULTIPLIER;
    @Value("${rabbitmq.concurrency.number}")
    private Integer CONCURRENCY_NUMBER;


    private static final int PREFETCH_COUNT = 1;
    private final CachingConnectionFactory cachingConnectionFactory;


    public RabbitMQConfig(CachingConnectionFactory cachingConnectionFactory) {
        this.cachingConnectionFactory = cachingConnectionFactory;
    }


    @Bean
    public Declarables topicBindings() {
        Queue topicQueue1 = new Queue(EMAIL_NOTIFICATION_QUEUE, DURABLE);
        TopicExchange topicExchange = new TopicExchange(TOPIC_EXCHANGE_NAME);

        return new Declarables(topicQueue1, topicExchange, BindingBuilder
                .bind(topicQueue1)
                .to(topicExchange)
                .with(BINDING_NOTIFICATION_PATTERN));
    }

    @Bean
    public RetryOperationsInterceptor retryInterceptor(RabbitTemplate rabbitTemplate) {

        return RetryInterceptorBuilder.stateless().maxAttempts(MAX_ATTEMPTS)
                .backOffOptions(INITIAL_INTERVAL_MS, MULTIPLIER, MAX_INTERVAL_MS)
                .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, RabbitTemplate rabbitTemplate) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, cachingConnectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setAdviceChain(retryInterceptor(rabbitTemplate));
        factory.setDefaultRequeueRejected(false);
        factory.setPrefetchCount(PREFETCH_COUNT);
        factory.setConcurrentConsumers(CONCURRENCY_NUMBER);
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory);
        template.setMessageConverter(converter);
        template.setBeforePublishPostProcessors();
        return template;
    }
}