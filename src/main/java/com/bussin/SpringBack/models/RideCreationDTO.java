package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RideCreationDTO implements Serializable {
    private UUID userUUID;
    private UUID plannedRouteUUID;
    private RideDTO rideDTO;

    @JsonCreator
    public RideCreationDTO(@JsonProperty("userUUID") UUID userUUID,
                           @JsonProperty("plannedRouteUUID") UUID plannedRouteUUID,
                           @JsonProperty("rideDTO") RideDTO rideDTO) {
        this.userUUID = userUUID;
        this.plannedRouteUUID = plannedRouteUUID;
        this.rideDTO = rideDTO;
    }
}
