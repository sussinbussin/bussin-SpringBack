package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.repositories.DriverRepository;
import com.bussin.SpringBack.services.DriverService;

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

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/v1/driver")
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
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    /**
     * Gets a driver by their car plate
     * @param carPlate The String
     * @return The driver if found, else null
     */
    @GetMapping("/{carPlate}")
    public Driver getDriverByCarPlate(@Valid @PathVariable String carPlate) {
        return driverService.getDriverByCarPlate(carPlate).orElse(null);
    }
    
    /**
     * Add new driver object
     * @param driver Driver object to add
     * @return A driver that was added.
     */
    @PostMapping
    public Driver addNewDriver(@Valid @RequestBody Driver driver) {
        return driverService.addNewDriver(driver);
    }

    @PutMapping("/{carPlate}")
    public Driver getDriverByCarPlate(@Valid @PathVariable String carPlate, @RequestBody Driver driver) {
        return driverService.updateDriver(carPlate, driver);
    }

    @DeleteMapping("/{carPlate}")
    public Driver deleteDriverByCarPlate(@Valid @PathVariable String carPlate) {
        return driverService.deleteDriver(carPlate);
    }
}
