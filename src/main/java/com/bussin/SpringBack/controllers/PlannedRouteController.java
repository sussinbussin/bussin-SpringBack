package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.PlannedRouteDTO;
import com.bussin.SpringBack.models.UserPublicDTO;
import com.bussin.SpringBack.services.PlannedRouteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/planned")
public class PlannedRouteController {
    private final PlannedRouteService plannedRouteService;

    @Autowired
    public PlannedRouteController(PlannedRouteService plannedRouteService) {
        this.plannedRouteService = plannedRouteService;
    }

    /**
     * Get all planned routes
     * @return List of planned routes
     */
    @Operation(summary = "Gets all planned routes")
    @GetMapping
    public List<PlannedRoute> getAllPlannedRoutes() {
        return plannedRouteService.getAllPlannedRoutes();
    }

    /**
     * Get a planned route by ID
     * @param routeId The UUID of planned route
     * @return Planned route if found
     */
    @Operation(summary = "Gets planned route by ID")
    @GetMapping("/{routeId}")
    public PlannedRoute getPlannedRouteById(@Valid @PathVariable UUID routeId) {
        return plannedRouteService.getPlannedRouteById(routeId);
    }

    /**
     * Get all passengers on a particular route
     * @param routeId The UUID of planned route
     * @return List of user public DTO if planned route is found
     */
    @Operation(summary = "Gets passengers on planned route")
    @GetMapping("/{routeId}/passengers")
    public List<UserPublicDTO> getPassengersOnRoute(@Valid @PathVariable UUID routeId) {
        return plannedRouteService.getPassengersOnRoute(routeId);
    }

    /**
     * Get a planned route after specific time
     * @param dateTime The LocalDateTime
     * @return List of planned routes after datetime
     */
    @Operation(summary = "Gets all routes after specific time")
    @GetMapping("/after/{dateTime}")
    public List<PlannedRoute> getPlannedRouteAfterTime(@Valid @PathVariable LocalDateTime dateTime) {
        return plannedRouteService.getPlannedRouteAfterTime(dateTime);
    }

    /**
     * Get distance between the trip starts and trip ends
     * @param tripStart The String
     * @param tripEnd The String
     * @return The distance of between the start and end of trip
     */
    @Operation(summary = "Gets distance between two addresses")
    @GetMapping("/distance")
    public BigDecimal getDistanceBetween(@Pattern(regexp = "^[0-9]{6}$") @PathVariable String tripStart,
            @Pattern(regexp = "^[0-9]{6}$") @PathVariable String tripEnd) {
        return plannedRouteService.getDistanceBetween(tripStart, tripEnd);
    }

    /**
     * Create a new planned route
     * @param plannedRouteDTO The PlannedRouteDTO with details to create
     * @param carPlate The String of driver that created the planned route
     * @return Created PlannedRoute
     */
    @Operation(summary = "Creates a planned route for a Driver")
    @Transactional
    @PostMapping("/{carPlate}")
    public PlannedRoute createNewPlannedRoute(@RequestBody @Valid PlannedRouteDTO plannedRouteDTO,
            @PathVariable String carPlate) {
        return plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                carPlate);
    }

    /**
     * Update a planned route by ID
     * @param routeId The UUID of Planned Route to be updated
     * @param plannedRouteDTO The PlannedRouteDTO details to update
     * @return Updated Planned Route
     */
    @Operation(summary = "Updates a planned route")
    @Transactional
    @PutMapping("/{routeId}")
    public PlannedRoute updatePlannedRouteById(@Valid @PathVariable UUID routeId,
            @Valid @RequestBody PlannedRouteDTO plannedRouteDTO) {
        return plannedRouteService.updatePlannedRouteById(routeId,
                plannedRouteDTO);
    }

    /**
     * Delete a planned route by ID
     * @param routeId The UUID of planned route
     * @return Deleted Planned Route
     */
    @Operation(summary = "Deletes a planned route by ID")
    @Transactional
    @DeleteMapping("/{routeId}")
    public PlannedRoute deletePlannedRouteById(@Valid @PathVariable UUID routeId) {
        return plannedRouteService.deletePlannedRouteByID(routeId);
    }
}
