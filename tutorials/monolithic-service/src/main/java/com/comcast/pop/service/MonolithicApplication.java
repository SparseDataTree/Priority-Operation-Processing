package com.comcast.pop.service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = { "com.comcast.pop.service" })
@EnableScheduling
public class MonolithicApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(MonolithicApplication.class, args);
    }
}
