package com.bussin.SpringBack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.SpringBack.models.PlannedRoute;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlannedRoutesRepository
        extends JpaRepository<PlannedRoute, UUID> {
        Optional<PlannedRoute> findPlannedRouteById(UUID uuid);
        Optional<PlannedRoute> findPlannedRouteByCapacity(Integer capacity);
        List<PlannedRoute> findPlannedRouteByDateTime(LocalDateTime localDateTime);
}
