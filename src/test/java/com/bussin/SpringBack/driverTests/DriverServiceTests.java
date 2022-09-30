package com.bussin.SpringBack.driverTests;

import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.models.DriverDTO;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.repositories.DriverRepository;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTests {
    @Mock
    private DriverRepository driverRepository;

    @Mock
    private UserService userService;

    private ModelMapper modelMapper;

    @InjectMocks
    private DriverService driverService;

    @BeforeEach
    private void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<UserDTO, User>() {
            @Override
            protected void configure() {
                skip(destination.getDriver());
            }
        });

        driverService = new DriverService(driverRepository, modelMapper);
        driverService.setUserService(userService);
    }

    @Test
    public void addNewDriver_success() {
        UUID uuid = UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");
        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345A")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id(uuid)
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(System.currentTimeMillis()))
                .mobile("90009000")
                .email("testguy@test.com")
                .isDriver(false)
                .build();

        User userResult = User.builder()
                .id(uuid)
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(System.currentTimeMillis()))
                .mobile("90009000")
                .email("testguy@test.com")
                .isDriver(true)
                .build();

        Driver driverResult = Driver.builder()
                .carPlate("SAA12345A")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .user(userResult)
                .build();

        when(userService.getUserById(uuid))
                .thenReturn(userDTO);
        when(userService.updateUser(uuid, userDTO))
                .thenAnswer(invocationOnMock ->
                    ((UserDTO)invocationOnMock.getArgument(1)).getIsDriver()?
                            //Will end test if bad
                            userResult:null);
        when(driverRepository.save(driverResult))
                .thenReturn(driverResult);

        assertEquals(driverService.addNewDriver(uuid, driverDTO), driverResult);

        verify(userService, times(1)).getUserById(uuid);
        verify(userService, times(1))
                .updateUser(uuid, userDTO);
        verify(driverRepository, times(1)).save(driverResult);
    }

    @Test
    public void addNewDriver_invalidParams_exception() {
        UUID uuid = UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345A")
                .modelAndColour("Flamingo MrBean Car")
                //Bad capacity
                .capacity(1)
                .fuelType("TypePremium")
                .build();

        assertThrows(ConstraintViolationException.class,
                () -> driverService.addNewDriver(uuid, driverDTO));

        verify(userService, never()).getUserById(uuid);
    }

    @Test
    public void addNewDriver_userNotFound_exception(){
        //TODO: Write test
    }

    @Test
    public void updateDriver_success() {
        UUID uuid = UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345A")
                //Modified model and colour
                .modelAndColour("Mystery Machine")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        User userResult = User.builder()
                .id(uuid)
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(System.currentTimeMillis()))
                .mobile("90009000")
                .email("testguy@test.com")
                .isDriver(true)
                .build();

        Driver driver = Driver.builder()
                .carPlate("SAA12345A")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .user(userResult)
                .build();

        Driver driverResult = Driver.builder()
                .carPlate("SAA12345A")
                .modelAndColour("Mystery Machine")
                .capacity(2)
                .fuelType("TypePremium")
                .user(userResult)
                .build();

        userResult.setDriver(driver);
        when(driverRepository.findDriverByCarPlate(driverDTO.getCarPlate()))
                .thenReturn(Optional.of(driver));
        when(driverRepository.save(driverResult))
                .thenReturn(driverResult);

        assertEquals(driverService.updateDriver(driverDTO.getCarPlate(),
                driverDTO), driverResult);

        verify(driverRepository, times(1))
                .findDriverByCarPlate(driverDTO.getCarPlate());
        verify(driverRepository, times(1))
                .save(driverResult);
    }

    @Test
    public void deleteDriver_success() {
        UUID uuid = UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");
        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345A")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id(uuid)
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(System.currentTimeMillis()))
                .mobile("90009000")
                .email("testguy@test.com")
                .isDriver(false)
                .build();

        User userResult = User.builder()
                .id(uuid)
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(System.currentTimeMillis()))
                .mobile("90009000")
                .email("testguy@test.com")
                .isDriver(true)
                .build();

        User userResultGood = User.builder()
                .id(uuid)
                .nric("S1234567A")
                .name("Test Guy")
                .address("444333")
                .dob(new Date(System.currentTimeMillis()))
                .mobile("90009000")
                .email("testguy@test.com")
                .isDriver(false)
                .build();

        Driver driverResult = Driver.builder()
                .carPlate("SAA12345A")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .user(userResult)
                .build();

        when(driverRepository.findDriverByCarPlate(driverResult.getCarPlate()))
                .thenReturn(Optional.of(driverResult));

        when(userService.updateUser(uuid, userDTO))
                .thenAnswer(invocationOnMock ->
                        ((UserDTO)invocationOnMock.getArgument(1)).getIsDriver()?
                                //Will end test if bad
                                userResultGood:null);

        assertEquals(driverService
                .deleteDriver(driverDTO.getCarPlate()), driverResult);

        verify(driverRepository, times(1))
                .findDriverByCarPlate(driverResult.getCarPlate());
        verify(userService, times(1))
                .updateUser(uuid, userDTO);
    }
}
