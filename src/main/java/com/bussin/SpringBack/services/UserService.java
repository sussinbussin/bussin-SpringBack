package com.bussin.SpringBack.services;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.bussin.SpringBack.exception.UserNotFoundException;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserCreationDTO;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Value("${cognito.clientid}")
    private String clientID;

    private AWSCognitoIdentityProvider client;

    @Autowired
    public void setAmazonCognitoClient(AWSCognitoIdentityProvider client) {
        this.client = client;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

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

    @Transactional
    public User createNewUserWithCognito(UserCreationDTO userCreationDTO) {
        userCreationDTO.getUserDTO().validate();

        List<AttributeType> attributeTypes = new ArrayList<>();
        attributeTypes.add(new AttributeType()
                .withName("email")
                .withValue(userCreationDTO.getUserDTO().getEmail()));

        SignUpRequest signUpRequest = new SignUpRequest()
                .withClientId(clientID)
                .withUsername(userCreationDTO.getUsername())
                .withPassword(userCreationDTO.getPassword())
                .withUserAttributes(attributeTypes);

        client.signUp(signUpRequest);

        return null;
    }

    public User getFullUserById(UUID uuid) {
        return userRepository.findById(uuid).orElseThrow(()
                -> new UserNotFoundException("No user with id " + uuid));
    }

    public UserDTO getUserById(UUID uuid) {
        return userRepository.findUserById(uuid).orElseThrow(()
                -> new UserNotFoundException("No user with id " + uuid));
    }

    public UserDTO getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(()
                -> new UserNotFoundException("No user with email " + email));
    }

    public User getFullUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()
                -> new UserNotFoundException("No user with email " + email));
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
    public UserDTO deleteUser(UUID uuid) {
        return userRepository.findUserById(uuid).map(found -> {
            userRepository.deleteById(uuid);
            return found;
        }).orElseThrow(() -> new UserNotFoundException("No user with id " + uuid));
    }
}
