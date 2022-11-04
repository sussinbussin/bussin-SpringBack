package com.bussin.SpringBack.models.plannedRoute;

import com.bussin.SpringBack.models.driver.DriverPublicDTO;
import com.bussin.SpringBack.models.ride.RideReturnDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * A subset of PlannedRoute with sensitive information
 */

@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class PlannedRoutePublicDTO extends PlannedRouteDTO {
    private List<RideReturnDTO> rides;

    private DriverPublicDTO driver;

    @JsonSetter
    public void setRides(List<RideReturnDTO> rides) {
        this.rides = rides;
    }

    @JsonSetter
    public void setDriver(DriverPublicDTO driver) {
        this.driver = driver;
    }
}
