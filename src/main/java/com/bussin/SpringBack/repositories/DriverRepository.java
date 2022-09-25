package com.bussin.SpringBack.repositories;

import com.bussin.SpringBack.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    Optional<Driver> findDriverByCarPlate(String carPlate);

    void deleteByCarPlate(String carPlate);
}
