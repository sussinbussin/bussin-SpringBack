package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.exception.WrongUserException;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserCreationDTO;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.UUID;

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
    public UserDTO getUserById(@Valid @PathVariable UUID userId) {
        isSameUser(userId);
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
    public User getFullUserById(@Valid @PathVariable UUID userId) {
        isSameUser(userId);
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
    public UserDTO getUserByEmail(@Valid @Email @PathVariable String email) {
        isSameUser(email);
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
    public User createNewUser(@Valid @RequestBody UserDTO userDTO) {
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
    public User createNewUserWithCognito(@Valid @RequestBody UserCreationDTO userCreationDTO) {
        log.info(String.format("Creating user with cognito %s",
                userCreationDTO));
        return userService.createNewUserWithCognito(userCreationDTO);
    }


    /**
     * Updates a user.
     *
     * @param userId    UUID of the user to update
     * @param userDTO Object with the fields to update
     * @return Full user with the updated fields
     */
    @Operation(summary = "Updates a user")
    @PutMapping("/{userId}")
    public User updateUserById(@Valid @PathVariable UUID userId,
                               @Valid @RequestBody UserDTO userDTO) {
        isSameUser(userId);
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
    public UserDTO deleteUserById(@Valid @PathVariable UUID userId) {
        isSameUser(userId);
        log.info(String.format("Deleting user %s", userId));
        return userService.deleteUser(userId);
    }

    /**
     * Check if the User querying for the method is the same user using UserID
     * @param userID The UUID of the User to be accessed
     */
    private void isSameUser(UUID userID) {
        User loggedIn =
                (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!loggedIn.getId().toString().equals(userID.toString())){
            wrongUserResponse(loggedIn.getId().toString(), userID.toString());
        }
    }

    /**
     * Check if the User querying for the method is the same user using Email
     * @param email The email of the User to be accessed
     */
    private void isSameUser(String email) {
        User loggedIn =
                (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!loggedIn.getEmail().equals(email)){
            wrongUserResponse(loggedIn.getEmail(), email);
        }
    }

    /**
     * Throw new WrongUserException when User is not the same
     * @param loggedIn The UUID of User
     * @param attempted The UUID of the User to be accessed
     */
    private void wrongUserResponse(String loggedIn, String attempted) {
        String response = String.format("Attempted modification of another user! " +
                        "%s tried to modify %s", loggedIn, attempted);
        log.warn(response);
        throw new WrongUserException(response);
    }
}