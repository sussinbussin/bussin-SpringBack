package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.UserNotFoundException;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addNewUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(UUID uuid) {
        return userRepository.findById(uuid);
    }

    public User updateUser(UUID uuid, User user) {
        return userRepository.findById(uuid).map( found -> {
            user.setId(uuid);
            return userRepository.save(user);
        }).orElseThrow(()-> new UserNotFoundException("No user with id " + uuid));
    }

    public User deleteUser(UUID uuid) {
        return userRepository.findById(uuid).map( found -> {
            userRepository.deleteById(uuid);
            return found;
        }).orElseThrow(()-> new UserNotFoundException("No user with id " + uuid));
    }
}
