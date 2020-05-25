package org.cloudcafe.aws.chapter3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.util.Arrays;

@SpringBootApplication
public class SimpleFileUploaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleFileUploaderApplication.class, args);
    }

    @Bean
    FilterRegistrationBean hiddenHttpMethodFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new HiddenHttpMethodFilter());
        registrationBean.setUrlPatterns(Arrays.asList("/*"));
        return registrationBean;
    }

}
