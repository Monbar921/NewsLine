package com.newsline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


//It is the main class of project. It runs application
@SpringBootApplication
public class NewslineApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(NewslineApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(NewslineApplication.class);
    }


}
