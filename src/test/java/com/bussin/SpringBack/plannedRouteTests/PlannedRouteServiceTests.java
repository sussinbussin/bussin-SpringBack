package com.bussin.SpringBack.plannedRouteTests;

import com.bussin.SpringBack.exception.PlannedRouteNotFoundException;
import com.bussin.SpringBack.models.*;
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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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

    static final PlannedRoute PLANNED_ROUTE = PlannedRoute.builder()
            .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555"))
            .plannedFrom("Start")
            .plannedTo("To")
            .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
            .capacity(1)
            .build();

    static final PlannedRouteDTO PLANNED_ROUTE_DTO = PlannedRouteDTO.builder()
            .plannedFrom("Start")
            .plannedTo("To")
            .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
            .capacity(1)
            .build();

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

    @Test
    public void getAllPlannedRoutes_noRoutes_success() {
        when(plannedRoutesRepository.findAll()).thenReturn(new ArrayList<>());

        assertEquals(plannedRouteService.getAllPlannedRoutes(),
                new ArrayList<>());

        verify(plannedRoutesRepository, times(1))
                .findAll();
    }

    @Test
    public void getAllPlannedRoutes_success() {
        ArrayList<PlannedRoute> plannedRoutes = new ArrayList<>();

        plannedRoutes.add(PLANNED_ROUTE.clone());

        when(plannedRoutesRepository.findAll()).thenReturn(plannedRoutes);

        assertEquals(plannedRouteService.getAllPlannedRoutes(), plannedRoutes);

        verify(plannedRoutesRepository, times(1)).findAll();
    }

    @Test
    public void createNewPlannedRoute_invalidParams_exception() {
        //No capacity
        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();
        plannedRouteDTO.setCapacity(-1);

        Driver driver = Driver.builder()
                .carPlate("SAA1234A")
                .modelAndColour("Yellow Submarine")
                .capacity(4)
                .fuelType("Premium")
                .build();

        assertThrows(ConstraintViolationException.class,
                () -> plannedRouteService.createNewPlannedRoute(plannedRouteDTO, driver.getCarPlate()));
        verify(plannedRoutesRepository, never()).save(any(PlannedRoute.class));
    }

    @Test
    public void getPassengersOnRoute_success() {
        PlannedRoute plannedRoute = PLANNED_ROUTE.clone();
        HashSet<Ride> rides = new HashSet<>();

        User user1 = User.builder()
                .id(UUID.randomUUID())
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

        Ride ride1 = Ride.builder()
            .id(UUID.randomUUID())
            .passengers(1)
            .plannedRoute(plannedRoute)
            .cost(BigDecimal.TEN)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .user(user1)
            .build();

        User user2 = User.builder()
                .id(UUID.randomUUID())
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

        Ride ride2 = Ride.builder()
                .id(UUID.randomUUID())
                .passengers(1)
                .plannedRoute(plannedRoute)
                .cost(BigDecimal.TEN)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .user(user2)
                .build();

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

    @Test
    public void getPassengersOnRoute_noRoute_failure() {
        PlannedRoute plannedRoute = PLANNED_ROUTE.clone();

        when(plannedRoutesRepository.findById(plannedRoute.getId()))
                .thenReturn(Optional.empty());

        assertThrows(PlannedRouteNotFoundException.class,
                () -> plannedRouteService.getPassengersOnRoute(plannedRoute.getId()));

        verify(plannedRoutesRepository, times(1))
                .findById(plannedRoute.getId());
    }

    @Test
    public void createNewPlannedRoute_alreadyExists_exception() {
        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();
        Driver driver = Driver.builder()
                .carPlate("SAA1234A")
                .modelAndColour("Yellow Submarine")
                .capacity(4)
                .fuelType("Premium")
                .build();

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

    @Test
    public void updatePlannedRoute_success() {
        Driver driver = Driver.builder()
                .carPlate("SAA1234A")
                .modelAndColour("Yellow Submarine")
                .capacity(4)
                .fuelType("Premium")
                .build();

        PlannedRoute plannedRoute = PLANNED_ROUTE.clone();
        plannedRoute.setDriver(driver);

        //Changed capacity
        PlannedRoute plannedRouteResult = PLANNED_ROUTE.clone();
        plannedRouteResult.setCapacity(3);

        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        when(plannedRoutesRepository.findById(
                UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555")))
                .thenReturn(Optional.of(plannedRoute));
        when(plannedRoutesRepository.save(plannedRouteResult))
                .thenReturn(plannedRouteResult);

        assertEquals(plannedRouteService
                .updatePlannedRouteById(plannedRoute.getId(),
                        plannedRouteDTO), plannedRouteResult);

        verify(plannedRoutesRepository, times(1))
                .findById(plannedRouteDTO.getId());
        verify(plannedRoutesRepository, times(1))
                .save(any(PlannedRoute.class));
    }

    @Test
    public void updatePlannedRoute_doesntExist_exception() {
        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        when(plannedRoutesRepository.findById(PLANNED_ROUTE.getId()))
                .thenReturn(Optional.empty());

        assertThrows(PlannedRouteNotFoundException.class,
                () -> plannedRouteService.updatePlannedRouteById(
                                UUID.fromString(
                                        "a6bb7dc3-5cbb-4408-a749-514e0b4a0555"),
                        plannedRouteDTO));

        verify(plannedRoutesRepository, times(1))
                .findById(any(UUID.class));
        verify(plannedRoutesRepository, never()).save(any(PlannedRoute.class));
    }

    @Test
    public void updatePlannedRoute_nonUniqueParams_exception() {
        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute = PLANNED_ROUTE.clone();

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

    @Test
    public void deletePlannedRoute_success() {
        PlannedRoute plannedRoute = PLANNED_ROUTE.clone();

        when(plannedRoutesRepository.findById(plannedRoute.getId()))
                .thenReturn(Optional.of(plannedRoute));

        assertEquals(plannedRoute, plannedRouteService
                .deletePlannedRouteByID(plannedRoute.getId()));

        verify(plannedRoutesRepository, times(1))
                .findById(plannedRoute.getId());
        verify(plannedRoutesRepository, times(1))
                .deleteById(plannedRoute.getId());
    }

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
