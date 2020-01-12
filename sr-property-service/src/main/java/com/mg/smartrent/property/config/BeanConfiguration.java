package com.mg.smartrent.property.config;

import com.mg.persistence.service.QueryService;
import com.mg.persistence.service.nosql.MongoQueryService;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public QueryService queryService(MongoQueryService queryService) {
        return queryService;
    }
}
