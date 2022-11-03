package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.models.DriverDTO;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.repositories.DriverRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.Set;

@Service
public class DriverService {
    private final ModelMapper modelMapper;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private final DriverRepository driverRepository;

    @Autowired
    public DriverService(DriverRepository driverRepository,
                         ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.driverRepository = driverRepository;
    }

    /**
     * Get all drivers
     * @return List of all drivers
     */
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    /**
     * Create a new driver
     * @param uuid The UUID of User
     * @param driverDTO The driver DTO of car details
     * @return The driver if created
     */
    @Transactional
    public Driver addNewDriver(UUID uuid, DriverDTO driverDTO) {
        driverDTO.validate();
        UserDTO foundUser = userService.getUserById(uuid);

        foundUser.setIsDriver(true);

        Driver driver = modelMapper.map(driverDTO, Driver.class);
        driver.setUser(userService.updateUser(uuid, foundUser));

        return driverRepository.save(driver);
    }

    /**
     * Get a driver by car plate
     * @param carPlate The String of Driver's car plate
     * @return The driver, if found
     */
    public Driver getDriverByCarPlate(String carPlate) {
        return driverRepository.findDriverByCarPlate(carPlate)
                .orElseThrow(() ->
                        new DriverNotFoundException(
                                "No driver found with car plate " + carPlate));
    }

    /**
     * Get the set of planned routes created by a driver
     * @param carPlate The String of Driver's car plate
     * @return A set of planned routes, if found
     */
    public Set<PlannedRoute> getAllPlannedRoutesByDriver(String carPlate) {
        return driverRepository.findDriverByCarPlate(carPlate).map(Driver::getPlannedRoutes).orElseThrow(()
                -> new DriverNotFoundException("No driver with car plate " + carPlate));
    }

    /**
     * Update a driver with driver DTO details
     * @param carPlate The String of Driver's car plate
     * @param driverDTO The Driver DTO of new details
     * @return Updated driver
     */
    @Transactional
    public Driver updateDriver(String carPlate, DriverDTO driverDTO) {
        driverDTO.validate();
        return driverRepository.findDriverByCarPlate(carPlate).map(found -> {
            driverDTO.setCarPlate(carPlate);
            return driverRepository.save(found.updateFromDTO(driverDTO));
        }).orElseThrow(() -> new DriverNotFoundException(
                "No driver with car plate " + carPlate));
    }

    /**
     * Deletes a Driver and sets the parent User to not a Driver
     *
     * @param carPlate Car plate of the Driver to delete
     * @return Deleted Driver
     */
    @Transactional
    public Driver deleteDriver(String carPlate) {
        return driverRepository.findDriverByCarPlate(carPlate).map(found -> {
            UserDTO toUpdate = UserDTO.builder().build();
            modelMapper.map(found.getUser(), toUpdate);
            toUpdate.setIsDriver(false);

            UUID uuid = found.getUser().getId();
            userService.updateUser(uuid, toUpdate);
            driverRepository.deleteByCarPlate(carPlate);
            return found;
        }).orElseThrow(() -> new DriverNotFoundException(
                "No driver with car plate " + carPlate));
    }
}
