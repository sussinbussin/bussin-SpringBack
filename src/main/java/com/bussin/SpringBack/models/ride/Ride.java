package com.bussin.SpringBack.models.ride;

import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
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
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * Model with ride information for users participating
 * in a planned route
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ride implements Serializable, Cloneable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(generator = "uuid2")
    @Schema(description = "UUID of the ride.",
            example = "844b8d14-ef82-4b27-b9b5-a5e765c1254f")
    private UUID id;

    @Schema(description = "Time when the passenger started riding")
    private Timestamp timestamp;

    @Max(11)
    @Min(1)
    @NotNull(message = "How many passengers can this ride accommodate?")
    @Schema(description = "Number of seats booked", example = "2")
    private Integer passengers;

    @DecimalMin("0")
    @Digits(integer = 6, fraction = 2)
    @Schema(description = "Cost of the ride", example = "3.00")
    private BigDecimal cost;

    @NotNull
    @Size(max = 512)
    @Schema(description = "Place ID of the passenger's journey start location",
            example = "place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
    private String rideFrom;

    @NotNull
    @Size(max = 512)
    @Schema(description = "Place ID of the passenger's journey destination " +
            "location", example = "place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
    private String rideTo;

    @ManyToOne
    @JoinColumn(name = "planned_route_id")
    @JsonBackReference
    private PlannedRoute plannedRoute;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
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
