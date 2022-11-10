package com.bussin.SpringBack.repositories;

import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlannedRoutesRepository
        extends JpaRepository<PlannedRoute, UUID> {
    Optional<PlannedRoute> findPlannedRouteById(UUID uuid);

    List<PlannedRoute> findPlannedRouteByDateTimeAfter(LocalDateTime localDateTime);
}
