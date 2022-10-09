package com.bussin.SpringBack.userTests;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.exception.UserNotFoundException;
import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.Ride;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserCreationDTO;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.repositories.UserRepository;
import com.bussin.SpringBack.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @Value("${cognito.clientid}")
    private String clientID;

    @BeforeEach
    private void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<UserDTO, User>() {
            @Override
            protected void configure() {
                skip(destination.getDriver());
            }
        });

        ClasspathPropertiesFileCredentialsProvider propertiesFileCredentialsProvider =
                new ClasspathPropertiesFileCredentialsProvider("application" +
                        ".properties");

       AWSCognitoIdentityProvider cognitoIdentityProvider =
               AWSCognitoIdentityProviderClientBuilder.standard()
                .withCredentials(propertiesFileCredentialsProvider)
                .withRegion("ap-southeast-1")
                .build();

        userService = new UserService(userRepository, modelMapper);
        userService.setAmazonCognitoClient(cognitoIdentityProvider);
        userService.setClientID("4pv7rad4vqjk19j50hlpg6jleo");
    }

    @Test
    public void getAllUsers_noUsers_success() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        assertEquals(userService.getAllUsers(), new ArrayList<>());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getAllUsers_success() {
        User user = TestObjects.USER.clone();

        ArrayList<User> users = new ArrayList<>();

        users.add(user);

        when(userRepository.findAll()).thenReturn(users);

        assertEquals(userService.getAllUsers(), users);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void createNewUser_success() {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(false);

        User saved = modelMapper.map(userDTO, User.class);
        when(userRepository.save(saved)).thenReturn(saved);

        assert (userService.createNewUser(userDTO).equals(saved));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void createNewUser_invalidParams_exception() {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setId(null);
        userDTO.setIsDriver(false);
        userDTO.setMobile("10009000");

        assertThrows(ConstraintViolationException.class,
                () -> userService.createNewUser(userDTO));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void createNewUser_alreadyExists_exception() {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        // userDTO.setId(null);
        userDTO.setIsDriver(false);

        User saved = modelMapper.map(userDTO, User.class);
        when(userRepository.save(saved))
                .thenThrow(new DataIntegrityViolationException("Test"));

        assertThrows(DataIntegrityViolationException.class,
                () -> userService.createNewUser(userDTO));
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * This test cannot be replicated because it adds to the user pool
     */
//    @Test
//    public void createNewUserWithCognito_success() {
//        UserDTO userDTO = TestObjects.USER_DTO.clone();
//        userDTO.setIsDriver(false);
//        userDTO.setEmail("signupusertest@gmail.com");
//
//        UserCreationDTO userCreationDTO = UserCreationDTO.builder()
//                .userDTO(userDTO)
//                .username("signupuser")
//                .password("P@ssw0rd")
//                .build();
//
//        userService.createNewUserWithCognito(userCreationDTO);
//    }

    @Test
    public void updateUser_success() {
        UUID uuid = UUID.randomUUID();

        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setId(uuid);
        userDTO.setName("Test Guy2");
        userDTO.setIsDriver(false);

        User user = TestObjects.USER.clone();
        user.setId(uuid);

        User userResult = TestObjects.USER.clone();
        userResult.setId(uuid);
        userResult.setName("Test Guy2");

        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();

        Ride ride = TestObjects.RIDE.clone();
        ride.setUser(user);

        HashSet<Ride> rides = new HashSet<>();
        rides.add(ride);
        plannedRoute.setRides(rides);
        user.setRides(rides);
        userResult.setRides(rides);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(userResult))
                .thenReturn(userResult);

        assertEquals(userService.updateUser(userDTO.getId(), userDTO), userResult);

        verify(userRepository, times(1))
                .findById(any(UUID.class));
        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    public void updateUser_doesntExist_exception() {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        when(userRepository.findById(userDTO.getId()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(userDTO.getId(), userDTO));

        verify(userRepository, times(1))
                .findById(any(UUID.class));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void updateUser_invalidParams_exception() {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setName(null);

        assertThrows(ConstraintViolationException.class,
                () -> userService.updateUser(userDTO.getId(), userDTO));

        verify(userRepository, never()).findById(any(UUID.class));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void updateUser_nonUniqueParams_exception() {

        UUID uuid = UUID.randomUUID();

        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setName("Test Guy2");
        userDTO.setIsDriver(false);

        User user = TestObjects.USER.clone();
        user.setId(uuid);

        User userResult = TestObjects.USER.clone();
        userResult.setId(uuid);
        userResult.setName("Test Guy2");

        PlannedRoute plannedRoute = TestObjects.PLANNED_ROUTE.clone();

        Ride ride = TestObjects.RIDE.clone();
        ride.setUser(user);

        HashSet<Ride> rides = new HashSet<>();
        rides.add(ride);
        plannedRoute.setRides(rides);
        user.setRides(rides);
        userResult.setRides(rides);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(userResult))
                .thenThrow(ConstraintViolationException.class);

        assertThrows(ConstraintViolationException.class,
                () -> userService.updateUser(user.getId(), userDTO));

        verify(userRepository, times(1))
                .findById(any(UUID.class));
        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    public void deleteUser_success() {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        when(userRepository.findUserById(userDTO.getId()))
                .thenReturn(Optional.of(userDTO));

        assertEquals(userDTO, userService.deleteUser(userDTO.getId()));

        verify(userRepository, times(1))
                .findUserById(userDTO.getId());
        verify(userRepository, times(1))
                .deleteById(userDTO.getId());
    }

    @Test
    public void deleteUser_doesntExist_exception() {
        UUID uuid = UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");
        when(userRepository.findUserById(uuid))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(uuid));

        verify(userRepository, times(1)).findUserById(uuid);
        verify(userRepository, never()).deleteById(uuid);
    }
}


