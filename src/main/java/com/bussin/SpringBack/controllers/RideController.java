package com.bussin.SpringBack.controllers;

import javax.transaction.Transactional;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;


import com.bussin.SpringBack.services.RideService;
import com.bussin.SpringBack.models.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/ride")
public class RideController {
    private final RideService rideService;

    @Autowired
    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    /**
     * Get all rides
     * @return List of all rides
     */
    @Operation(summary = "Gets the details of all rides")
    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.getAllRides();
    }

    /**
     * Get a ride by a ride ID
     * @param rideId The UUID of ride
     * @return The ride if found, else null
     */
    @Operation(summary = "Gets a ride by its ID")
    @GetMapping("/{rideId}")
    public Ride getRideById(@Valid @PathVariable UUID rideId) {
        return rideService.getRideById(rideId);
    }

    /**
     * Create a new ride
     * @param creationDTO The ride creation DTO to create a new ride
     * @return The ride that is created
     */
    @Operation(summary = "Create a new ride")
    @Transactional
    @PostMapping
    public Ride createNewRide(@Valid @RequestBody RideCreationDTO creationDTO) {
        return rideService.createNewRide(creationDTO.getRideDTO(),
                creationDTO.getUserUUID(), creationDTO.getPlannedRouteUUID());
    }

    /**
     * Update a ride by ID
     * @param rideId The UUID of ride to be updated
     * @param rideDTO The ride DTO details to update
     * @return Updated Ride
     */
    @Operation(summary = "Update a ride by its ID")
    @Transactional
    @PutMapping("/{rideId}")
    public Ride updateRideById
            (@Valid @PathVariable UUID rideId,
            @Valid @RequestBody RideDTO rideDTO) {
        return rideService.updateRideById(rideId, rideDTO);
    }

    /**
     * Delete a ride by ID
     * @param rideId The UUID of ride
     * @return Deleted Ride
     */
    @Operation(summary = "Delete a ride by its ID")
    @Transactional
    @DeleteMapping("/{rideId}")
    public Ride deleteRideById(@Valid @PathVariable UUID rideId) {
        return rideService.deleteRideById(rideId);
    }
}
