package com.bussin.SpringBack.userTests;

import com.bussin.SpringBack.exception.UserNotFoundException;
import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.Ride;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.repositories.UserRepository;
import com.bussin.SpringBack.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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

    private UserService userService;

    @BeforeEach
    private void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<UserDTO, User>() {
            @Override
            protected void configure() {
                skip(destination.getDriver());
            }
        });

        userService = new UserService(userRepository, modelMapper);
    }

    @Test
    public void getAllUsers_noUsers_success() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        assert (userService.getAllUsers().equals(new ArrayList<>()));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getAllUsers_success() {
        ArrayList<User> users = new ArrayList<>();
        User user = User.builder()
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(System.currentTimeMillis()))
                .mobile("90009000")
                .email("testguy@test.com")
                .isDriver(false)
                .build();
        users.add(user);

        when(userRepository.findAll()).thenReturn(users);

        assert (userService.getAllUsers().equals(users));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void createNewUser_success() {
        UserDTO userDTO = UserDTO.builder()
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(System.currentTimeMillis()))
                .mobile("90009000")
                .email("testguy@test.com")
                .isDriver(false)
                .build();

        User saved = modelMapper.map(userDTO, User.class);
        when(userRepository.save(saved)).thenReturn(saved);

        assert (userService.createNewUser(userDTO).equals(saved));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void createNewUser_invalidParams_exception() {
        UserDTO userDTO = UserDTO.builder()
                .nric("S1234567")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(System.currentTimeMillis()))
                .mobile("10009000")
                .email("testguytest.com")
                .isDriver(false)
                .build();

        assertThrows(ConstraintViolationException.class,
                () -> userService.createNewUser(userDTO));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void createNewUser_alreadyExists_exception() {
        UserDTO userDTO = UserDTO.builder()
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(System.currentTimeMillis()))
                .mobile("90009000")
                .email("testguy@test.com")
                .isDriver(false)
                .build();

        User saved = modelMapper.map(userDTO, User.class);
        when(userRepository.save(saved))
                .thenThrow(new DataIntegrityViolationException("Test"));

        assertThrows(DataIntegrityViolationException.class,
                () -> userService.createNewUser(userDTO));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUser_success() {
        UserDTO userDTO = UserDTO.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
                .nric("S1234567A")
                .name("Test Guy2")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

        User user = User.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

        User userResult = User.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
                .nric("S1234567A")
                .name("Test Guy2")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

        PlannedRoute plannedRoute = PlannedRoute.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555"))
                .plannedFrom("Start")
                .plannedTo("To")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(1)
                .build();

        Ride ride = Ride.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0533"))
                .user(user)
                .cost(BigDecimal.TEN)
                .timestamp(new Timestamp(900000500))
                .passengers(1)
                .build();

        HashSet<Ride> rides = new HashSet<>();
        rides.add(ride);
        plannedRoute.setRides(rides);
        user.setRides(rides);
        userResult.setRides(rides);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(userResult))
                .thenReturn(userResult);

        assertEquals(userService.updateUser(user.getId(), userDTO), userResult);

        verify(userRepository, times(1))
                .findById(any(UUID.class));
        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    public void updateUser_doesntExist_exception() {
        UserDTO userDTO = UserDTO.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
                .nric("S1234567A")
                .name("Test Guy2")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

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
        UserDTO userDTO = UserDTO.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
                .nric("S1234567A")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

        assertThrows(ConstraintViolationException.class,
                () -> userService.updateUser(userDTO.getId(), userDTO));

        verify(userRepository, never()).findById(any(UUID.class));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void updateUser_nonUniqueParams_exception() {
        UserDTO userDTO = UserDTO.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
                .nric("S1234567A")
                .name("Test Guy2")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

        User user = User.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

        User userResult = User.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
                .nric("S1234567A")
                .name("Test Guy2")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

        PlannedRoute plannedRoute = PlannedRoute.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0555"))
                .plannedFrom("Start")
                .plannedTo("To")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(1)
                .build();

        Ride ride = Ride.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a0533"))
                .user(user)
                .cost(BigDecimal.TEN)
                .timestamp(new Timestamp(900000500))
                .passengers(1)
                .build();

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
        User user = User.builder()
                .id(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(900000000))
                .mobile("90009000")
                .email("testguy2@test.com")
                .isDriver(false)
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        assertEquals(user, userService.deleteUser(user.getId()));

        verify(userRepository, times(1))
                .findById(user.getId());
        verify(userRepository, times(1))
                .deleteById(user.getId());
    }

    @Test
    public void deleteUser_doesntExist_exception() {
        UUID uuid = UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");
        when(userRepository.findById(uuid))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(uuid));

        verify(userRepository, times(1)).findById(uuid);
        verify(userRepository, never()).deleteById(uuid);
    }
}


