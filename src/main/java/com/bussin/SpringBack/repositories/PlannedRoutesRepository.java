package com.bussin.SpringBack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.SpringBack.models.PlannedRoute;

import java.util.Optional;
import java.util.UUID;

public interface PlannedRoutesRepository
        extends JpaRepository<PlannedRoute, UUID> {
        Optional<PlannedRoute> findPlannedRouteById(UUID uuid);
}
