package com.bussin.SpringBack.controllers;

import javax.transaction.Transactional;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Gets the details of all rides")
    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.getAllRides();
    }

    @Operation(summary = "Gets a ride by its ID")
    @GetMapping("/{rideId}")
    public Ride getRideById(@Valid @PathVariable UUID rideId) {
        return rideService.getRideById(rideId);
    }

    @Operation(summary = "Creates a new ride")
    @Transactional
    @PostMapping
    public Ride createNewRide(@Valid @RequestBody RideCreationDTO creationDTO) {
        return rideService.createNewRide(creationDTO.getRideDTO(),
                creationDTO.getUserUUID(), creationDTO.getPlannedRouteUUID());
    }
}
