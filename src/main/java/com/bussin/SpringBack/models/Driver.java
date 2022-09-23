package com.bussin.SpringBack.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "driver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
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
}
