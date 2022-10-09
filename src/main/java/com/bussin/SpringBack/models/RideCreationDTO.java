package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RideCreationDTO implements Serializable {
    @Schema(description = "UUID of the user booking the ride",
        example = "844b8d14-ef82-4b27-b9b5-a5e765c1254f")
    private UUID userUUID;

    @Schema(description = "UUID of the planned route the ride is joining",
            example = "844b8d14-ef82-4b27-b9b5-a5e765c1254f")
    private UUID plannedRouteUUID;

    @Schema(description = "Ride information in the format of a RideDTO")
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
