package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.driver.Driver;
import com.bussin.SpringBack.models.driver.DriverDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoutePublicDTO;
import com.bussin.SpringBack.services.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static com.bussin.SpringBack.utils.NotAuthorizedUtil.isSameDriver;
import static com.bussin.SpringBack.utils.NotAuthorizedUtil.isSameUserId;

@Slf4j
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
        log.info(String.format("Retrieving all drivers"));
        return driverService.getAllDrivers();
    }

    /**
     * Gets a driver by their car plate
     *
     * @param carPlate The String
     * @return The driver if found, else null
     */
    @Operation(summary = "Gets a Driver by their Car Plate")
    @GetMapping("/{carPlate}")
    public Driver getDriverByCarPlate(@Valid @PathVariable String carPlate) {
        isSameDriver(carPlate);
        log.info(String.format("Retrieving driver %s", carPlate));
        return driverService.getDriverByCarPlate(carPlate);
    }

    /**
     * Gets all the planned routes by driver's car plate
     * @param carPlate The String
     * @return A set of planned routes if  found, else null
     */
    @Operation
    @GetMapping("/{carPlate}/plannedRoutes")
    public List<PlannedRoutePublicDTO> getAllPlannedRoutesByDriver(@Valid @PathVariable String carPlate){
        isSameDriver(carPlate);
        log.info(String.format("Retrieving planned routes from driver %s",
                carPlate));
        return driverService.getAllPlannedRoutesByDriver(carPlate);
    }

    /**
     * Create a new driver
     * @param userUUID The UUID of user that is going to be a driver
     * @param driverDTO The driver DTO to be created
     * @return The Driver that is created
     */
    @Operation(summary = "Converts a User to Driver")
    @PostMapping("/{userUUID}")
    public Driver addNewDriver(@Valid @PathVariable UUID userUUID,
                               @Valid @RequestBody DriverDTO driverDTO) {
        isSameUserId(userUUID);
        log.info(String.format("Creating new driver for %s: %s", userUUID, driverDTO));
        return driverService.addNewDriver(userUUID, driverDTO);
    }

    /**
     * Update a Driver Object.
     *
     * @param carPlate  Car plate of the Driver to update
     * @param driverDTO DriverDTO with the information to update
     * @return Updated Driver
     */
    @Operation(summary = "Updates a Driver")
    @PutMapping("/{carPlate}")
    public Driver updateDriverByCarPlate(@Valid @PathVariable String carPlate,
                                         @Valid @RequestBody DriverDTO driverDTO) {
        isSameDriver(carPlate);
        log.info(String.format("Updating driver for %s: %s", carPlate,
                driverDTO));
        return driverService.updateDriver(carPlate, driverDTO);
    }

    /**
     * Delete a Driver and amend the User.
     *
     * @param carPlate Car plate number of the Driver to delete
     * @return Deleted Driver
     */
    @Operation(summary = "Converts a Driver into User")
    @DeleteMapping("/{carPlate}")
    public Driver deleteDriverByCarPlate(@Valid @PathVariable String carPlate) {
        isSameDriver(carPlate);
        log.info(String.format("Deleting driver %s", carPlate));
        return driverService.deleteDriver(carPlate);
    }
}
