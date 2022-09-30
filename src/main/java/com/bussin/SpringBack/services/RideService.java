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
    private final GasPriceService gasPriceService;

    @Autowired
    public RideService(ModelMapper modelMapper, RideRepository rideRepository,
            PlannedRoutesRepository plannedRoutesRepository,
                       UserService userService, GasPriceService gasPriceService) {
        this.modelMapper = modelMapper;
        this.rideRepository = rideRepository;
        this.plannedRoutesRepository = plannedRoutesRepository;
        this.userService = userService;
        this.gasPriceService = gasPriceService;
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
        User found =  userService.getFullUserById(userId);
        Ride ride = modelMapper.map(rideDTO, Ride.class);
        PlannedRoute plannedRoute = plannedRoutesRepository
                .findPlannedRouteById(plannedRouteId)
                .orElseThrow(() ->
                        new PlannedRouteNotFoundException("No planned route with id " + plannedRouteId));

        ride.setCost(gasPriceService.getAvgGasPriceByType(
                GasPriceKey.GasType.valueOf(plannedRoute.getDriver().getFuelType())));
        ride.setUser(found);

        //TODO: Check that passengers does not exceed capacity

        ride.setPlannedRoute(plannedRoute);
        return rideRepository.save(ride);

    }

    @Transactional
    public Ride updateRideById(UUID rideId, RideDTO rideDTO) {
        rideDTO.setId(rideId);
        rideDTO.validate();
        return rideRepository.findById(rideId).map(found -> {
            found.updateFromDTO(rideDTO);
            return rideRepository.save(found);
        }).orElseThrow(() -> new RideNotFoundException("No ride with ID " + rideId));
    }

    @Transactional
    public Ride deleteRideById(UUID rideId) {
        return rideRepository.findById(rideId).map(found -> {
            rideRepository.deleteById(rideId);
            return found;
        }).orElseThrow(() -> new RideNotFoundException("No ride with ID " + rideId));
    }

}
