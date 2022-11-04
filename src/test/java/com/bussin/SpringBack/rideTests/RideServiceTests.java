package com.bussin.SpringBack.rideTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.exception.RideException;
import com.bussin.SpringBack.exception.RideNotFoundException;
import com.bussin.SpringBack.models.driver.Driver;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.ride.Ride;
import com.bussin.SpringBack.models.ride.RideDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import com.bussin.SpringBack.repositories.PlannedRoutesRepository;
import com.bussin.SpringBack.repositories.RideRepository;
import com.bussin.SpringBack.services.RideService;
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

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
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
public class RideServiceTests {
    @Mock
    private RideRepository rideRepository;

    @Mock
    private PlannedRoutesRepository plannedRoutesRepository;

    @Mock
    private UserService userService;

    private ModelMapper modelMapper;

    @InjectMocks
    private RideService rideService;

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
        rideService = new RideService(modelMapper, rideRepository,
                plannedRoutesRepository, userService);
    }

    /**
     * Get rides when there are no rides
     */
    @Test
    public void getAllRides_noRides_success() {
        when(rideRepository.findAll()).thenReturn(new ArrayList<>());

        assertEquals(rideService.getAllRides(), new ArrayList<>());

        verify(rideRepository, times(1)).findAll();
    }

    /**
     * Get all rides
     */
    @Test
    public void getAllRides_success() {
        ArrayList<Ride> rides = new ArrayList<>();
        rides.add(TestObjects.RIDE.clone());

        when(rideRepository.findAll()).thenReturn(rides);

        assertEquals(rideService.getAllRides(), rides);

        verify(rideRepository, times(1)).findAll();
    }

    /**
     * Create a new ride with invalid parameters
     */
    @Test
    public void createNewRide_invalidParams_exception() {
        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();
        rideDTO.setPassengers(1000);

        User user = TestObjects.USER.clone();

        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();

        assertThrows(ConstraintViolationException.class,
                () -> rideService.createNewRide(rideDTO,
                        user.getId(), plannedRoute.getId()));

        verify(rideRepository, never()).save(any(Ride.class));
    }

    /**
     * Create a new ride when passengers are over the capacity
     */
    @Test
    public void createNewRide_passengersOverCapacity_exception() {
        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();
        rideDTO.setPassengers(5);

        User user = TestObjects.USER.clone();

        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();

        when(plannedRoutesRepository.findPlannedRouteById(plannedRoute.getId())).thenReturn(Optional.of(plannedRoute));

        assertThrows(RideException.class,
                () -> rideService.createNewRide(rideDTO,
                        user.getId(), plannedRoute.getId()));

        verify(rideRepository, never()).save(any(Ride.class));
    }

    /**
     * Create a new ride when multiple rides are over the capacity
     */
    @Test
    public void createNewRide_multipleRidesOverCapacity_exception() {
        Ride ride = TestObjects.RIDE.clone();

        Set<Ride> rides = new HashSet<>() {{
            add(ride);
        }};

        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();
        rideDTO.setPassengers(4);

        User user = TestObjects.USER.clone();

        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();
        plannedRoute.setRides(rides);

        when(plannedRoutesRepository.findPlannedRouteById(plannedRoute.getId())).thenReturn(Optional.of(plannedRoute));

        assertThrows(RideException.class,
                () -> rideService.createNewRide(rideDTO,
                        user.getId(), plannedRoute.getId()));

        verify(rideRepository, never()).save(any(Ride.class));
    }

    /**
     * Create a new ride when ride already exist
     */
    @Test
    public void createNewRide_alreadyExists_exception() {
        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();

        Driver driver = TestObjects.DRIVER.clone();

        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();
        plannedRoute.setDriver(driver);

        UserDTO userDTO = TestObjects.USER_DTO.clone();

        User user = modelMapper.map(userDTO, User.class);
        user.setDriver(driver);

        when(rideRepository.save(any(Ride.class)))
                .thenThrow(new DataIntegrityViolationException("Test"));

        when(userService.getFullUserById(user.getId()))
                .thenReturn(user);

        when(plannedRoutesRepository.findPlannedRouteById(plannedRoute.getId()))
                .thenReturn(Optional.of(plannedRoute));

        assertThrows(DataIntegrityViolationException.class,
                () -> rideService
                        .createNewRide(rideDTO, user.getId(), plannedRoute.getId()));

        verify(rideRepository, times(1)).save(any(Ride.class));
    }

    /**
     * Update a ride with valid parameters
     */
    @Test
    public void updateRide_success() {
        Ride ride = TestObjects.RIDE.clone();

        Ride rideResult = TestObjects.RIDE.clone();
        rideResult.setCost(BigDecimal.TEN);

        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();
        rideDTO.setCost(BigDecimal.TEN);

        when(rideRepository.findById(ride.getId()))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(rideResult)).thenReturn(rideResult);

        assertEquals(rideResult.getCost(),
                rideService.updateRideById(ride.getId(), rideDTO).getCost());

        verify(rideRepository, times(1)).findById(rideDTO.getId());
        verify(rideRepository, times(1)).save(any(Ride.class));
    }

    /**
     * Update a ride when ride doesn't exist
     */
    @Test
    public void updateRide_doesntExist_exception() {
        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();

        when(rideRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class,
                () -> rideService.updateRideById(UUID.randomUUID(), rideDTO));

        verify(rideRepository, times(1))
                .findById(any(UUID.class));

        verify(rideRepository, never()).save(any(Ride.class));
    }

    /**
     * Update a ride with invalid parameters
     */
    @Test
    public void updateRide_invalidParams_exception() {
        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();
        rideDTO.setPassengers(1000);

        assertThrows(ConstraintViolationException.class,
                () -> rideService.updateRideById(rideDTO.getId(), rideDTO));

        verify(rideRepository, never()).findById(any(UUID.class));

        verify(rideRepository, never()).save(any(Ride.class));
    }

    /**
     * Update a user with non unique parameters
     */
    @Test
    public void updateRide_nonUniqueParams_exception() {
        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();
        Ride ride = TestObjects.RIDE.clone();

        when(rideRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(ride));
        when(rideRepository.save(ride))
                .thenThrow(ConstraintViolationException.class);

        assertThrows((ConstraintViolationException.class),
                () -> rideService.updateRideById
                        (ride.getId(), rideDTO));

        verify(rideRepository, times(1))
                .findById(any(UUID.class));
        verify(rideRepository, times(1))
                .save(any(Ride.class));
    }

    /**
     * Delete a ride
     */
    @Test
    public void deleteRide_success() {
        Ride ride = TestObjects.RIDE.clone();

        when(rideRepository.findById(ride.getId()))
                .thenReturn(Optional.of(ride));

        assertEquals(ride.getId(), rideService
                .deleteRideById(ride.getId()).getId());

        verify(rideRepository, times(1))
                .findById(ride.getId());

        verify(rideRepository, times(1))
                .deleteById(ride.getId());
    }

    /**
     * Delete a ride when ride doesn't exist
     */
    @Test
    public void deleteRide_doesntExist_exception() {
        UUID uuid = UUID.randomUUID();
        when(rideRepository.findById(uuid))
                .thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class,
                () -> rideService.deleteRideById(uuid));

        verify(rideRepository, times(1)).findById(uuid);
        verify(rideRepository, never()).deleteById(uuid);
    }
}
