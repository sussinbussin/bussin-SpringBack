package com.bussin.SpringBack.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class ScalarConfig {
    /**
     * Configures a RuntimeWiringConfigurer to wire the Beans supporting the extended scalars.
     * @return RuntimeWiringConfigurer to wire the beans below
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> {
            wiringBuilder.scalar(ExtendedScalars.Date);
            wiringBuilder.scalar(ExtendedScalars.DateTime);
        };
    }

    /**
     * Bean for ExtendedScalars Date to support GraphQL Date Scalar.
     * @return ExtendedScalars Date
     */
    @Bean
    public GraphQLScalarType date() {
        return ExtendedScalars.Date;
    }

    /**
     * Bean for ExtendedScalars DateTime to support GraphQL DateTime Scalar.
     * @return ExtendedScalars DateTime
     */
    @Bean
    public GraphQLScalarType dateTime() {
        return ExtendedScalars.DateTime;
    }
}
