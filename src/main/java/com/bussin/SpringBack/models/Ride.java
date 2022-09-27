package com.bussin.SpringBack.models;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.Type;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Ride implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    private Timestamp timestamp;

    @NotNull
    @Max(11)
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
    }
}
