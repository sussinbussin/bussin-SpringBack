package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.services.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bussin.SpringBack.repositories.UserRepository;

import javax.validation.Valid;
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
     * Gets a user by their UUID.
     *
     * @param uuid The UUID
     * @return The user if found, else null
     */
    @GetMapping("/{uuid}")
    public User getUserById(@PathVariable UUID uuid) {
        return userService.getUserById(uuid).orElse(null);
    }

    /**
     * Add new user object.
     *
     * @param user User object to add.
     * @return A user that was added.
     */
    @PostMapping
    public User addNewUser(@Valid @RequestBody User user) {
        return userService.addNewUser(user);
    }

    @PutMapping("/{uuid}")
    public User getUserById(@PathVariable UUID uuid, @RequestBody User user) {
        return userService.updateUser(uuid, user);
    }

    @DeleteMapping("/{uuid}")
    public User deleteUserById(@PathVariable UUID uuid) {
        return userService.deleteUser(uuid);
    }
}
