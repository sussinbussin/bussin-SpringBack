package com.bussin.SpringBack.driverTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.exception.UserNotFoundException;
import com.bussin.SpringBack.models.driver.Driver;
import com.bussin.SpringBack.models.driver.DriverDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteResultDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
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

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTests {
    @Mock
    private DriverRepository driverRepository;

    @Mock
    private UserService userService;

    private ModelMapper modelMapper;

    @InjectMocks
    private DriverService driverService;

    /**
     * Initializes new model mapper and driver service before each unit test
     */
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

    /**
     * Add new driver with valid parameters
     */
    @Test
    public void addNewDriver_success() {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        UserDTO userDTO = TestObjects.COGNITO_DRIVER_DTO.clone();
        userDTO.setIsDriver(false);

        User userResult = TestObjects.USER.clone();
        userResult.setId(UUID.randomUUID());
        userResult.setIsDriver(true);

        Driver driverResult = TestObjects.DRIVER.clone();
        driverResult.setUser(userResult);

        when(userService.getUserById(userDTO.getId()))
                .thenReturn(userDTO);
        when(userService.updateUser(any(UUID.class), any(UserDTO.class)))
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

    /**
     * Add new driver with invalid parameters
     */
    @Test
    public void addNewDriver_invalidParams_exception() {
        UUID uuid = UUID.randomUUID();

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        driverDTO.setCapacity(1);

        assertThrows(ConstraintViolationException.class,
                () -> driverService.addNewDriver(uuid, driverDTO));

        verify(userService, never()).getUserById(uuid);
    }

    /**
     * Add new driver when user is not found
     */
    @Test
    public void addNewDriver_userNotFound_exception(){
        UUID uuid = UUID.randomUUID();

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        when(userService.getUserById(uuid)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class,
                () -> driverService.addNewDriver(uuid, driverDTO));

        verify(driverRepository, never()).save(any(Driver.class));
    }

    /**
     * Get all the drivers
     */
    @Test
    public void getAllDrivers_success() {
        ArrayList<Driver> drivers = new ArrayList<>();

        drivers.add(TestObjects.DRIVER.clone());

        when(driverRepository.findAll()).thenReturn(drivers);

        assertEquals(driverService.getAllDrivers(), drivers);

        verify(driverRepository, times(1)).findAll();
    }

    /**
     * Get all drivers when there are no drivers
     */
    @Test
    public void getAllDrivers_noDrivers_success() {
        when(driverRepository.findAll()).thenReturn(new ArrayList<>());

        assertEquals(driverService.getAllDrivers(), new ArrayList<>());

        verify(driverRepository, times(1)).findAll();
    }

    /**
     * Get all planned routes by a driver
     */
     @Test
     public void getAllPlannedRoutesByDriver_success() {
         Driver driver = TestObjects.DRIVER.clone();

         List<PlannedRouteResultDTO> plannedRoutePublicResult = new ArrayList<>();
         Set<PlannedRoute> plannedRouteResult = new HashSet<>();

         ModelMapper modelMapper
                 = new ModelMapper();
         modelMapper.addMappings(new PropertyMap<PlannedRoute, PlannedRouteResultDTO>() {
             @Override
             protected void configure() {
                 map().setCarPlate(source.getDriver().getCarPlate());
             }
         });

         PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();
         plannedRoutePublicResult.add(modelMapper.map(plannedRoute,
                 PlannedRouteResultDTO.class));

         plannedRouteResult.add(plannedRoute);
         driver.setPlannedRoutes(plannedRouteResult);
         plannedRoute.setDriver(driver);

         when(driverRepository.findDriverByCarPlate(driver.getCarPlate())).thenReturn(Optional.of(driver));

         assertEquals(plannedRoutePublicResult.get(0).getCarPlate(),
                 driverService.getAllPlannedRoutesByDriver(driver.getCarPlate()).get(0).getCarPlate());

         verify(driverRepository, times(1)).findDriverByCarPlate(driver.getCarPlate());
     }

    /**
     * Update a driver with valid parameters
     */
    @Test
    public void updateDriver_success() {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        driverDTO.setModelAndColour("Flamingo MrBean Car");

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

    /**
     * Update a driver with not unique parameters
     */
    @Test
    public void updateDriver_nonUniqueParams_exception() {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

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

    /**
     * Delete a driver
     */
    @Test
    public void deleteDriver_success() {
        UUID id = UUID.randomUUID();

        UserDTO userDTO = TestObjects.COGNITO_DRIVER_DTO.clone();
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

        when(userService.updateUser(any(UUID.class), any(UserDTO.class)))
                .thenAnswer(invocationOnMock ->
                        ((UserDTO)invocationOnMock.getArgument(1)).getIsDriver()?
                                //Will end test if bad
                                userResultGood:null);

        assertEquals(driverService
                .deleteDriver(driverResult.getCarPlate()), driverResult);

        verify(driverRepository, times(1))
                .findDriverByCarPlate(driverResult.getCarPlate());
        verify(userService, times(1))
                .updateUser(any(UUID.class), any(UserDTO.class));
    }

    /**
     * Delete a driver that does not exist
     */
     @Test
     public void deleteDriver_doesntExist_exception() {
        String carPlate = "SAA1234B";
        when(driverRepository.findDriverByCarPlate(any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class,
                () -> driverService.deleteDriver(carPlate));

        verify(driverRepository, times(1)).findDriverByCarPlate(carPlate);
        verify(driverRepository, never()).deleteByCarPlate(carPlate);
     }
}
