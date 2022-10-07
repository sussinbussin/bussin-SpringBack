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
import javax.validation.constraints.Size;
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

    @Operation(summary = "Gets all planned routes")
    @GetMapping
    public List<PlannedRoute> getAllPlannedRoutes() {
        return plannedRouteService.getAllPlannedRoutes();
    }

    @Operation(summary = "Gets planned route by ID")
    @GetMapping("/{routeId}")
    public PlannedRoute getPlannedRouteById(@Valid @PathVariable UUID routeId) {
        return plannedRouteService.getPlannedRouteById(routeId);
    }

    @Operation(summary = "Gets passengers on planned route")
    @GetMapping("/{routeId}/passengers")
    public List<UserPublicDTO> getPassengersOnRoute(@Valid @PathVariable UUID routeId) {
        return plannedRouteService.getPassengersOnRoute(routeId);
    }

    @Operation(summary = "Gets all routes after specific time")
    @GetMapping("/after/{dateTime}")
    public List<PlannedRoute> getPlannedRouteAfterTime(@Valid @PathVariable LocalDateTime dateTime) {
        return plannedRouteService.getPlannedRouteAfterTime(dateTime);
    }

    @Operation(summary = "Gets distance between two addresses")
    @GetMapping("/distance")
    public BigDecimal getDistanceBetween(@Pattern(regexp = "^[0-9]{6}$") @PathVariable String tripStart,
            @Pattern(regexp = "^[0-9]{6}$") @PathVariable String tripEnd) {
        return plannedRouteService.getDistanceBetween(tripStart, tripEnd);
    }

    @Operation(summary = "Gets best planned routes given a start and " +
            "destination")
    @GetMapping("/suggestion")
    public List<PlannedRoute> getSuggestedRoutes(@RequestParam String tripStart,
            @RequestParam String tripEnd) {
        return plannedRouteService.getSuggestedRoutes(tripStart, tripEnd);
    }

    @Operation(summary = "Creates a planned route for a Driver")
    @Transactional
    @PostMapping("/{carPlate}")
    public PlannedRoute createNewPlannedRoute(@RequestBody @Valid PlannedRouteDTO plannedRouteDTO,
            @PathVariable String carPlate) {
        return plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                carPlate);
    }

    @Operation(summary = "Updates a planned route")
    @Transactional
    @PutMapping("/{routeId}")
    public PlannedRoute updatePlannedRouteById(@Valid @PathVariable UUID routeId,
            @Valid @RequestBody PlannedRouteDTO plannedRouteDTO) {
        return plannedRouteService.updatePlannedRouteById(routeId,
                plannedRouteDTO);
    }

    @Operation(summary = "Deletes a planned route by ID")
    @Transactional
    @DeleteMapping("/{routeId}")
    public PlannedRoute deletePlannedrouteById(@Valid @PathVariable UUID routeId) {
        return plannedRouteService.deletePlannedRouteByID(routeId);
    }
}
