package org.cloudcafe.aws.chapter7.config;

import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

//@Configuration
public class AwsXrayConfig {
    @Bean
    public Filter TracingFilter() {
       return new AWSXRayServletFilter("cloudcafe-app");
    }
}