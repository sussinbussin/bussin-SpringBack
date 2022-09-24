package com.bussin.SpringBack.models;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.util.*;

import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Ride implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    private Timestamp timestamp;

    @NotNull
    @Min(1)
    @NotNull(message = "How many passengers can this ride accommodate?")
    private Integer passengers;

    @NotNull
    @DecimalMin("0")
    private BigDecimal cost;

    @ManyToOne
    @JoinColumn(name = "planned_route_id")
    private PlannedRoute plannedRoute;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void updateFromDTO(RideDTO rideDTO) {
        this.id = rideDTO.getId();
        this.timestamp = rideDTO.getTimestamp();
        this.passengers = rideDTO.getPassengers();
        this.cost = rideDTO.getCost();
    }
}
