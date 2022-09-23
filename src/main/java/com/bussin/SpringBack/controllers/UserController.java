package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/users")
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
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }

    /**
     * Gets a user DTO by their UUID.
     *
     * @param uuid The UUID
     * @return The user DTO if found, else null
     */
    @GetMapping("/{uuid}")
    public UserDTO getUserById(@Valid @PathVariable UUID uuid) {
        return userService.getUserById(uuid).orElse(null);
    }

    /**
     * Gets a full user by their UUID.
     *
     * @param uuid The UUID
     * @return The full user if found, else null
     */
    @GetMapping("/full/{uuid}")
    public User getFullUserById(@Valid @PathVariable UUID uuid) {

        return userService.getFullUserById(uuid).orElse(null);
    }

    /**
     * Gets a user by their email.
     *
     * @param email The email of the user to return
     * @return The user with the specified email
     */
    @GetMapping("/byEmail/{email}")
<<<<<<< HEAD
    public UserDTO getUserByEmail(@Valid @PathVariable String email) {
=======
    public UserDTO getUserByEmail(@Email @PathVariable String email) {
>>>>>>> 5d3406053c542bf510b1cc3cdac3842dab10b29a

        return userService.getUserByEmail(email).orElse(null);
    }

    /**
     * Add new user object.
     *
     * @param userDTO User object to add.
     * @return A user that was added.
     */
    @PostMapping
    public User createNewUser(@Valid @RequestBody UserDTO userDTO) {

        return userService.createNewUser(userDTO);
    }


    /**
     * Updates a user.
     *
     * @param uuid    UUID of the user to update
     * @param userDTO Object with the fields to update
     * @return Full user with the updated fields
     */
    @PutMapping("/{uuid}")
    public User updateUserById(@Valid @PathVariable UUID uuid,
                               @Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(uuid, userDTO);
    }

    /**
     * Deletes a user.
     *
     * @param uuid UUID of the user to delete
     * @return Full deleted user
     */
    @DeleteMapping("/{uuid}")
    public User deleteUserById(@Valid @PathVariable UUID uuid) {

        return userService.deleteUser(uuid);
    }
}