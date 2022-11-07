package com.bussin.SpringBack.models.plannedRoute;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PlannedRouteResultDTO extends PlannedRouteDTO {
    private String carPlate;

    private List<UUID> rides;

    @JsonSetter
    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }
}
