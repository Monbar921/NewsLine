package com.newsline.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;


@Configuration
@EnableRetry
@PropertySource("classpath:retryConfig.properties")
public class RetryConfig {
    @Value("${retry.maxAttempts}")
    private int maxAttempts;

    @Value("${retry.maxDelay}")
    private int maxDelay;

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(maxDelay);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

//        Map<Class<? extends Throwable>, Boolean> map= new HashMap<>();
//        map.put(IOException.class, Boolean.TRUE);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAttempts);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
