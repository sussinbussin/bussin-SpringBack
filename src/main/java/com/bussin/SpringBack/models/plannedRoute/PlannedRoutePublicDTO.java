package com.bussin.SpringBack.models.plannedRoute;

import com.bussin.SpringBack.models.driver.DriverPublicDTO;
import com.bussin.SpringBack.models.ride.RideReturnDTO;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
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
