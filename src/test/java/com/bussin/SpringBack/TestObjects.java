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
            .dob(new Date(System.currentTimeMillis()))
            .mobile("90009000")
            .email("testguy@test.com")
            .isDriver(false)
            .build();

    public static final UserDTO USER_DTO = UserDTO.builder()
            .id(UUID.randomUUID())
            .nric("S1234567A")
            .name("Test Guy")
            .dob(new Date(System.currentTimeMillis()))
            .mobile("90009000")
            .email("testguy@test.com")
            .isDriver(true)
            .build();

    public static final User COGNITO_USER = User.builder()
            .id(UUID.randomUUID())
            .nric("S1337369P")
            .name("springbacktest")
            .dob(new Date(System.currentTimeMillis()))
            .mobile("90001337")
            .email("springbacktest@gmail.com")
            .isDriver(false)
            .build();

    public static final UserDTO COGNITO_USER_DTO = UserDTO.builder()
            .id(UUID.randomUUID())
            .nric("S1337369P")
            .name("springbacktest")
            .dob(new Date(System.currentTimeMillis()))
            .mobile("90001337")
            .email("springbacktest@gmail.com")
            .isDriver(false)
            .build();

    public static final UserDTO COGNITO_DRIVER_DTO = UserDTO.builder()
            .id(UUID.randomUUID())
            .nric("S1337420Z")
            .name("Robert The Driver")
            .dob(new Date(System.currentTimeMillis()))
            .mobile("94201337")
            .email("Robert@gmail.com")
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
            .plannedFrom("place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
            .plannedTo("place_id:ChIJGddBg6MZ2jERACsxW7Ovm_4")
            .originLatitude(BigDecimal.valueOf(103.985120))
            .originLongitude(BigDecimal.valueOf(1.349640))
            .destLatitude(BigDecimal.valueOf(103.852020))
            .destLongitude(BigDecimal.valueOf(1.296700))
            .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
            .capacity(1)
            .build();

    public static final PlannedRouteDTO PLANNED_ROUTE_DTO = PlannedRouteDTO.builder()
            .plannedFrom("place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
            .plannedTo("place_id:ChIJGddBg6MZ2jERACsxW7Ovm_4")
            .originLatitude(BigDecimal.valueOf(1.349640))
            .originLongitude(BigDecimal.valueOf(103.985120))
            .destLatitude(BigDecimal.valueOf(1.296700))
            .destLongitude(BigDecimal.valueOf(103.852020))
            .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
            .capacity(3)
            .build();

    public static final RideDTO RIDE_DTO = RideDTO.builder()
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .passengers(1)
            .rideTo("place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
            .rideFrom("place_id:ChIJGddBg6MZ2jERACsxW7Ovm_4")
            .build();

    public static final Ride RIDE = Ride.builder()
            .id(UUID.randomUUID())
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .passengers(1)
            .cost(new BigDecimal("6.90"))
            .rideTo("place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
            .rideFrom("place_id:ChIJGddBg6MZ2jERACsxW7Ovm_4")
            .build();
}
