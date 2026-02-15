package com.technical.rest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.technical")
@EnableJpaRepositories(basePackages = "com.technical.database.entity")
@EntityScan(basePackages = "com.technical.database.entity")
public class TestConfig {
}
