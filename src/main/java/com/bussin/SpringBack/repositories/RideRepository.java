package com.bussin.SpringBack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bussin.SpringBack.models.*;
import java.util.*;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {
    Optional<RideDTO> findRideById(UUID uuid);
}
