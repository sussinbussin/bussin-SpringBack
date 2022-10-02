package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class PlannedRoute implements Serializable, Cloneable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @Pattern(regexp = "^[0-9]{6}$")
    @NotNull(message = "Please set a starting point")
    private String plannedFrom;

    @Pattern(regexp = "^[0-9]{6}$")
    @NotNull(message = "Please set a destination.")
    private String plannedTo;

    @NotNull(message = "Date and time should not be empty")
    private LocalDateTime dateTime;

    @Max(11)
    @Min(1)
    @NotNull(message = "How many passengers?")
    private Integer capacity;

    @OneToMany(mappedBy = "plannedRoute")
    private Set<Ride> rides;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @Cascade({ CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DELETE })
    @JoinColumn(name = "carPlate")
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

    @Override
    public PlannedRoute clone() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(this), PlannedRoute.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
