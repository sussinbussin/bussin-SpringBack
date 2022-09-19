package com.bussin.SpringBack.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bussin.SpringBack.models.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {

}
