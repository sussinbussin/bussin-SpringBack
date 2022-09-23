package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import javax.transaction.Transactional;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    @Autowired
    public DriverService (DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver addNewDriver(Driver driver) {
        return driverRepository.save(driver);
    }
    
    public Optional<Driver> getDriverByCarPlate(String carPlate) {
        return driverRepository.findDriverByCarPlate(carPlate);
    }

    public Driver updateDriver(String carPlate, Driver driver) {
        return driverRepository.findDriverByCarPlate(carPlate).map( found -> {
            driver.setCarPlate(carPlate);
            return driverRepository.save(driver);
        }).orElseThrow(() -> new DriverNotFoundException("No driver with car plate " + carPlate));
    }

    public Driver deleteDriver(String carPlate) {
        return driverRepository.findDriverByCarPlate(carPlate).map( found -> {
            driverRepository.deleteByCarPlate(carPlate);
            return found;
        }).orElseThrow(()-> new DriverNotFoundException("No driver with car plate " + carPlate));
    }
}
