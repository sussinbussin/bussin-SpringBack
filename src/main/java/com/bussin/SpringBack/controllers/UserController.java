package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserCreationDTO;
import com.bussin.SpringBack.models.user.UserDTO;
import com.bussin.SpringBack.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.UUID;

import static com.bussin.SpringBack.utils.NotAuthorizedUtil.isSameUserEmail;
import static com.bussin.SpringBack.utils.NotAuthorizedUtil.isSameUserId;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets all users.
     *
     * @return List of all users.
     */
    @GetMapping
    @Operation(summary = "Gets all users")
    public List<User> getAllUsers() {
        log.info("Retrieving all users");
        return userService.getAllUsers();
    }

    /**
     * Gets a user DTO by their UUID.
     *
     * @param userId The UUID
     * @return The user DTO if found, else null
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Gets a user by their ID")
    public UserDTO getUserById(@Valid @PathVariable final UUID userId) {
        isSameUserId(userId);
        log.info(String.format("Retrieving user %s", userId));
        return userService.getUserById(userId);
    }

    /**
     * Gets a full user by their UUID.
     *
     * @param userId The UUID
     * @return The full user if found, else null
     */
    @Operation(summary = "Gets a user and all their related info by their ID")
    @GetMapping("/full/{userId}")
    public User getFullUserById(@Valid @PathVariable final UUID userId) {
        isSameUserId(userId);
        log.info(String.format("Retrieving full user %s", userId));
        return userService.getFullUserById(userId);
    }

    /**
     * Gets a user by their email.
     *
     * @param email The email of the user to return
     * @return The user with the specified email
     */
    @Operation(summary = "Gets a user by their email")
    @GetMapping("/byEmail/{email}")
    public UserDTO getUserByEmail(@Valid @Email @PathVariable final String email) {
        isSameUserEmail(email);
        log.info(String.format("Retrieving user with email %s", email));
        return userService.getUserByEmail(email);
    }

    /**
     * Add new user object.
     *
     * @param userDTO User object to add.
     * @return A user that was added.
     */
    @Operation(summary = "Creates a new user")
    @PostMapping
    public User createNewUser(@Valid @RequestBody final UserDTO userDTO) {
        log.info(String.format("Creating user %s", userDTO));
        return userService.createNewUser(userDTO);
    }

    /**
     * Add new user object.
     *
     * @param userCreationDTO User object to add.
     * @return A user that was added.
     */
    @Operation(summary = "Creates a new user")
    @PostMapping("/wCognito/create")
    public User createNewUserWithCognito(@Valid @RequestBody final UserCreationDTO userCreationDTO) {
        log.info(String.format("Creating user with cognito %s: %s",
                userCreationDTO.getUsername(), userCreationDTO.getUserDTO()));
        return userService.createNewUserWithCognito(userCreationDTO);
    }


    /**
     * Updates a user.
     *
     * @param userId  UUID of the user to update
     * @param userDTO Object with the fields to update
     * @return Full user with the updated fields
     */
    @Operation(summary = "Updates a user")
    @PutMapping("/{userId}")
    public User updateUserById(@Valid @PathVariable final UUID userId,
                               @Valid @RequestBody final UserDTO userDTO) {
        isSameUserId(userId);
        log.info(String.format("Updating user %s with %s", userId, userDTO));
        return userService.updateUser(userId, userDTO);
    }

    /**
     * Deletes a user.
     *
     * @param userId UUID of the user to delete
     * @return Full deleted user
     */
    @Operation(summary = "Deletes a user by their ID")
    @DeleteMapping("/{userId}")
    public UserDTO deleteUserById(@Valid @PathVariable final UUID userId) {
        isSameUserId(userId);
        log.info(String.format("Deleting user %s", userId));
        return userService.deleteUser(userId);
    }
}