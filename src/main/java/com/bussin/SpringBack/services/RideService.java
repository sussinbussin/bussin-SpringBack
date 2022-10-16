package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.PlannedRouteNotFoundException;
import com.bussin.SpringBack.exception.RideException;
import com.bussin.SpringBack.exception.RideNotFoundException;
import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.Ride;
import com.bussin.SpringBack.models.RideDTO;
import com.bussin.SpringBack.models.User;
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
    private final PricingService pricingService;

    @Autowired
    public RideService(ModelMapper modelMapper, RideRepository rideRepository,
                       PlannedRoutesRepository plannedRoutesRepository,
                       UserService userService, PricingService pricingService) {
        this.modelMapper = modelMapper;
        this.rideRepository = rideRepository;
        this.plannedRoutesRepository = plannedRoutesRepository;
        this.userService = userService;
        this.pricingService = pricingService;
    }

    /**
     * Get all rides
     * @return List of all rides
     */
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    /**
     * Get a ride by ID
     * @param uuid The UUID of Ride
     * @return Ride details
     */
    public Ride getRideById(UUID uuid) {
        return rideRepository.findById(uuid).orElseThrow(() -> new RideNotFoundException("No ride with id " + uuid));
    }

    /**
     * Create a new ride
     * @param rideDTO The ride DTO details to create
     * @param userId The UUID of user
     * @param plannedRouteId The UUID of planned route
     * @return Ride details created
     */
    @Transactional
    public Ride createNewRide(RideDTO rideDTO, UUID userId, UUID plannedRouteId) {
        rideDTO.validate();
        User found = userService.getFullUserById(userId);
        Ride ride = modelMapper.map(rideDTO, Ride.class);
        PlannedRoute plannedRoute = plannedRoutesRepository
                .findPlannedRouteById(plannedRouteId)
                .orElseThrow(() -> new PlannedRouteNotFoundException("No planned route with id " + plannedRouteId));

        ride.setCost(pricingService.getPriceOfRide(plannedRoute));
        ride.setUser(found);

        int passengersOnBoard = ride.getPassengers();

        if(plannedRoute.getRides() != null) {
            for (Ride rideFound : plannedRoute.getRides()) {
                passengersOnBoard += rideFound.getPassengers();
            }
        }

        if (passengersOnBoard > plannedRoute.getCapacity()) {
            throw new RideException("Passenger is over the car's capacity");
        }

        ride.setPlannedRoute(plannedRoute);
        return rideRepository.save(ride);
    }

    /**
     * Update a ride by ID
     * @param rideId The UUID of ride to be updated
     * @param rideDTO The ride DTO to update
     * @return Updated Ride
     */
    @Transactional
    public Ride updateRideById(UUID rideId, RideDTO rideDTO) {
        rideDTO.setId(rideId);
        rideDTO.validate();
        return rideRepository.findById(rideId).map(found -> {
            found.updateFromDTO(rideDTO);
            return rideRepository.save(found);
        }).orElseThrow(() -> new RideNotFoundException("No ride with ID " + rideId));
    }

    /**
     * Delete a ride by ID
     * @param rideId The UUID of ride
     * @return Deleted Ride
     */
    @Transactional
    public Ride deleteRideById(UUID rideId) {
        return rideRepository.findById(rideId).map(found -> {
            rideRepository.deleteById(rideId);
            return found;
        }).orElseThrow(() -> new RideNotFoundException("No ride with ID " + rideId));
    }
}
