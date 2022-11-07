package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.PlannedRouteNotFoundException;
import com.bussin.SpringBack.exception.RideException;
import com.bussin.SpringBack.exception.RideNotFoundException;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.ride.Ride;
import com.bussin.SpringBack.models.ride.RideDTO;
import com.bussin.SpringBack.models.ride.RidePublicDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.repositories.PlannedRoutesRepository;
import com.bussin.SpringBack.repositories.RideRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class RideService {
    private final ModelMapper modelMapper;
    private final RideRepository rideRepository;
    private final PlannedRoutesRepository plannedRoutesRepository;
    private final UserService userService;

    @Autowired
    public RideService(ModelMapper modelMapper, RideRepository rideRepository,
                       PlannedRoutesRepository plannedRoutesRepository,
                       UserService userService) {
        this.modelMapper = modelMapper;
        this.rideRepository = rideRepository;
        this.plannedRoutesRepository = plannedRoutesRepository;
        this.userService = userService;
    }

    /**
     * Get all rides
     *
     * @return List of all rides
     */
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    /**
     * Get a ride by ID
     *
     * @param uuid The UUID of Ride
     * @return Ride details
     */
    public RidePublicDTO getRideById(final UUID uuid) {
        return modelMapper.map(rideRepository.findById(uuid)
                                             .orElseThrow(() -> new RideNotFoundException("No ride with id "
                                                     + uuid)), RidePublicDTO.class);
    }

    /**
     * Create a new ride
     *
     * @param rideDTO        The ride DTO details to create
     * @param userId         The UUID of user
     * @param plannedRouteId The UUID of planned route
     * @return Ride details created
     */
    @Transactional
    public RidePublicDTO createNewRide(final RideDTO rideDTO, final UUID userId,
                                       final UUID plannedRouteId) {
        rideDTO.validate();
        rideDTO.setId(null);

        User found = userService.getFullUserById(userId);
        Ride ride = modelMapper.map(rideDTO, Ride.class);
        PlannedRoute plannedRoute = plannedRoutesRepository
                .findPlannedRouteById(plannedRouteId)
                .orElseThrow(() -> new PlannedRouteNotFoundException("No planned route with id " + plannedRouteId));

        ride.setUser(found);

        if (ride.getPassengers() + plannedRoute.getPassengerCount() > plannedRoute.getCapacity()) {
            throw new RideException("Passenger is over the car's capacity");
        }

        ride.setPlannedRoute(plannedRoute);
        return modelMapper.map(rideRepository.save(ride), RidePublicDTO.class);
    }

    /**
     * Update a ride by ID
     *
     * @param rideId  The UUID of ride to be updated
     * @param rideDTO The ride DTO to update
     * @return Updated Ride
     */
    @Transactional
    public RidePublicDTO updateRideById(final UUID rideId, final RideDTO rideDTO) {
        rideDTO.setId(rideId);
        rideDTO.validate();
        return modelMapper.map(rideRepository.findById(rideId).map(found -> {
            modelMapper.map(rideDTO, found);
            return rideRepository.save(found);
        }).orElseThrow(() -> new RideNotFoundException("No ride with ID " + rideId)), RidePublicDTO.class);
    }

    /**
     * Delete a ride by ID
     *
     * @param rideId The UUID of ride
     * @return Deleted Ride
     */
    @Transactional
    public RidePublicDTO deleteRideById(final UUID rideId) {
        return modelMapper.map(rideRepository.findById(rideId).map(found -> {
            rideRepository.deleteById(rideId);
            return found;
        }).orElseThrow(() -> new RideNotFoundException("No ride with ID " + rideId)), RidePublicDTO.class);
    }
}
