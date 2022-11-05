package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoutePublicDTO;
import com.bussin.SpringBack.models.user.UserPublicDTO;
import com.bussin.SpringBack.services.PlannedRouteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
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
        log.info("Retrieving all planned routes");
        return plannedRouteService.getAllPlannedRoutes();
    }

    /**
     * Get a planned route by ID
     * @param routeId The UUID of planned route
     * @return Planned route if found
     */
    @Operation(summary = "Gets planned route by ID")
    @GetMapping("/{routeId}")
    public PlannedRoutePublicDTO getPlannedRouteById(@Valid @PathVariable UUID routeId) {
        log.info(String.format("Retrieving planned route %s", routeId));
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
        log.info(String.format("Retrieving passengers on planned route %s",
                routeId));
        return plannedRouteService.getPassengersOnRoute(routeId);
    }

    /**
     * Get a planned route after specific time
     * @param dateTime The LocalDateTime
     * @return List of planned routes after datetime
     */
    @Operation(summary = "Gets all routes after specific time")
    @GetMapping("/after/{dateTime}")
    public List<PlannedRoutePublicDTO> getPlannedRouteAfterTime(@Valid @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        log.info(String.format("Retrieving planned routes after %s", dateTime));
        return plannedRouteService.getPlannedRouteAfterTime(dateTime);
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
        log.info(String.format("Creating planned route %s", plannedRouteDTO));
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
    public PlannedRoutePublicDTO updatePlannedRouteById(@Valid @PathVariable UUID routeId,
            @Valid @RequestBody PlannedRouteDTO plannedRouteDTO) {
        log.info(String.format("Updating planned route %s: %s",
                routeId, plannedRouteDTO));
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
    public PlannedRoutePublicDTO deletePlannedRouteById(@Valid @PathVariable UUID routeId) {
        log.info(String.format("Deleting planned route %s", routeId));
        return plannedRouteService.deletePlannedRouteByID(routeId);
    }
}
