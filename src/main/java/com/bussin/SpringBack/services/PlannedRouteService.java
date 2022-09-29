package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.CannotConnectToDistanceServerException;
import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.exception.PlannedRouteNotFoundException;
import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.PlannedRouteDTO;
import com.bussin.SpringBack.models.UserPublicDTO;
import com.bussin.SpringBack.repositories.DriverRepository;
import com.bussin.SpringBack.repositories.PlannedRoutesRepository;
import com.fasterxml.jackson.core.type.TypeReference;
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

    public List<PlannedRoute> getAllPlannedRoutes() {
        return plannedRoutesRepository.findAll();
    }

    public PlannedRoute getPlannedRouteById(UUID uuid) {
        return plannedRoutesRepository.findById(uuid)
                .orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                        "planned route with ID " + uuid));
    }

    public List<UserPublicDTO> getPassengersOnRoute(UUID plannedRouteUUID) {
        return plannedRoutesRepository.findById(plannedRouteUUID).map(found -> {
            List<UserPublicDTO> users = new ArrayList<>();
            found.getRides().forEach(ride -> users.add(modelMapper
                    .map(ride.getUser(),UserPublicDTO.class)));
            return users;
        }).orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                "planned route with ID " + plannedRouteUUID));
    }

    //Will link to distance service
    public List<PlannedRoute> getSuggestedRoutes(String tripStart,
                                                 String tripEnd) {
        try{
            URI uri = new URIBuilder(distanceUrl+"/suggestions")
                    .addParameter("tripStart", tripStart)
                    .addParameter("tripEnd", tripEnd)
                    .build();

            HttpGet request = new HttpGet(uri);

            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            CloseableHttpResponse httpResponse =
                    HttpClientBuilder.create().build().execute(request);

            return objectMapper.readValue(httpResponse.getEntity().getContent(),
                            new TypeReference<>() {});
        } catch (URISyntaxException e) {

        } catch (IOException e) {
            throw new CannotConnectToDistanceServerException();
        }

        throw new PlannedRouteNotFoundException(String.format("Cannot find " +
                "any routes suiting a journey from %s to %s",
                tripStart, tripEnd));
    }

    //Will link to distance service
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

        } catch (IOException e) {
            throw new CannotConnectToDistanceServerException();
        }
        throw new IllegalStateException("Something went wrong while finding " +
                "distance");
    }

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

    @Transactional
    public PlannedRoute deletePlannedRouteByID(UUID uuid) {
        return plannedRoutesRepository.findById(uuid).map(found -> {
            plannedRoutesRepository.deleteById(uuid);
            return found;
        }).orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                "planned route with ID " + uuid));
    }
}
