package com.bussin.SpringBack.models.ride;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

/**
 * A subset of Ride for modifications and insertions
 */
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RideDTO implements Serializable, Cloneable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(generator = "uuid2")
    @Schema(description = "UUID of the ride.",
            example = "UUID of the ride.")
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

    @JsonCreator
    public RideDTO(@JsonProperty("id") final UUID id,
                   @JsonProperty("timestamp") final Timestamp timestamp,
                   @JsonProperty("passengers") final Integer passengers,
                   @JsonProperty("cost") final BigDecimal cost,
                   @JsonProperty("rideFrom") final String rideFrom,
                   @JsonProperty("rideTo") final String rideTo) {
        this.id = id;
        this.timestamp = timestamp;
        this.passengers = passengers;
        this.cost = cost;
        this.rideFrom = rideFrom;
        this.rideTo = rideTo;
    }

    /**
     * Check if there is any constraint violations during input
     */
    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<RideDTO>> violations = validator.validate(this);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }

    @Override
    public RideDTO clone() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(this), RideDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "RideDTO{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", passengers=" + passengers +
                ", cost=" + cost +
                ", rideFrom='" + rideFrom + '\'' +
                ", rideTo='" + rideTo + '\'' +
                '}';
    }
}
