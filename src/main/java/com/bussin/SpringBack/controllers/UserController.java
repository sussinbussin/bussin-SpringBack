package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.repositories.UserRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {
    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * GraphQL query to return all users.
     * @return An Iterable containing all users.
     */
    @QueryMapping
    public Iterable<User> allUsers() {
        return userRepository.findAll();
    }

    /**
     * GraphQL mutation to add a user.
     * @param user The User object to add
     * @return The added User object
     */
    @MutationMapping
    public User addUser(@Argument User user) {
        return userRepository.save(user);
    }
}
