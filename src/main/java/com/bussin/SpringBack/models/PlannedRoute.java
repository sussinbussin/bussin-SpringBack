package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.media.Schema;
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
import javax.validation.constraints.Size;
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
    @Schema(description = "UUID of the planned route.",
            example = "844b8d14-ef82-4b27-b9b5-a5e765c1254f")
    private UUID id;

    @NotNull
    @Size(max = 512)
    @Schema(description = "Place ID of the starting location",
        example = "place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
    private String plannedFrom;

    @NotNull
    @Size(max = 512)
    @Schema(description = "Place ID of the destination location",
            example = "place_id:ChIJ483Qk9YX2jERA0VOQV7d1tY")
    private String plannedTo;

    @NotNull(message = "Date and time should not be empty")
    @Schema(description = "Date and Time of when this planned route will be " +
            "travelled.", example = "2022-10-09T01:09:34.337Z")
    private LocalDateTime dateTime;

    @Max(11)
    @Min(1)
    @NotNull(message = "How many passengers?")
    @Schema(description = "How many passengers the driver is willing to take",
            example = "2")
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
