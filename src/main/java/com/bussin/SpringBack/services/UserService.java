package com.bussin.SpringBack.services;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.bussin.SpringBack.exception.UserNotFoundException;
import com.bussin.SpringBack.models.user.SignUpUniqueRequest;
import com.bussin.SpringBack.models.user.SignUpUniqueResponse;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserCreationDTO;
import com.bussin.SpringBack.models.user.UserDTO;
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
    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Value("${clientid}")
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

    /**
     * Get all user
     * @return List of users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Create a new user
     * @param userDTO The UserDTO
     * @return The user, if created successfully
     */
    @Transactional
    public User createNewUser(UserDTO userDTO) {
        userDTO.validate();
        userDTO.setId(null);
        return userRepository.save(modelMapper.map(userDTO, User.class));
    }

    /**
     * Create a new user with cognito
     * @param userCreationDTO The UserCreationDTO
     * @return The user, if created successfully
     */
    @Transactional
    public User createNewUserWithCognito(UserCreationDTO userCreationDTO) {
        userCreationDTO.getUserDTO().validate();

        User user = userRepository.save(modelMapper.map(userCreationDTO.getUserDTO(),
                User.class));

        System.out.println(user);

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

        return user;
    }

    /**
     * Get all the details of a user by ID
     * @param uuid The UUID of user
     * @return The full user, if found
     */
    public User getFullUserById(UUID uuid) {
        return userRepository.findById(uuid).orElseThrow(()
                -> new UserNotFoundException("No user with id " + uuid));
    }

    /**
     * Get a user by ID
     * @param uuid The UUID of user
     * @return The user DTO, if found
     */
    public UserDTO getUserById(UUID uuid) {
        return userRepository.findUserById(uuid).orElseThrow(()
                -> new UserNotFoundException("No user with id " + uuid));
    }

    /**
     * Get a user by email
     * @param email The email of user to return
     * @return The user DTO, if found
     */
    public UserDTO getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(()
                -> new UserNotFoundException("No user with email " + email));
    }

    /**
     * Get all the details of a user by email
     * @param email The email of user to return
     * @return The full user, if found
     */
    public User getFullUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()
                -> new UserNotFoundException("No user with email " + email));
    }

    /**
     * Update a user with new details using ID
     * @param uuid The UUID of user
     * @param userDTO The new user DTO details to update
     * @return The full details of user, if found
     */
    @Transactional
    public User updateUser(UUID uuid, UserDTO userDTO) {
        userDTO.setId(uuid);
        userDTO.validate();
        return userRepository.findById(uuid).map(found -> {
            found.updateFromDTO(userDTO);
            return userRepository.save(found);
        }).orElseThrow(() -> new UserNotFoundException("No user with id " + uuid));
    }

    /**
     * Delete a user by ID
     * @param uuid The UUID of user
     * @return The user DTO, if found
     */
    @Transactional
    public UserDTO deleteUser(UUID uuid) {
        return userRepository.findUserById(uuid).map(found -> {
            userRepository.deleteById(uuid);
            return found;
        }).orElseThrow(() -> new UserNotFoundException("No user with id " + uuid));
    }

    /**
     * Checks if the provided credentials are unique
     * @param request The credentials to check
     * @return
     */
    public SignUpUniqueResponse isUniqueCheck(SignUpUniqueRequest request) {
        return SignUpUniqueResponse.builder()
                .nricUnique(request.getNric() == null || !userRepository.existsByNric(request.getNric()))
                .emailUnique(request.getEmail() == null || !userRepository.existsByEmail(request.getEmail()))
                .mobileUnique(request.getMobile() == null || !userRepository.existsByMobile(request.getMobile()))
                .build();
    }
}
