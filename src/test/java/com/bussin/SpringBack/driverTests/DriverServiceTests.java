package com.bussin.SpringBack.driverTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.exception.UserNotFoundException;
import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.models.DriverDTO;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.repositories.DriverRepository;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTests {
    @Mock
    private DriverRepository driverRepository;

    @Mock
    private UserService userService;

    private ModelMapper modelMapper;

    @InjectMocks
    private DriverService driverService;

    @BeforeEach
    private void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<UserDTO, User>() {
            @Override
            protected void configure() {
                skip(destination.getDriver());
            }
        });

        driverService = new DriverService(driverRepository, modelMapper);
        driverService.setUserService(userService);
    }

    @Test
    public void addNewDriver_success() {

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        driverDTO.setCarPlate("SAA1234A");
        driverDTO.setModelAndColour("Yellow Submarine");
        driverDTO.setCapacity(4);

        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(false);

        User userResult = TestObjects.USER.clone();
        userResult.setId(UUID.randomUUID());
        userResult.setIsDriver(true);

        Driver driverResult = TestObjects.DRIVER.clone();
        driverResult.setUser(userResult);

        when(userService.getUserById(userDTO.getId()))
                .thenReturn(userDTO);
        when(userService.updateUser(userDTO.getId(), userDTO))
                .thenAnswer(invocationOnMock ->
                    ((UserDTO)invocationOnMock.getArgument(1)).getIsDriver()?
                            //Will end test if bad
                            userResult:null);
        when(driverRepository.save(driverResult))
                .thenReturn(driverResult);

        assertEquals(driverService.addNewDriver(userDTO.getId(), driverDTO), driverResult);

        verify(userService, times(1)).getUserById(userDTO.getId());
        verify(userService, times(1))
                .updateUser(userDTO.getId(), userDTO);
        verify(driverRepository, times(1)).save(driverResult);
    }

    @Test
    public void addNewDriver_invalidParams_exception() {
        UUID uuid = UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        driverDTO.setCapacity(1);

        assertThrows(ConstraintViolationException.class,
                () -> driverService.addNewDriver(uuid, driverDTO));

        verify(userService, never()).getUserById(uuid);
    }

    @Test
    public void addNewDriver_userNotFound_exception(){
        UUID uuid = UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        when(userService.getUserById(uuid)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class,
                () -> driverService.addNewDriver(uuid, driverDTO));

        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    public void updateDriver_success() {
        UUID uuid = UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        driverDTO.setCarPlate("SAA1234A");
        driverDTO.setCapacity(4);

        User userResult = TestObjects.USER.clone();
        userResult.setIsDriver(true);

        Driver driver = TestObjects.DRIVER.clone();
        driver.setUser(userResult);

        Driver driverResult = TestObjects.DRIVER.clone();
        driverResult.setUser(userResult);
        driverResult.setModelAndColour("Flamingo MrBean Car");

        userResult.setDriver(driver);
        when(driverRepository.findDriverByCarPlate(driverDTO.getCarPlate()))
                .thenReturn(Optional.of(driver));
        when(driverRepository.save(driverResult))
                .thenReturn(driverResult);

        assertEquals(driverService.updateDriver(driverDTO.getCarPlate(),
                driverDTO), driverResult);

        verify(driverRepository, times(1))
                .findDriverByCarPlate(driverDTO.getCarPlate());
        verify(driverRepository, times(1))
                .save(driverResult);
    }

    @Test
    public void updateDriver_nonUniqueParams_exception() {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        driverDTO.setCarPlate("SAA1234A");
        driverDTO.setModelAndColour("Yellow Submarine");
        driverDTO.setCapacity(4);

        User userResult = TestObjects.USER.clone();
        userResult.setIsDriver(true);

        Driver driver = TestObjects.DRIVER.clone();
        driver.setUser(userResult);

        Driver driverResult = TestObjects.DRIVER.clone();
        driverResult.setUser(userResult);

        userResult.setDriver(driver);

        when(driverRepository.findDriverByCarPlate(driverDTO.getCarPlate()))
                .thenReturn(Optional.of(driver));

        when(driverRepository.save(driverResult))
                .thenThrow(ConstraintViolationException.class);

        assertThrows(ConstraintViolationException.class,
                        () -> driverService.updateDriver(driver.getCarPlate(), driverDTO));

        verify(driverRepository, times(1)).findDriverByCarPlate(any(String.class));
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    public void deleteDriver_success() {

        UUID id = UUID.randomUUID();

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        driverDTO.setCarPlate("SAA1234A");
        driverDTO.setModelAndColour("Yellow Submarine");
        driverDTO.setCapacity(4);

        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setId(id);
        userDTO.setIsDriver(false);

        User userResult = TestObjects.USER.clone();
        userResult.setId(id);
        userResult.setIsDriver(true);

        User userResultGood = TestObjects.USER.clone();
        userResultGood.setId(id);

        Driver driverResult = TestObjects.DRIVER.clone();
        driverResult.setUser(userResult);

        userResult.setDriver(driverResult);

        when(driverRepository.findDriverByCarPlate(driverResult.getCarPlate()))
                .thenReturn(Optional.of(driverResult));

        when(userService.updateUser(userDTO.getId(), userDTO))
                .thenAnswer(invocationOnMock ->
                        ((UserDTO)invocationOnMock.getArgument(1)).getIsDriver()?
                                //Will end test if bad
                                userResultGood:null);

        assertEquals(driverService
                .deleteDriver(driverDTO.getCarPlate()), driverResult);

        verify(driverRepository, times(1))
                .findDriverByCarPlate(driverResult.getCarPlate());
        verify(userService, times(1))
                .updateUser(userDTO.getId(), userDTO);
    }

     @Test
     public void deleteDriver_doesntExist_exception() {
        String carPlate = "SAA1234B";
        when(driverRepository.findDriverByCarPlate(carPlate))
                .thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class,
                () -> driverService.deleteDriver(carPlate));

        verify(driverRepository, times(1)).findDriverByCarPlate(carPlate);
        verify(driverRepository, never()).deleteByCarPlate(carPlate);
     }
}
