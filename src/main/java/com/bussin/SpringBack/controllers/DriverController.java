package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.models.DriverDTO;
import com.bussin.SpringBack.repositories.DriverRepository;
import com.bussin.SpringBack.services.DriverService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/driver")
public class DriverController {
    private final DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    /**
     * Gets all drivers.
     * 
     * @return List of all drivers.
     */
    @Operation(summary = "Gets all drivers")
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    /**
     * Gets a driver by their car plate
     * @param carPlate The String
     * @return The driver if found, else null
     */
    @Operation(summary = "Gets a Driver by their Car Plate")
    @GetMapping("/{carPlate}")
    public Driver getDriverByCarPlate(@Valid @PathVariable String carPlate) {
        return driverService.getDriverByCarPlate(carPlate);
    }

    @Operation(summary = "Converts a User to Driver")
    @PostMapping
    public Driver addNewDriver(@Valid UUID userUUID,
                               @Valid @RequestBody DriverDTO driverDTO) {
        return driverService.addNewDriver(userUUID, driverDTO);
    }

    /**
     * Update a Driver Object.
     * @param carPlate Car plate of the Driver to update
     * @param driverDTO DriverDTO with the information to update
     * @return Updated Driver
     */
    @Operation(summary = "Updates a Driver")
    @PutMapping("/{carPlate}")
    public Driver getDriverByCarPlate(@Valid @PathVariable String carPlate,
                                      @Valid @RequestBody DriverDTO driverDTO) {
        return driverService.updateDriver(carPlate, driverDTO);
    }

    /**
     * Delete a Driver and amend the User.
     * @param carPlate Car plate number of the Driver to delete
     * @return Deleted Driver
     */
    @Operation(summary = "Converts a Driver into User")
    @DeleteMapping("/{carPlate}")
    public Driver deleteDriverByCarPlate(@Valid @PathVariable String carPlate) {
        return driverService.deleteDriver(carPlate);
    }
}
