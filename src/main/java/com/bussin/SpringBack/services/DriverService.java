package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.exception.UserNotFoundException;
import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.models.DriverDTO;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.repositories.DriverRepository;
import com.bussin.SpringBack.repositories.UserRepository;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DriverService {
    private ModelMapper modelMapper;

    @Setter
    private UserService userService;

    private final DriverRepository driverRepository;

    @Autowired
    public DriverService(DriverRepository driverRepository,
                         ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.driverRepository = driverRepository;
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Transactional
    public Driver addNewDriver(UUID uuid, DriverDTO driverDTO) {
        driverDTO.validate();
        return userService.getUserById(uuid).map(foundUser -> {
            foundUser.setIsDriver(true);
            Driver driver = modelMapper.map(driverDTO,
                    Driver.class);
            User user = userService.updateUser(uuid, foundUser);
            user.setDriver(driver);
            driver.setUser(user);
            return driverRepository.save(driver);
        }).orElseThrow(() -> new UserNotFoundException("No user found with id" +
                " " + uuid));
    }

    public Driver getDriverByCarPlate(String carPlate) {
        return driverRepository.findDriverByCarPlate(carPlate)
                .orElseThrow(() ->
                        new UserNotFoundException("No driver found with car " +
                                "plate " + carPlate));
    }

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
            driverRepository.deleteByCarPlate(carPlate);
            UserDTO toUpdate =
                    modelMapper.map(found.getUser(), UserDTO.class);
            toUpdate.setIsDriver(false);
            userService.updateUser(found.getUser().getId(), toUpdate);
            return found;
        }).orElseThrow(() -> new DriverNotFoundException(
                "No driver with car plate " + carPlate));
    }
}
