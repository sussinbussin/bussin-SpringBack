package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RideDTO implements Serializable, Cloneable {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    private Timestamp timestamp;

    @NotNull
    @Max(11)
    @Min(1)
    @NotNull(message = "How many passengers can this ride accommodate?")
    private Integer passengers;

    @Pattern(regexp = "^[0-9]{6}$")
    @NotNull
    private String rideFrom;

    @Pattern(regexp = "^[0-9]{6}$")
    @NotNull
    private String rideTo;

    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<RideDTO>> violations = validator.validate(this);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }

    @JsonCreator
    public RideDTO(@JsonProperty("id") UUID id,
                   @JsonProperty("timestamp") Timestamp timestamp,
                   @JsonProperty("passengers") Integer passengers,
                   @JsonProperty("rideFrom") String rideFrom,
                   @JsonProperty("rideTo") String rideTo) {
        this.id = id;
        this.timestamp = timestamp;
        this.passengers = passengers;
        this.rideFrom = rideFrom;
        this.rideTo = rideTo;
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
}
