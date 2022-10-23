package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString
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

    @NotNull
    @Column(scale = 6, precision = 8)
    private BigDecimal originLatitude;

    @NotNull
    @Column(scale = 6, precision = 9)
    private BigDecimal originLongitude;

    @NotNull
    @Column(scale = 6, precision = 8)
    private BigDecimal destLatitude;

    @NotNull
    @Column(scale = 6, precision = 9)
    private BigDecimal destLongitude;

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
                           @JsonProperty("capacity") Integer capacity,
                           @JsonProperty("originLatitude") BigDecimal originLatitude,
                           @JsonProperty("originLongitude") BigDecimal originLongitude,
                           @JsonProperty("destLatitude") BigDecimal destLatitude,
                           @JsonProperty("destLongitude") BigDecimal destLongitude)
    {
        this.id = id;
        this.plannedFrom = plannedFrom;
        this.plannedTo = plannedTo;
        this.dateTime = dateTime;
        this.capacity = capacity;
        this.originLatitude = originLatitude;
        this.originLongitude = originLongitude;
        this.destLatitude = destLatitude;
        this.destLongitude = destLongitude;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlannedRouteDTO that = (PlannedRouteDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getPlannedFrom(), that.getPlannedFrom()) && Objects.equals(getPlannedTo(), that.getPlannedTo()) && Objects.equals(getDateTime(), that.getDateTime()) && Objects.equals(getCapacity(), that.getCapacity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPlannedFrom(), getPlannedTo(), getDateTime(), getCapacity());
    }
}
