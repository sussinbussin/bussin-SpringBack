package com.bussin.SpringBack.repositories;

import com.bussin.SpringBack.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.SpringBack.models.PlannedRoute;

public interface PlannedRoutesRepository extends JpaRepository<PlannedRoute, Driver> {

}
