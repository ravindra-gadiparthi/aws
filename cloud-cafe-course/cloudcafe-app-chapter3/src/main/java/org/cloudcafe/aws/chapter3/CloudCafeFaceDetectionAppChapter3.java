package org.cloudcafe.aws.chapter3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.util.Arrays;

@SpringBootApplication
public class CloudCafeFaceDetectionAppChapter3 {

    public static void main(String[] args) {
        SpringApplication.run(CloudCafeFaceDetectionAppChapter3.class, args);
    }

    @Bean
    FilterRegistrationBean hiddenHttpMethodFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new HiddenHttpMethodFilter());
        registrationBean.setUrlPatterns(Arrays.asList("/*"));
        return registrationBean;
    }


    @Bean
    TransferManager transferManager(AmazonS3 amazonS3) {
        return TransferManagerBuilder.standard().withS3Client(amazonS3).build();
    }
}
