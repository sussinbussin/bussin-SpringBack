package com.bussin.SpringBack.models.driver;

import com.bussin.SpringBack.models.plannedRoute.PlannedRoutePublicDTO;
import com.bussin.SpringBack.models.ride.RideReturnDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "carPlate")
public class DriverPublicDTO implements Serializable {
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

    private UUID user;

    private List<PlannedRoutePublicDTO> plannedRoutes;

    @JsonCreator
    public DriverPublicDTO(@JsonProperty("carPlate") String carPlate,
                     @JsonProperty("modelAndColour") String modelAndColour,
                     @JsonProperty("capacity") Integer capacity,
                     @JsonProperty("fuelType") String fuelType,
                     @JsonProperty("user") UUID user,
                     @JsonProperty("rides") List<PlannedRoutePublicDTO> plannedRoutes) {
        this.carPlate = carPlate;
        this.modelAndColour = modelAndColour;
        this.capacity = capacity;
        this.fuelType = fuelType;
        this.user = user;
        this.plannedRoutes = plannedRoutes;
    }

    @JsonSetter
    public void setUser(UUID user) {
        this.user = user;
    }
}
