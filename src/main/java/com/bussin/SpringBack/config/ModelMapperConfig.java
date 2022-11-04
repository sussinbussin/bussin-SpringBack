package com.bussin.SpringBack.config;

import com.bussin.SpringBack.models.driver.Driver;
import com.bussin.SpringBack.models.driver.DriverPublicDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoutePublicDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteResultDTO;
import com.bussin.SpringBack.models.ride.Ride;
import com.bussin.SpringBack.models.ride.RideReturnDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        modelMapper.emptyTypeMap(UserDTO.class, User.class)
                .addMappings(mapper -> mapper.skip(User::setDriver))
                .implicitMappings();

        // Configure where to derive car plate from for PlannedRouteResultDTO
        modelMapper.emptyTypeMap(PlannedRoute.class, PlannedRouteResultDTO.class)
                .addMappings(mapper ->
                        mapper.map(src -> src.getDriver().getCarPlate(),
                                PlannedRouteResultDTO::setCarPlate))
                .implicitMappings();

        modelMapper.emptyTypeMap(Driver.class, DriverPublicDTO.class)
                .addMappings(mapper -> mapper
                        .map(Driver::getPlannedRoutes,
                                DriverPublicDTO::setPlannedRoutes))
                .addMappings(mapper -> mapper.map(src -> src.getUser().getId(), DriverPublicDTO::setUser))
                .implicitMappings();

        modelMapper.emptyTypeMap(Ride.class, RideReturnDTO.class)
                .addMappings(mapper -> mapper
                        .map(Ride::getPlannedRoute,
                                RideReturnDTO::setPlannedRoute))
                .addMappings(mapper -> mapper.map(src -> src.getUser().getId(), RideReturnDTO::setUserId))
                .implicitMappings();

        modelMapper.emptyTypeMap(PlannedRoute.class, PlannedRoutePublicDTO.class)
                .addMappings(mapper -> mapper
                        .map(PlannedRoute::getRides,
                                PlannedRoutePublicDTO::setRides))
                .addMappings(mapper -> mapper
                        .map(PlannedRoute::getDriver, PlannedRoutePublicDTO::setDriver))
                .implicitMappings();

        Converter<Set<PlannedRoute>, List<PlannedRoutePublicDTO>> convertPRs
                = new AbstractConverter<>() {
            @Override
            protected List<PlannedRoutePublicDTO> convert(Set<PlannedRoute> plannedRoutes) {
                return plannedRoutes.stream().map(plannedRoute -> modelMapper.map(plannedRoute,
                        PlannedRoutePublicDTO.class)).collect(Collectors.toList());
            }
        };

        Converter<List<Ride>, List<RideReturnDTO>> convertRides
                = new AbstractConverter<>() {
            @Override
            protected List<RideReturnDTO> convert(List<Ride> rides) {
                return rides.stream().map(ride -> modelMapper.map(ride,
                        RideReturnDTO.class)).collect(Collectors.toList());
            }
        };

        modelMapper.addConverter(convertPRs);
        modelMapper.addConverter(convertRides);

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
