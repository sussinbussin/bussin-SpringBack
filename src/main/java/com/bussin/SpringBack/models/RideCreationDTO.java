package com.bussin.SpringBack.models;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RideCreationDTO implements Serializable {
    private UUID userUUID;
    private UUID plannedRouteUUID;
    private RideDTO rideDTO;
}
