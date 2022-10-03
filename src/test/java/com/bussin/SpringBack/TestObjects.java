package com.bussin.SpringBack;

import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.models.DriverDTO;
import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.PlannedRouteDTO;
import com.bussin.SpringBack.models.Ride;
import com.bussin.SpringBack.models.RideDTO;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class TestObjects {
    public static final User USER = User.builder()
            .nric("S1234567A")
            .name("Test Guy")
            .address("444333")
            .dob(new Date(System.currentTimeMillis()))
            .mobile("90009000")
            .email("testguy@test.com")
            .isDriver(false)
            .build();

    public static final UserDTO USER_DTO = UserDTO.builder()
            .id(UUID.randomUUID())
            .nric("S1234567A")
            .name("Test Guy")
            .address("444333")
            .dob(new Date(System.currentTimeMillis()))
            .mobile("90009000")
            .email("testguy@test.com")
            .isDriver(true)
            .build();

    public static final Driver DRIVER = Driver.builder()
            .carPlate("SAA1234A")
            .modelAndColour("Yellow Submarine")
            .capacity(4)
            .fuelType("TypePremium")
            .build();

    public static final DriverDTO DRIVER_DTO = DriverDTO.builder()
            .carPlate("SAA1234A")
            .modelAndColour("Yellow Submarine")
            .capacity(4)
            .fuelType("TypePremium")
            .build();

    public static final PlannedRoute PLANNED_ROUTE = PlannedRoute.builder()
            .id(UUID.randomUUID())
            .plannedFrom("188065")
            .plannedTo("119077")
            .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
            .capacity(1)
            .build();

    public static final PlannedRouteDTO PLANNED_ROUTE_DTO = PlannedRouteDTO.builder()
            .plannedFrom("188065")
            .plannedTo("119077")
            .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
            .capacity(3)
            .build();

    public static final RideDTO RIDE_DTO = RideDTO.builder()
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .passengers(1)
            .rideTo("188605")
            .rideFrom("119077")
            .build();

    public static final Ride RIDE = Ride.builder()
            .id(UUID.randomUUID())
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .passengers(1)
            .cost(new BigDecimal(6.90))
            .rideTo("188605")
            .rideFrom("119077")
            .build();
}
