package com.bussin.SpringBack.userTests;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserCreationDTO;
import com.bussin.SpringBack.repositories.UserRepository;
import com.bussin.SpringBack.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCognitoTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AWSCognitoIdentityProvider provider;

    @InjectMocks
    private UserService userService;
    /**
     * Create a new user with valid credentials success
     */
    @Test
    public void createNewUser_validUser_success() {
        userService = new UserService(userRepository, new ModelMapper());

        User user = TestObjects.COGNITO_USER.clone();

        UserCreationDTO userCreationDTO = new UserCreationDTO("Test",
                "P@ssw0rd", TestObjects.COGNITO_USER_DTO);
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.setAmazonCognitoClient(provider);

        assertEquals(user,
                userService.createNewUserWithCognito(userCreationDTO));

        verify(provider, times(1)).signUp(any(SignUpRequest.class));
    }
}
