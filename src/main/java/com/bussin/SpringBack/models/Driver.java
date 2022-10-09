package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity(name = "driver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "carPlate")
public class Driver implements Serializable, Cloneable {
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
            example = "TypeDiesel | Type92 | Type95 | Type98 | Type Premium")
    @NotNull
    private String fuelType;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "driver", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<PlannedRoute> plannedRoutes;

    public Driver updateFromDTO(DriverDTO driverDTO) {
        this.carPlate = driverDTO.getCarPlate();
        this.modelAndColour = driverDTO.getModelAndColour();
        this.capacity = driverDTO.getCapacity();
        this.fuelType = driverDTO.getFuelType();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Driver driver = (Driver) o;
        return carPlate != null && Objects.equals(carPlate, driver.carPlate);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public Driver clone() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(this), Driver.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
