package com.bussin.SpringBack.models;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class Ride {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @NotNull(message = "How many passengers can this ride accommodate?")
    private Timestamp timestamp;

    @NotNull
    @Min(1)
    private Integer passengers;

    @NotNull
    @DecimalMin("0")
    private BigDecimal cost;

    @ManyToOne
    @JoinColumn(name = "planned_route_id")
    private PlannedRoute plannedRoute;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
