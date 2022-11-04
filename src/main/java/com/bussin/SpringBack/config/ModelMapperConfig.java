package com.bussin.SpringBack.config;

import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoutePublicDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    /**
     * Creates a model mapper instance to map DTOs.
     *
     * @return configured model mapper
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Mapping to not initialise Driver object for Users by default
        modelMapper.addMappings(new PropertyMap<UserDTO, User>() {
            @Override
            protected void configure() {
                skip(destination.getDriver());
            }
        });

        // Configure where to derive car plate from for PlannedRoutePublicDTO
        modelMapper.addMappings(new PropertyMap<PlannedRoute, PlannedRoutePublicDTO>() {
            @Override
            protected void configure() {
                map().setCarPlate(source.getDriver().getCarPlate());
            }
        });

        return modelMapper;
    }

    /**
     * Creates an object mapper that deserializes JSON to objects
     *
     * @return an object mapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
