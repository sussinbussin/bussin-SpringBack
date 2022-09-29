package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.exception.PlannedRouteNotFoundException;
import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.PlannedRouteDTO;
import com.bussin.SpringBack.models.UserPublicDTO;
import com.bussin.SpringBack.repositories.DriverRepository;
import com.bussin.SpringBack.repositories.PlannedRoutesRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            found.getRides().forEach(ride -> {
                users.add(modelMapper.map(ride.getUser(),UserPublicDTO.class));
            });
            return users;
        }).orElseThrow(() -> new PlannedRouteNotFoundException("No " +
                "planned route with ID " + plannedRouteUUID));
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
