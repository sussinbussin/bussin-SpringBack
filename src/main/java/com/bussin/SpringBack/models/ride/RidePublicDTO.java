package com.bussin.SpringBack.models.ride;

import com.bussin.SpringBack.models.plannedRoute.PlannedRoutePublicDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class RidePublicDTO implements Serializable{
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(generator = "uuid2")
    @Schema(description = "UUID of the ride.",
            example = "844b8d14-ef82-4b27-b9b5-a5e765c1254f")
    private UUID id;

    @Schema(description = "Time when the passenger started riding")
    private Timestamp timestamp;

    @Max(11)
    @Min(1)
    @NotNull(message = "How many passengers can this ride accommodate?")
    @Schema(description = "Number of seats booked", example = "2")
    private Integer passengers;

    @DecimalMin("0")
    @Digits(integer=6, fraction=2)
    @Schema(description = "Cost of the ride", example = "3.00")
    private BigDecimal cost;

    @NotNull
    @Size(max = 512)
    @Schema(description = "Place ID of the passenger's journey start location",
            example = "place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
    private String rideFrom;

    @NotNull
    @Size(max = 512)
    @Schema(description = "Place ID of the passenger's journey destination " +
            "location", example = "place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
    private String rideTo;

    @JsonBackReference
    private PlannedRoutePublicDTO plannedRoute;

    private UUID userId;
}
