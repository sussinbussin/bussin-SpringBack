package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Ride implements Serializable, Cloneable{
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Ride ride = (Ride) o;
        return id != null && Objects.equals(id, ride.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public Ride clone() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(this), Ride.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
