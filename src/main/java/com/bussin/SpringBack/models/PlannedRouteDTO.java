package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "UUID of the planned route.",
            example = "844b8d14-ef82-4b27-b9b5-a5e765c1254f")
    private UUID id;

    @NotNull
    @Size(max = 512)
    @Schema(description = "Place ID of the starting location",
            example = "place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
    private String plannedFrom;

    @NotNull
    @Size(max = 512)
    @Schema(description = "Place ID of the destination location",
            example = "place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
    private String plannedTo;

    @NotNull(message = "Date and time should not be empty")
    @Schema(description = "Date and Time of when this planned route will be " +
            "travelled.", example = "2022-10-09T01:09:34.337Z")
    private LocalDateTime dateTime;

    @Max(11)
    @Min(1)
    @NotNull(message = "How many passengers?")
    @Schema(description = "How many passengers the driver is willing to take",
            example = "2")
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
