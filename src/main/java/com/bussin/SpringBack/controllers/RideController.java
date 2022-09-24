package com.bussin.SpringBack.controllers;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bussin.SpringBack.services.RideService;
import com.bussin.SpringBack.models.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/ride")
public class RideController {
    private final RideService rideService;

    @Autowired
    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.getAllRides();
    }

    @GetMapping("/{rideId}")
    public Ride getRideById(@Valid @PathVariable UUID rideId) {
        return rideService.getRideById(rideId).orElse(null);
    }

    @Transactional
    @PostMapping("/{userId}/{plannedRouteId}")
    public Ride createNewRide(@RequestBody RideDTO rideDTO,
            @Valid @PathVariable UUID userId,
            @Valid @PathVariable UUID plannedRouteId) {
        return rideService.createNewRide(rideDTO, userId, plannedRouteId);
    }
}
