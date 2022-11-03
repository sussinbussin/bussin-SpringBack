package com.bussin.SpringBack.repositories;

import com.bussin.SpringBack.models.ride.Ride;
import com.bussin.SpringBack.models.ride.RideDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {
    Optional<RideDTO> findRideById(UUID uuid);
}
