package com.bussin.SpringBack.repositories;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bussin.SpringBack.models.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    Optional<Driver> findDriverByCarPlate(String carPlate);
    Driver deleteByCarPlate(String carPlate);
}
