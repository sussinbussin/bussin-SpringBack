package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.exception.PlannedRouteNotFoundException;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoutePublicDTO;
import com.bussin.SpringBack.models.user.UserPublicDTO;
import com.bussin.SpringBack.repositories.DriverRepository;
import com.bussin.SpringBack.repositories.PlannedRoutesRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlannedRouteService {
    private final ModelMapper modelMapper;
    private final PlannedRoutesRepository plannedRoutesRepository;
    private final DriverRepository driverRepository;

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
    public PlannedRoutePublicDTO getPlannedRouteById(UUID uuid) {
        PlannedRoute plannedRoute = plannedRoutesRepository.findById(uuid)
                .orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                        "planned route with ID " + uuid));
        return modelMapper.map(plannedRoute,
                        PlannedRoutePublicDTO.class);
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
    public List<PlannedRoutePublicDTO> getPlannedRouteAfterTime(LocalDateTime dateTime) {
        return plannedRoutesRepository.findPlannedRouteByDateTimeAfter(dateTime)
                .stream().map(plannedRoute -> modelMapper.map(plannedRoute, PlannedRoutePublicDTO.class))
                .collect(Collectors.toList());
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
        plannedRouteDTO.setId(null);
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
    public PlannedRoutePublicDTO updatePlannedRouteById(UUID uuid,
                                               PlannedRouteDTO plannedRouteDTO) {
        plannedRouteDTO.setId(uuid);
        plannedRouteDTO.validate();
        return modelMapper.map(plannedRoutesRepository.findById(uuid).map(found -> {
            modelMapper.map(plannedRouteDTO, found);
            return plannedRoutesRepository.save(found);
        }).orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                "planned route with ID " + uuid)), PlannedRoutePublicDTO.class);
    }

    /**
     * Delete a planned route by ID
     * @param uuid The UUID of planned route
     * @return Deleted Planned Route if found
     */
    @Transactional
    public PlannedRoutePublicDTO deletePlannedRouteByID(UUID uuid) {
        return modelMapper.map(plannedRoutesRepository.findById(uuid).map(found -> {
            plannedRoutesRepository.deleteById(uuid);
            return found;
        }).orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                "planned route with ID " + uuid)), PlannedRoutePublicDTO.class);
    }
}
