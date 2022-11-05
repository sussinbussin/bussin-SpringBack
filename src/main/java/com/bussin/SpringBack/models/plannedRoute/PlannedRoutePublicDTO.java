package com.bussin.SpringBack.models.plannedRoute;

import com.bussin.SpringBack.models.driver.DriverPublicDTO;
import com.bussin.SpringBack.models.ride.RidePublicDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSetter;
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
public class PlannedRoutePublicDTO extends PlannedRouteDTO {
    @JsonManagedReference
    private List<RidePublicDTO> rides;

    private DriverPublicDTO driver;

    @JsonSetter
    public void setRides(List<RidePublicDTO> rides) {
        this.rides = rides;
    }

    @JsonSetter
    public void setDriver(DriverPublicDTO driver) {
        this.driver = driver;
    }
}
