package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class PlannedRouteDTO implements Serializable, Cloneable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @NotNull
    @Size(max = 512)
    private String plannedFrom;

    @NotNull
    @Size(max = 512)
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

    @Override
    public PlannedRouteDTO clone() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(this), PlannedRouteDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
