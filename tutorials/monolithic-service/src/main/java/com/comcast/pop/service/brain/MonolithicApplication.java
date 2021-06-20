package com.comcast.pop.service.brain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.comcast.pop.service" })
public class MonolithicApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(MonolithicApplication.class, args);
    }
}
