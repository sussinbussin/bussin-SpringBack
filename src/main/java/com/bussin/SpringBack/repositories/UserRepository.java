package com.bussin.SpringBack.repositories;

import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<UserDTO> findUserById(UUID id);

    Optional<UserDTO> findUserByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByNric(String nric);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);
}
