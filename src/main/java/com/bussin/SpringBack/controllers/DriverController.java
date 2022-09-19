package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.repositories.DriverRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/driver")
public class DriverController {
    private DriverRepository drivers;

    public DriverController(DriverRepository drivers) {
        this.drivers = drivers;
    }

    /**
     * Gets all drivers.
     * 
     * @return List of all drivers.
     */
    @GetMapping
    public List<Driver> getAllDrivers() {
        return drivers.findAll();
    }

}
