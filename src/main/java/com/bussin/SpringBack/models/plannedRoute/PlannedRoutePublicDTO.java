package com.bussin.SpringBack.models.plannedRoute;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A class of PlannedRoutePublicDTO object for Drivers to retrieve information by car plate.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PlannedRoutePublicDTO extends PlannedRouteDTO {
    private String carPlate;

    @JsonSetter
    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }
}
