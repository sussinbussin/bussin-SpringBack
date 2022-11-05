package com.bussin.SpringBack.config;

import com.bussin.SpringBack.models.driver.Driver;
import com.bussin.SpringBack.models.driver.DriverPublicDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoutePublicDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteResultDTO;
import com.bussin.SpringBack.models.ride.Ride;
import com.bussin.SpringBack.models.ride.RidePublicDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

        /**
         * Map Planned Route's driver to the DTO's carPlate field
         * Map Planned Route's rides to the DTO's ride IDs
         */
        modelMapper.emptyTypeMap(PlannedRoute.class, PlannedRouteResultDTO.class)
                .addMappings(mapper ->
                        mapper.map(src -> src.getDriver().getCarPlate(),
                                PlannedRouteResultDTO::setCarPlate))
                .addMappings(mapper ->
                        mapper.map(PlannedRoute::getRides,
                                PlannedRouteResultDTO::setRides))
                .implicitMappings();

        /**
         *  Map driver's planned routes to the DTO's planned route DTOs
         *  Map Ride's userID to DTO's userID
         */
        modelMapper.emptyTypeMap(Driver.class, DriverPublicDTO.class)
                .addMappings(mapper -> mapper
                        .map(Driver::getPlannedRoutes,
                                DriverPublicDTO::setPlannedRoutes))
                .addMappings(mapper -> mapper.map(src -> src.getUser().getId(), DriverPublicDTO::setUser))
                .implicitMappings();

        /**
         * Map Ride's planned route to DTO's planned route DTO
         * Map Ride's userID to DTO's userID
         */
        modelMapper.emptyTypeMap(Ride.class, RidePublicDTO.class)
                .addMappings(mapper -> mapper
                        .map(Ride::getPlannedRoute,
                                RidePublicDTO::setPlannedRoute))
                .addMappings(mapper -> mapper.map(src -> src.getUser().getId(), RidePublicDTO::setUserId))
                .implicitMappings();


        /**
         * Map Planned Route's driver to the DTO's carPlate field
         * Map Planned Route's rides to the DTO's planned route DTOs
         */
        modelMapper.emptyTypeMap(PlannedRoute.class, PlannedRoutePublicDTO.class)
                .addMappings(mapper -> mapper
                        .map(PlannedRoute::getRides,
                                PlannedRoutePublicDTO::setRides))
                .addMappings(mapper -> mapper
                        .map(PlannedRoute::getDriver, PlannedRoutePublicDTO::setDriver))
                .implicitMappings();

        /**
         * Converter from a set of Planned Routes to a List of DTOs
         */
        Converter<Set<PlannedRoute>, List<PlannedRoutePublicDTO>> convertPRs
                = new AbstractConverter<>() {
            @Override
            protected List<PlannedRoutePublicDTO> convert(Set<PlannedRoute> plannedRoutes) {
                return plannedRoutes.stream().map(plannedRoute -> modelMapper.map(plannedRoute,
                        PlannedRoutePublicDTO.class)).collect(Collectors.toList());
            }
        };

        /**
         * Converter from a List of Rides to a List of DTOs
         */
        Converter<List<Ride>, List<RidePublicDTO>> convertRides
                = new AbstractConverter<>() {
            @Override
            protected List<RidePublicDTO> convert(List<Ride> rides) {
                return rides.stream().map(ride -> modelMapper.map(ride,
                        RidePublicDTO.class)).collect(Collectors.toList());
            }
        };

        Converter<List<Ride>, List<UUID>> convertRideToUUID
                = new AbstractConverter<>() {
            @Override
            protected List<UUID> convert(List<Ride> rides) {
                return rides == null ? new ArrayList<>() :
                        rides.stream().map(Ride::getId)
                                .collect(Collectors.toList());
            }
        };

        modelMapper.addConverter(convertPRs);
        modelMapper.addConverter(convertRides);
        modelMapper.addConverter(convertRideToUUID);

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
