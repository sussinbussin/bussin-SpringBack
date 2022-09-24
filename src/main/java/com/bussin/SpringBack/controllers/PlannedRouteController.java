package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.PlannedRouteDTO;
import com.bussin.SpringBack.services.PlannedRouteService;
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

    @GetMapping
    public List<PlannedRoute> getAllPlannedRoutes() {
        return plannedRouteService.getAllPlannedRoutes();
    }

    @GetMapping("/{routeId}")
    public PlannedRoute getPlannedRouteById(@Valid @PathVariable UUID routeId) {
        return plannedRouteService.getPlannedRouteById(routeId).orElse(null);
    }

    @Transactional
    @PostMapping("/{carPlate}")
    public PlannedRoute createNewPlannedRoute
            (@RequestBody @Valid PlannedRouteDTO plannedRouteDTO,
             @PathVariable String carPlate) {
        return plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                carPlate);
    }

    @Transactional
    @PutMapping("/{routeId}")
    public PlannedRoute updatePlannedRouteById
            (@Valid @PathVariable UUID routeId,
             @Valid @RequestBody PlannedRouteDTO plannedRouteDTO) {
        return plannedRouteService.updatePlannedRouteById(routeId,
                plannedRouteDTO);
    }

    @Transactional
    @DeleteMapping("/{routeId}")
    public PlannedRoute deletePlannedrouteById(@Valid @PathVariable UUID routeId) {
        return plannedRouteService.deletePlannedRouteByID(routeId);
    }
}
