package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class PlannedRouteDTO implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @NotNull(message = "Please set a starting point")
    private String plannedFrom;

    @NotNull(message = "Please set a destination.")
    private String plannedTo;

    @NotNull(message = "Date and time should not be empty")
    private LocalDateTime dateTime;

    @Max(11)
    @Min(1)
    @NotNull(message = "How many passengers?")
    private Integer capacity;

    public void validate() {
        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<PlannedRouteDTO>> violations =
                validator.validate(this);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }

    @JsonCreator
    public PlannedRouteDTO(@JsonProperty("id") UUID id,
                           @JsonProperty("plannedFrom") String plannedFrom,
                           @JsonProperty("plannedTo") String plannedTo,
                           @JsonProperty("dateTime") LocalDateTime dateTime,
                           @JsonProperty("capacity") Integer capacity) {
        this.id = id;
        this.plannedFrom = plannedFrom;
        this.plannedTo = plannedTo;
        this.dateTime = dateTime;
        this.capacity = capacity;
    }
}
