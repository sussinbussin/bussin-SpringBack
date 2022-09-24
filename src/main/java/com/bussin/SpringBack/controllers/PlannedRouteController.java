package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.PlannedRouteDTO;
import com.bussin.SpringBack.services.PlannedRouteService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/planned")
public class PlannedRouteController {
    private final PlannedRouteService plannedRouteService;

    @Autowired
    public PlannedRouteController(PlannedRouteService plannedRouteService) {
        this.plannedRouteService = plannedRouteService;
    }

    @Operation(summary = "Get all the planned routes")
    @GetMapping
    public List<PlannedRoute> getAllPlannedRoutes() {
        return plannedRouteService.getAllPlannedRoutes();
    }

    @Operation(summary = "Gets a planned route by its ID")
    @GetMapping("/{routeId}")
    public PlannedRoute getPlannedRouteById(@Valid @PathVariable UUID routeId) {
        return plannedRouteService.getPlannedRouteById(routeId).orElse(null);
    }

    @Operation(summary = "Create a new planned route")
    @Transactional
    @PostMapping("/{carPlate}")
    public PlannedRoute createNewPlannedRoute
            (@RequestBody @Valid PlannedRouteDTO plannedRouteDTO,
             @PathVariable String carPlate) {
        return plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                carPlate);
    }

    @Operation(summary = "Update a planned route by ID")
    @Transactional
    @PutMapping("/{routeId}")
    public PlannedRoute updatePlannedRouteById
            (@Valid @PathVariable UUID routeId,
             @Valid @RequestBody PlannedRouteDTO plannedRouteDTO) {
        return plannedRouteService.updatePlannedRouteById(routeId,
                plannedRouteDTO);
    }

    @Operation(summary = "Delete a planned route by its ID")
    @Transactional
    @DeleteMapping("/{routeId}")
    public PlannedRoute deletePlannedrouteById(@Valid @PathVariable UUID routeId) {
        return plannedRouteService.deletePlannedRouteByID(routeId);
    }
}
