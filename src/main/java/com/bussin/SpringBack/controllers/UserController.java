package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bussin.SpringBack.repositories.UserRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {
    private UserRepository users;

    @Autowired
    public UserController(UserRepository users) {
        this.users = users;
    }

    /**
     * Gets all users.
     * 
     * @return List of all users.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return users.findAll();
    }

    /**
     * Add new user object.
     * 
     * @param user User object to add.
     * @return A user that was added.
     */
    @PostMapping
    public User addNewUser(@Valid @RequestBody User user) {
        return users.save(user);
    }
}
