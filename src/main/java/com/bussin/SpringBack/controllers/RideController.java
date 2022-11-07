package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.ride.Ride;
import com.bussin.SpringBack.models.ride.RideCreationDTO;
import com.bussin.SpringBack.models.ride.RideDTO;
import com.bussin.SpringBack.models.ride.RidePublicDTO;
import com.bussin.SpringBack.services.RideService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
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
     *
     * @return List of all rides
     */
    @Operation(summary = "Gets the details of all rides")
    @GetMapping
    public List<Ride> getAllRides() {
        log.info("Retrieving all rides");
        return rideService.getAllRides();
    }

    /**
     * Get a ride by a ride ID
     *
     * @param rideId The UUID of ride
     * @return The ride if found, else null
     */
    @Operation(summary = "Gets a ride by its ID")
    @GetMapping("/{rideId}")
    public RidePublicDTO getRideById(@Valid @PathVariable final UUID rideId) {
        log.info(String.format("Retrieving ride %s", rideId));
        return rideService.getRideById(rideId);
    }

    /**
     * Create a new ride
     *
     * @param creationDTO The ride creation DTO to create a new ride
     * @return The ride that is created
     */
    @Operation(summary = "Create a new ride")
    @Transactional
    @PostMapping
    public RidePublicDTO createNewRide(@Valid @RequestBody final RideCreationDTO creationDTO) {
        log.info(String.format("Creating ride %s", creationDTO));
        return rideService.createNewRide(creationDTO.getRideDTO(),
                creationDTO.getUserUUID(), creationDTO.getPlannedRouteUUID());
    }

    /**
     * Update a ride by ID
     *
     * @param rideId  The UUID of ride to be updated
     * @param rideDTO The ride DTO details to update
     * @return Updated Ride
     */
    @Operation(summary = "Update a ride by its ID")
    @Transactional
    @PutMapping("/{rideId}")
    public RidePublicDTO updateRideById
    (@Valid @PathVariable final UUID rideId,
     @Valid @RequestBody final RideDTO rideDTO) {
        log.info(String.format("Updating ride %s: %s", rideId, rideDTO));
        return rideService.updateRideById(rideId, rideDTO);
    }

    /**
     * Delete a ride by ID
     *
     * @param rideId The UUID of ride
     * @return Deleted Ride
     */
    @Operation(summary = "Delete a ride by its ID")
    @Transactional
    @DeleteMapping("/{rideId}")
    public RidePublicDTO deleteRideById(@Valid @PathVariable final UUID rideId) {
        log.info(String.format("Deleting ride %s", rideId));
        return rideService.deleteRideById(rideId);
    }
}
