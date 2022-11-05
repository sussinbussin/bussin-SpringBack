package com.bussin.SpringBack.models.plannedRoute;

import com.bussin.SpringBack.models.driver.Driver;
import com.bussin.SpringBack.models.ride.Ride;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Planned Route model with route information
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @NotNull
    @Column(scale = 6, precision = 8)
    private BigDecimal originLatitude;

    @NotNull
    @Column(scale = 6, precision = 9)
    private BigDecimal originLongitude;

    @NotNull
    @Column(scale = 6, precision = 8)
    private BigDecimal destLatitude;

    @NotNull
    @Column(scale = 6, precision = 9)
    private BigDecimal destLongitude;

    @JsonManagedReference
    @OneToMany(mappedBy = "plannedRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ride> rides;

    @ManyToOne
    @JoinColumn(name = "carPlate")
    private Driver driver;

    /**
     * Updates a PlannedRoute with PlannedRouteDTO object
     *
     * @param plannedRouteDTO The PlannedRouteDTO object to be updated
     */
    public void updateFromDTO(PlannedRouteDTO plannedRouteDTO) {
        this.id = plannedRouteDTO.getId();
        this.plannedFrom = plannedRouteDTO.getPlannedFrom();
        this.plannedTo = plannedRouteDTO.getPlannedTo();
        this.dateTime = plannedRouteDTO.getDateTime();
        this.capacity = plannedRouteDTO.getCapacity();
        this.originLatitude = plannedRouteDTO.getOriginLatitude();
        this.originLongitude = plannedRouteDTO.getOriginLongitude();
        this.destLatitude = plannedRouteDTO.getDestLatitude();
        this.destLongitude = plannedRouteDTO.getDestLongitude();
    }

    /**
     * Count the number of passengers in a planned route
     *
     * @return The total number of passengers in a planned route
     */
    @JsonIgnore
    public int getPassengerCount() {
        return rides == null ? 0 : rides.stream().mapToInt(Ride::getPassengers).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlannedRoute that = (PlannedRoute) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getPlannedFrom(), that.getPlannedFrom()) && Objects.equals(getPlannedTo(), that.getPlannedTo()) && Objects.equals(getDateTime(), that.getDateTime()) && Objects.equals(getCapacity(), that.getCapacity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPlannedFrom(), getPlannedTo(), getDateTime(), getCapacity());
    }

    @Override
    public PlannedRoute clone() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(this), PlannedRoute.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
