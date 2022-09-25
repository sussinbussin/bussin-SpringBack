package com.bussin.SpringBack.testConfig;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.bussin.SpringBack.repositories")
@TestPropertySource("classpath:application.properties")
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan("com.bussin.SpringBack")
public class H2JpaConfig {

}
