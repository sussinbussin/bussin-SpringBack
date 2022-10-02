package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

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

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class DriverDTO implements Serializable, Cloneable {
    @NotNull(message = "Car Plate should not be empty")
    private String carPlate;

    @Size(max = 20, message = "Not longer than 20 characters")
    @NotNull(message = "Model and colour should not be empty")
    private String modelAndColour;

    @Min(value = 2)
    @Max(value = 12)
    @NotNull(message = "Capacity should not be empty")
    private Integer capacity;

    @NotNull
    private String fuelType;

    public void validate() {
        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<DriverDTO>> violations =
                validator.validate(this);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }

    @JsonCreator
    public DriverDTO(@JsonProperty("carPlate") String carPlate,
                     @JsonProperty("modelAndColour") String modelAndColour,
                     @JsonProperty("capacity") Integer capacity,
                     @JsonProperty("fuelType") String fuelType) {
        this.carPlate = carPlate;
        this.modelAndColour = modelAndColour;
        this.capacity = capacity;
        this.fuelType = fuelType;
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
}
