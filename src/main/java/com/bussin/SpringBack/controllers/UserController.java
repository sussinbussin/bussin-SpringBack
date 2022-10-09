package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserCreationDTO;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.UUID;

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

        return userService.deleteUser(userId);
    }
}