package com.bussin.SpringBack.models.plannedRoute;

import com.bussin.SpringBack.models.ride.RidePublicDTO;
import com.bussin.SpringBack.models.ride.RideReturnDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class PlannedRouteResultDTO extends PlannedRouteDTO {
    private String carPlate;

    @JsonManagedReference
    private List<RideReturnDTO> rides;

    @JsonSetter
    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }
}
