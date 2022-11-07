package com.bussin.SpringBack.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    /**
     * Check if backend service is reachable
     *
     * @return 200 HTTPStatus.OK
     */
    @GetMapping
    @Operation(summary = "Health check")
    public ResponseEntity<Void> getHealthCheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
