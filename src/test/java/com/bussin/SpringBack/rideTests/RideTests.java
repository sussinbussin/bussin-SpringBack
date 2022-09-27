package com.bussin.SpringBack.rideTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.bussin.SpringBack.models.*;
import com.bussin.SpringBack.repositories.*;
import com.bussin.SpringBack.services.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import java.time.*;

import javax.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
public class RideTests {
        @Mock
        private RideRepository rideRepository;

        @Mock
        private PlannedRoutesRepository plannedRoutesRepository;

        @Mock
        private UserService userService;

        @Mock
        private PriceService priceService;

        private ModelMapper modelMapper;

        @InjectMocks
        private RideService rideService;

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
                                plannedRoutesRepository, userService,
                        priceService);
        }

        @Test
        public void getAllRides_noRides_success() {
                when(rideRepository.findAll()).thenReturn(new ArrayList<>());

                assertEquals(rideService.getAllRides(), new ArrayList<>());

                verify(rideRepository, times(1)).findAll();
        }

        @Test
        public void getAllRides_success() {
                ArrayList<Ride> rides = new ArrayList<>();
                Ride ride = Ride.builder()
                                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555"))
                                .timestamp(new Timestamp(System.currentTimeMillis()))
                                .passengers(1)
                                .cost(new BigDecimal(6.90))
                                .build();
                rides.add(ride);

                when(rideRepository.findAll()).thenReturn(rides);

                assertEquals(rideService.getAllRides(), rides);

                verify(rideRepository, times(1)).findAll();
        }

        @Test
        public void createNewRide_invalidParams_exception() {
                RideDTO rideDTO = RideDTO.builder()
                                .timestamp(new Timestamp(System.currentTimeMillis()))
                                .passengers(1000)
                                .build();

                User user = User.builder()
                                .nric("S1234567A")
                                .name("Test Guy")
                                .address("444333")
                                .dob(new Date(System.currentTimeMillis()))
                                .mobile("90009000")
                                .email("testguy@test.com")
                                .isDriver(false)
                                .build();

                PlannedRoute plannedRoute = PlannedRoute.builder()
                        .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555"))
                        .plannedFrom("Start")
                        .plannedTo("To")
                        .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                        .capacity(1)
                        .build();

                assertThrows(ConstraintViolationException.class,
                                () -> rideService.createNewRide(rideDTO,
                                        user.getId(), plannedRoute.getId()));

                verify(rideRepository, never()).save(any(Ride.class));
        }

        // @Test
        // public void createNewRide_alreadyExists_exception() {
        // RideDTO rideDTO = RideDTO.builder()
        // .timestamp(new Timestamp(System.currentTimeMillis()))
        // .passengers(1)
        // .cost(new BigDecimal(6.90))
        // .build();

        // PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
        // .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555"))
        // .plannedFrom("Start")
        // .plannedTo("To")
        // .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
        // .capacity(1)
        // .build();

        // User user = User.builder()
        // .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
        // .nric("S1234567A")
        // .name("Test Guy")
        // .address("444333")
        // .dob(new Date(System.currentTimeMillis()))
        // .mobile("90009000")
        // .email("testguy@test.com")
        // .isDriver(false)
        // .build();

        // Ride ride = modelMapper.map(rideDTO, Ride.class);

        // PlannedRoute plannedRoute = modelMapper.map(plannedRouteDTO,
        // PlannedRoute.class);
        // ride.setPlannedRoute(plannedRoute);
        // ride.setUser(user);

        // when(rideRepository.save(ride))
        // .thenThrow(new DataIntegrityViolationException("Test"));

        // when(plannedRoutesRepository.findPlannedRouteById(plannedRouteDTO.getId()))
        // .thenReturn(Optional.of(plannedRouteDTO));

        // when(userService.getFullUserById(user.getId())).thenReturn(Optional.of(user));

        // assertThrows(DataIntegrityViolationException.class,
        // () -> rideService
        // .createNewRide(rideDTO, user.getId(), plannedRouteDTO.getId()));

        // verify(rideRepository, times(1)).save(any(Ride.class));

        // }

        @Test
        public void updateRide_success() {
                UserDTO userDTO = UserDTO.builder()
                                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
                                .nric("S1234567A")
                                .name("Test Guy")
                                .address("444333")
                                .dob(new Date(System.currentTimeMillis()))
                                .mobile("90009000")
                                .email("testguy@test.com")
                                .isDriver(false)
                                .build();

                User user = modelMapper.map(userDTO, User.class);

                Driver driver = Driver.builder()
                                .carPlate("SAA1234A")
                                .modelAndColour("Yellow Submarine")
                                .capacity(4)
                                .fuelType("Premium")
                                .build();

                PlannedRoute plannedRoute = PlannedRoute.builder()
                                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555"))
                                .plannedFrom("Start")
                                .plannedTo("To")
                                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                                .capacity(1)
                                .driver(driver)
                                .build();

                Ride ride = Ride.builder()
                                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555"))
                                .timestamp(new Timestamp(System.currentTimeMillis()))
                                .passengers(1)
                                .cost(new BigDecimal(6.90))
                                .plannedRoute(plannedRoute)
                                .user(user)
                                .build();

                // Changed passengers
                Ride rideResult = Ride.builder()
                                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555"))
                                .timestamp(new Timestamp(System.currentTimeMillis()))
                                .passengers(3)
                                .cost(new BigDecimal(6.90))
                                .plannedRoute(plannedRoute)
                                .user(user)
                                .build();

                RideDTO rideDTO = RideDTO.builder()
                                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555"))
                                .timestamp(new Timestamp(System.currentTimeMillis()))
                                .passengers(3)
                                .build();

                when(rideRepository.findById(
                                UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555")))
                                .thenReturn(Optional.of(ride));

                when(rideRepository.save(rideResult)).thenReturn(rideResult);

                assertEquals(rideService.updateRideById(rideDTO.getId(), rideDTO), rideResult);

                verify(rideRepository, times(1)).findById(rideDTO.getId());
                verify(rideRepository, times(1)).save(any(Ride.class));
        }
}
