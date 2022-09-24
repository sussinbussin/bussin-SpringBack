package com.bussin.SpringBack.models;

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
public class Driver implements Serializable {
    @Id
    @NotNull(message = "Car Plate should not be empty")
    private String carPlate;

    @Size(max = 20, message = "Not longer than 20 characters")
    @NotNull(message = "Model and colour should not be empty")
    private String modelAndColour;

    @Min(value = 2)
    @Max(value = 12)
    @NotNull(message = "Capacity should not be empty")
    private Integer capacity;

    private String fuelType;

    @OneToOne
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
}
