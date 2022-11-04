package com.bussin.SpringBack.config;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoConfig {
    /**
     * Configure Amazon Cognito path and credentials using application.properties.
     *
     * @return AWSCognitoIdentityProvider The AWSCognitoIdentityProvider
     */
    @Bean
    public AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {
        ClasspathPropertiesFileCredentialsProvider propertiesFileCredentialsProvider =
                new ClasspathPropertiesFileCredentialsProvider("application" +
                        ".properties");

        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withCredentials(propertiesFileCredentialsProvider)
                .withRegion("ap-southeast-1")
                .build();

    }
}
