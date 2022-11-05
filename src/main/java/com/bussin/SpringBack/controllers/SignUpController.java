package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.models.user.SignUpUniqueRequest;
import com.bussin.SpringBack.models.user.SignUpUniqueResponse;
import com.bussin.SpringBack.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/unique")
public class SignUpController {
    private final UserService userService;

    @Autowired
    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Checks if a set of credentials can be used to create a unique user.
     * @param request The set of credentials to check
     * @return Whether the credentials can be used to create a unique user
     */
    @Operation(summary = "Checks if a set of credentials can be used to create a unique user")
    @PostMapping
    public SignUpUniqueResponse isUniqueCheck(@RequestBody SignUpUniqueRequest request) {
        return userService.isUniqueCheck(request);
    }
}
