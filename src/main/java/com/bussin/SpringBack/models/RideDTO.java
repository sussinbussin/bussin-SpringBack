package com.bussin.SpringBack.models;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import org.hibernate.Hibernate;

import java.sql.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RideDTO {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    private Timestamp timestamp;

    @NotNull
    @Max(11)
    @Min(1)
    @NotNull(message = "How many passengers can this ride accommodate?")
    private Integer passengers;
    
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
                   @JsonProperty("passengers") Integer passengers) {
        this.id = id;
        this.timestamp = timestamp;
        this.passengers = passengers;
    }
}
