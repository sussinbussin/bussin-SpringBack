package com.bussin.SpringBack.services;

import com.bussin.SpringBack.exception.UserNotFoundException;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User createNewUser(UserDTO userDTO) {
        userDTO.validate();
        return userRepository.save(modelMapper.map(userDTO, User.class));
    }

    public Optional<User> getFullUserById(UUID uuid) {
        return userRepository.findById(uuid);
    }

    public Optional<UserDTO> getUserById(UUID uuid) {
        return userRepository.findUserById(uuid);
    }

    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Transactional
    public User updateUser(UUID uuid, UserDTO userDTO) {
        userDTO.setId(uuid);
        userDTO.validate();
        return userRepository.findById(uuid).map(found -> {
            found.updateFromDTO(userDTO);
            return userRepository.save(found);
        }).orElseThrow(() -> new UserNotFoundException("No user with id " + uuid));
    }

    @Transactional
    public User deleteUser(UUID uuid) {
        return userRepository.findById(uuid).map(found -> {
            userRepository.deleteById(uuid);
            return found;
        }).orElseThrow(() -> new UserNotFoundException("No user with id " + uuid));
    }
}
