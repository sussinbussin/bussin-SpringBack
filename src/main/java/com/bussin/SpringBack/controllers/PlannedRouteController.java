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

    @Operation(summary = "Gets all planned routes")
    @GetMapping
    public List<PlannedRoute> getAllPlannedRoutes() {
        return plannedRouteService.getAllPlannedRoutes();
    }

    @Operation(summary = "Gets planned route by ID")
    @GetMapping("/{uuid}")
    public PlannedRoute getPlannedRouteById(@Valid @PathVariable UUID uuid) {
        return plannedRouteService.getPlannedRouteById(uuid);
    }

    @Operation(summary = "Creates a planned route for a Driver")
    @Transactional
    @PostMapping("/{carPlate}")
    public PlannedRoute createNewPlannedRoute
            (@RequestBody @Valid PlannedRouteDTO plannedRouteDTO,
             @PathVariable String carPlate) {
        return plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                carPlate);
    }

    @Operation(summary = "Updates a planned route")
    @Transactional
    @PutMapping("/{uuid}")
    public PlannedRoute updatePlannedRouteById
            (@Valid @PathVariable UUID uuid,
             @Valid @RequestBody PlannedRouteDTO plannedRouteDTO) {
        return plannedRouteService.updatePlannedRouteById(uuid,
                plannedRouteDTO);
    }

    @Operation(summary = "Deletes a planned route by ID")
    @Transactional
    @DeleteMapping("/{uuid}")
    public PlannedRoute deletePlannedrouteById(@Valid @PathVariable UUID uuid) {
        return plannedRouteService.deletePlannedRouteByID(uuid);
    }
}
