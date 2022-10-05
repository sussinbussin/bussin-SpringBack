package com.bussin.SpringBack.repositories;

import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<UserDTO> findUserById(UUID id);

    Optional<UserDTO> findUserByEmail(String email);

    Optional<User> findByEmail(String email);
}
