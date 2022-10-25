package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.CannotConnectToDistanceServerException;
import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.exception.PlannedRouteNotFoundException;
import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.PlannedRouteDTO;
import com.bussin.SpringBack.models.UserPublicDTO;
import com.bussin.SpringBack.repositories.DriverRepository;
import com.bussin.SpringBack.repositories.PlannedRoutesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.net.URIBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PlannedRouteService {
    private final ModelMapper modelMapper;
    private final PlannedRoutesRepository plannedRoutesRepository;
    private final DriverRepository driverRepository;

    @Value("distance.serverUrl")
    private String distanceUrl;

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public PlannedRouteService(ModelMapper modelMapper,
                               PlannedRoutesRepository plannedRoutesRepository,
                               DriverRepository driverRepository) {
        this.modelMapper = modelMapper;
        this.plannedRoutesRepository = plannedRoutesRepository;
        this.driverRepository = driverRepository;
    }

    /**
     * Get all planned routes
     * @return List of planned routes
     */
    public List<PlannedRoute> getAllPlannedRoutes() {
        return plannedRoutesRepository.findAll();
    }

    /**
     * Get a planned route by ID
     * @param uuid The UUID of planned route
     * @return Planned route if found
     */
    public PlannedRoute getPlannedRouteById(UUID uuid) {
        return plannedRoutesRepository.findById(uuid)
                .orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                        "planned route with ID " + uuid));
    }

    /**
     * Get all passengers on a particular route
     * @param plannedRouteUUID The UUID of planned route
     * @return List of user public DTO if planned route is found
     */
    public List<UserPublicDTO> getPassengersOnRoute(UUID plannedRouteUUID) {
        return plannedRoutesRepository.findById(plannedRouteUUID).map(found -> {
            List<UserPublicDTO> users = new ArrayList<>();
            found.getRides().forEach(ride -> users.add(modelMapper
                    .map(ride.getUser(),UserPublicDTO.class)));
            return users;
        }).orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                "planned route with ID " + plannedRouteUUID));
    }

    /**
     * Get a planned route after specific time
     * @param dateTime The LocalDateTime
     * @return List of planned routes after datetime
     */
    public List<PlannedRoute> getPlannedRouteAfterTime(LocalDateTime dateTime) {
        return plannedRoutesRepository.findPlannedRouteByDateTime(dateTime);
    }

    /**
     * Get distance between the trip starts and trip ends
     * @param tripStart The String
     * @param tripEnd The String
     * @return The distance of between the start and end of trip
     */
    public BigDecimal getDistanceBetween(String tripStart, String tripEnd) {
        try{
            URI uri = new URIBuilder(distanceUrl+"/distance")
                    .addParameter("tripStart", tripStart)
                    .addParameter("tripEnd", tripEnd)
                    .build();

            HttpGet request = new HttpGet(uri);

            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            CloseableHttpResponse httpResponse =
                    HttpClientBuilder.create().build().execute(request);

            return objectMapper.readValue(httpResponse.getEntity().getContent(),
                    BigDecimal.class);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Something went wrong while finding " +
                    "distance", e);
        } catch (IOException e) {
            throw new CannotConnectToDistanceServerException();
        }
    }

    /**
     * Create a new planned route for a driver
     * @param plannedRouteDTO The PlannedRouteDTO with details to create
     * @param carPlate The String of driver that created the planned route
     * @return Created PlannedRoute if driver is found
     */
    @Transactional
    public PlannedRoute createNewPlannedRoute(PlannedRouteDTO plannedRouteDTO,
                                              String carPlate) {
        plannedRouteDTO.validate();
        return driverRepository.findDriverByCarPlate(carPlate).map(found -> {
            PlannedRoute plannedRoute = modelMapper.map(plannedRouteDTO,
                    PlannedRoute.class);
            plannedRoute.setDriver(found);
            return plannedRoutesRepository.save(plannedRoute);
        }).orElseThrow(() -> new DriverNotFoundException("No driver with car " +
                "plate " + carPlate));
    }

    /**
     * Update a planned route by ID
     * @param uuid The UUID of Planned Route to be updated
     * @param plannedRouteDTO The PlannedRouteDTO details to update
     * @return Updated Planned Route if found
     */
    @Transactional
    public PlannedRoute updatePlannedRouteById(UUID uuid,
                                               PlannedRouteDTO plannedRouteDTO) {
        plannedRouteDTO.setId(uuid);
        plannedRouteDTO.validate();
        return plannedRoutesRepository.findById(uuid).map(found -> {
            found.updateFromDTO(plannedRouteDTO);
            return plannedRoutesRepository.save(found);
        }).orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                "planned route with ID " + uuid));
    }

    /**
     * Delete a planned route by ID
     * @param uuid The UUID of planned route
     * @return Deleted Planned Route if found
     */
    @Transactional
    public PlannedRoute deletePlannedRouteByID(UUID uuid) {
        return plannedRoutesRepository.findById(uuid).map(found -> {
            plannedRoutesRepository.deleteById(uuid);
            return found;
        }).orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                "planned route with ID " + uuid));
    }
}
