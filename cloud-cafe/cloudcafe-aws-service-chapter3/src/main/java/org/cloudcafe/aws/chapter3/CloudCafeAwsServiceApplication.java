package org.cloudcafe.aws.chapter3;

import org.cloudcafe.aws.chapter3.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

@SpringBootApplication
public class CloudCafeAwsServiceApplication {

    @Autowired
    ImageRepository imageRepository;

    public static void main(String[] args) {
        SpringApplication.run(CloudCafeAwsServiceApplication.class, args);
    }

    @Bean
    HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }


    @Bean
    CommandLineRunner commandLineRunner(){
        return (args -> {
            System.out.println("getting data from mysql");
            System.out.println(imageRepository.findAll());
        });
    }
}
