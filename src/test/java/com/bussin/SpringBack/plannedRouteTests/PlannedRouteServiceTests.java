package com.bussin.SpringBack.plannedRouteTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.exception.PlannedRouteNotFoundException;
import com.bussin.SpringBack.models.driver.Driver;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteDTO;
import com.bussin.SpringBack.models.ride.Ride;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import com.bussin.SpringBack.models.user.UserPublicDTO;
import com.bussin.SpringBack.repositories.DriverRepository;
import com.bussin.SpringBack.repositories.PlannedRoutesRepository;
import com.bussin.SpringBack.services.PlannedRouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlannedRouteServiceTests {
    @Mock
    private PlannedRoutesRepository plannedRoutesRepository;

    @Mock
    private DriverRepository driverRepository;

    private ModelMapper modelMapper;

    @InjectMocks
    private PlannedRouteService plannedRouteService;

    /**
     * Initializes new model mapper and planned route service before each unit test
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

        plannedRouteService = new PlannedRouteService(modelMapper,
                plannedRoutesRepository, driverRepository);
    }

    /**
     * Get all planned routes when there are no routes
     */
    @Test
    public void getAllPlannedRoutes_noRoutes_success() {
        when(plannedRoutesRepository.findAll()).thenReturn(new ArrayList<>());

        assertEquals(plannedRouteService.getAllPlannedRoutes(),
                new ArrayList<>());

        verify(plannedRoutesRepository, times(1))
                .findAll();
    }

    /**
     * Get all planned routes
     */
    @Test
    public void getAllPlannedRoutes_success() {
        ArrayList<PlannedRoute> plannedRoutes = new ArrayList<>();

        plannedRoutes.add(TestObjects.PLANNED_ROUTE.clone());

        when(plannedRoutesRepository.findAll()).thenReturn(plannedRoutes);

        assertEquals(plannedRouteService.getAllPlannedRoutes(), plannedRoutes);

        verify(plannedRoutesRepository, times(1)).findAll();
    }

    /**
     * Create a new planned route with invalid parameters
     */
    @Test
    public void createNewPlannedRoute_invalidParams_exception() {
        //No capacity
        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();
        plannedRouteDTO.setCapacity(-1);

        Driver driver = TestObjects.DRIVER.clone();

        assertThrows(ConstraintViolationException.class,
                () -> plannedRouteService.createNewPlannedRoute(plannedRouteDTO, driver.getCarPlate()));
        verify(plannedRoutesRepository, never()).save(any(PlannedRoute.class));
    }

    /**
     * Get all passengers on a route
     */
    @Test
    public void getPassengersOnRoute_success() {
        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();
        HashSet<Ride> rides = new HashSet<>();

        User user1 = TestObjects.USER.clone();
        user1.setId(UUID.randomUUID());

        Ride ride1 = TestObjects.RIDE.clone();
        ride1.setUser(user1);
        ride1.setPlannedRoute(plannedRoute);

        User user2 = TestObjects.USER.clone();
        user2.setId(UUID.randomUUID());
        user2.setEmail("testguy2@test.com");

        Ride ride2 = TestObjects.RIDE.clone();
        ride2.setId(UUID.randomUUID());
        ride2.setPlannedRoute(plannedRoute);
        ride2.setUser(user2);

        rides.add(ride1);
        rides.add(ride2);
        plannedRoute.setRides(rides);

        when(plannedRoutesRepository.findById(plannedRoute.getId()))
                .thenReturn(Optional.of(plannedRoute));

        List<UserPublicDTO> result = new ArrayList<>();
        result.add(modelMapper.map(user1, UserPublicDTO.class));
        result.add(modelMapper.map(user2, UserPublicDTO.class));

        assertThat(plannedRouteService.getPassengersOnRoute(plannedRoute.getId()))
                .hasSameElementsAs(result);

        verify(plannedRoutesRepository, times(1))
                .findById(plannedRoute.getId());
    }

    /**
     * Get passengers on a planned route when planned route doesn't exist
     */
    @Test
    public void getPassengersOnRoute_noRoute_failure() {
        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();

        when(plannedRoutesRepository.findById(plannedRoute.getId()))
                .thenReturn(Optional.empty());

        assertThrows(PlannedRouteNotFoundException.class,
                () -> plannedRouteService.getPassengersOnRoute(plannedRoute.getId()));

        verify(plannedRoutesRepository, times(1))
                .findById(plannedRoute.getId());
    }

    /**
     * Create a new planned route when planned route already exist
     */
    @Test
    public void createNewPlannedRoute_alreadyExists_exception() {
        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();
        Driver driver = TestObjects.DRIVER.clone();

        PlannedRoute plannedRoute = modelMapper.map(plannedRouteDTO,
                PlannedRoute.class);
        when(plannedRoutesRepository.save(plannedRoute))
                .thenThrow(new DataIntegrityViolationException("Test"));
        when(driverRepository.findDriverByCarPlate(driver.getCarPlate()))
                .thenReturn(Optional.of(driver));

        assertThrows(DataIntegrityViolationException.class,
                () -> plannedRouteService
                        .createNewPlannedRoute(plannedRouteDTO, driver.getCarPlate()));
        verify(plannedRoutesRepository, times(1))
                .save(any(PlannedRoute.class));
    }

    /**
     * Get all planned routes after a particular time
     */
    @Test
    public void getAllPlannedRoutesAfterTime_success() {
        List<PlannedRoute> plannedRouteResult = new ArrayList<>();

        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();
        plannedRouteResult.add(plannedRoute);

        when(plannedRoutesRepository.findPlannedRouteByDateTime(
                        plannedRoute.getDateTime()))
                        .thenReturn(plannedRouteResult);

        assertEquals(plannedRouteResult.get(0).getId(),
                plannedRouteService.getPlannedRouteAfterTime(
                        plannedRoute.getDateTime()).get(0).getId());
                        
        verify(plannedRoutesRepository, times(1))
                .findPlannedRouteByDateTime(plannedRoute.getDateTime());
    }

    /**
     * Update a planned route with valid parameters
     */
    @Test
    public void updatePlannedRoute_success() {
        Driver driver = TestObjects.DRIVER.clone();

        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();
        plannedRoute.setDriver(driver);

        //Changed capacity
        PlannedRoute plannedRouteResult = TestObjects.PLANNED_ROUTE.clone();
        plannedRouteResult.setCapacity(3);
        plannedRouteResult.setDriver(driver);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();
        plannedRouteDTO.setId(plannedRouteResult.getId());

        when(plannedRoutesRepository.findById(plannedRouteResult.getId()))
                .thenReturn(Optional.of(plannedRoute));
        when(plannedRoutesRepository.save(plannedRouteResult))
                .thenReturn(plannedRouteResult);

        assertEquals(plannedRouteService
                .updatePlannedRouteById(plannedRoute.getId(),
                        plannedRouteDTO).getId(), plannedRouteResult.getId());

        verify(plannedRoutesRepository, times(1))
                .findById(plannedRouteDTO.getId());
        verify(plannedRoutesRepository, times(1))
                .save(any(PlannedRoute.class));
    }

    /**
     * Update a planned route that doesn't exist
     */
    @Test
    public void updatePlannedRoute_doesntExist_exception() {
        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();
        plannedRouteDTO.setId(UUID.randomUUID());

        when(plannedRoutesRepository.findById(TestObjects.PLANNED_ROUTE.getId()))
                .thenReturn(Optional.empty());

        assertThrows(PlannedRouteNotFoundException.class,
                () -> plannedRouteService.updatePlannedRouteById(
                                TestObjects.PLANNED_ROUTE.getId(),
                        plannedRouteDTO));

        verify(plannedRoutesRepository, times(1))
                .findById(any(UUID.class));
        verify(plannedRoutesRepository, never()).save(any(PlannedRoute.class));
    }

    /**
     * Update a planned route with non unique parameters
     */
    @Test
    public void updatePlannedRoute_nonUniqueParams_exception() {
        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();
        plannedRouteDTO.setId(UUID.randomUUID());

        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();

        when(plannedRoutesRepository.findById(plannedRoute.getId()))
                .thenReturn(Optional.of(plannedRoute));

        when(plannedRoutesRepository.save(plannedRoute))
                .thenThrow(ConstraintViolationException.class);

        assertThrows(ConstraintViolationException.class,
                () -> plannedRouteService.updatePlannedRouteById
                        (plannedRoute.getId(), plannedRouteDTO));

        verify(plannedRoutesRepository, times(1))
                .findById(any(UUID.class));
        verify(plannedRoutesRepository, times(1))
                .save(any(PlannedRoute.class));
    }

    /**
     * Delete a planned route
     */
    @Test
    public void deletePlannedRoute_success() {
        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();

        when(plannedRoutesRepository.findById(plannedRoute.getId()))
                .thenReturn(Optional.of(plannedRoute));

        assertEquals(plannedRoute.getId(), plannedRouteService
                .deletePlannedRouteByID(plannedRoute.getId()).getId());

        verify(plannedRoutesRepository, times(1))
                .findById(plannedRoute.getId());
        verify(plannedRoutesRepository, times(1))
                .deleteById(plannedRoute.getId());
    }

    /**
     * Delete a planned route that doesn't exist
     */
    @Test
    public void deletePlannedRoute_doesntExist_exception() {
        UUID uuid = UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");
        when(plannedRoutesRepository.findById(uuid))
                .thenReturn(Optional.empty());

        assertThrows(PlannedRouteNotFoundException.class,
                () -> plannedRouteService.deletePlannedRouteByID(uuid));

        verify(plannedRoutesRepository, times(1)).findById(uuid);
        verify(plannedRoutesRepository, never()).deleteById(uuid);
    }
}
