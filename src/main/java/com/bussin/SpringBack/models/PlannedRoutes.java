package com.bussin.SpringBack.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "planned_from_unique", columnNames = "plannedFrom"),
    @UniqueConstraint(name = "driver_from_unique", columnNames = "driverFrom")
})
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class PlannedRoutes {
    @Id @NotNull(message = "Please set a starting point")
    private String plannedFrom;

    @NotNull(message = "Please set a destination.")
    private String plannedTo;

    @NotNull(message = "Date and time should not be empty")
    private LocalDateTime dateTime;

    @NotNull(message = "How often will it repeat?")
    private Integer frequency;

    public PlannedRoutes(String plannedFrom, String plannedTo, LocalDateTime dateTime, Integer frequency) {
        this.plannedFrom = plannedFrom;
        this.plannedTo = plannedTo;
        this.dateTime = dateTime;
        this.frequency = frequency;
    }
}
