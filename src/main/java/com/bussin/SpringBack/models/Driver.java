package com.bussin.SpringBack.models;

import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.*;

@Entity(name = "driver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Driver {
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
}
