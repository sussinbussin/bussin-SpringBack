package com.bussin.SpringBack.models.driver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
import java.util.Set;

/**
 * A subset of Driver for modifications and insertions
 */
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class DriverDTO implements Serializable, Cloneable {
    @Id
    @NotNull(message = "Car Plate should not be empty")
    @Schema(description = "Driver's car plate number", example = "SAA1234A")
    private String carPlate;

    @Size(max = 255, message = "Not longer than 255 characters")
    @NotNull(message = "Model and colour should not be empty")
    @Schema(description = "Driver's car model and colour",
            example = "Yellow Volkswagen Beetle")
    private String modelAndColour;

    @Min(value = 2)
    @Max(value = 12)
    @NotNull(message = "Capacity should not be empty")
    @Schema(description = "Capacity of the car (including driver)",
            example = "4")
    private Integer capacity;

    @Schema(description = "Type of fuel the car uses, follow example values",
            example = "Diesel | 92 | 95 | 98 | Premium")
    @NotNull
    private String fuelType;

    @JsonCreator
    public DriverDTO(@JsonProperty("carPlate") final String carPlate,
                     @JsonProperty("modelAndColour") final String modelAndColour,
                     @JsonProperty("capacity") final Integer capacity,
                     @JsonProperty("fuelType") final String fuelType) {
        this.carPlate = carPlate;
        this.modelAndColour = modelAndColour;
        this.capacity = capacity;
        this.fuelType = fuelType;
    }

    /**
     * Check if there is any constraint violations during input
     */
    public void validate() {
        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<DriverDTO>> violations =
                validator.validate(this);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }

    @Override
    public DriverDTO clone() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(this), DriverDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "DriverDTO{" +
                "carPlate='" + carPlate + '\'' +
                ", modelAndColour='" + modelAndColour + '\'' +
                ", capacity=" + capacity +
                ", fuelType='" + fuelType + '\'' +
                '}';
    }
}
