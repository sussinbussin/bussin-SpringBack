package com.bussin.SpringBack.services;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bussin.SpringBack.repositories.*;
import com.bussin.SpringBack.models.*;
import com.bussin.SpringBack.exception.*;
import java.util.*;

@Service
public class RideService {
    private final ModelMapper modelMapper;
    private final RideRepository rideRepository;
    private final PlannedRoutesRepository plannedRoutesRepository;
    private final UserService userService;

    @Autowired
    public RideService(ModelMapper modelMapper, RideRepository rideRepository,
            PlannedRoutesRepository plannedRoutesRepository, UserService userService) {
        this.modelMapper = modelMapper;
        this.rideRepository = rideRepository;
        this.plannedRoutesRepository = plannedRoutesRepository;
        this.userService = userService;
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Ride getRideById(UUID uuid) {
        return rideRepository.findById(uuid).orElseThrow(() ->
                new RideNotFoundException("No ride with id " + uuid));
    }

    @Transactional
    public Ride createNewRide(RideDTO rideDTO, UUID userId, UUID plannedRouteId) {
        rideDTO.validate();
        return userService.getFullUserById(userId).map(found -> {
            Ride ride = modelMapper.map(rideDTO, Ride.class);
            ride.setUser(found);
            PlannedRoute plannedRoute = plannedRoutesRepository
                    .findPlannedRouteById(plannedRouteId)
                    .orElseThrow(() ->
                            new PlannedRouteNotFoundException("No planned route with id " + plannedRouteId));
            ride.setPlannedRoute(plannedRoute);

            return rideRepository.save(ride);
        }).orElseThrow(() -> new UserNotFoundException(("No user with id " + userId)));
    }

    // @Transactional
    // public Ride updateRideById(UUID rideId,
    //         Ride ride) {
        
    // }

}
