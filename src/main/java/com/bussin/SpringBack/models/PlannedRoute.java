package com.bussin.SpringBack.models;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlannedRoute implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @NotNull(message = "Please set a starting point")
    private String plannedFrom;

    @NotNull(message = "Please set a destination.")
    private String plannedTo;

    @NotNull(message = "Date and time should not be empty")
    private LocalDateTime dateTime;

    @Min(1)
    @NotNull(message = "How many passengers?")
    private Integer capacity;

    @OneToMany(mappedBy = "plannedRoute")
    private Set<Ride> rides;

    @ManyToOne
    @JoinColumn(name = "car_plate")
    private Driver driver;

    public void updateFromDTO(PlannedRouteDTO plannedRouteDTO) {
        this.id = plannedRouteDTO.getId();
        this.plannedFrom = plannedRouteDTO.getPlannedFrom();
        this.plannedTo = plannedRouteDTO.getPlannedTo();
        this.dateTime = plannedRouteDTO.getDateTime();
        this.capacity = plannedRouteDTO.getCapacity();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PlannedRoute that = (PlannedRoute) o;
        return plannedFrom != null && Objects.equals(plannedFrom, that.plannedFrom);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
