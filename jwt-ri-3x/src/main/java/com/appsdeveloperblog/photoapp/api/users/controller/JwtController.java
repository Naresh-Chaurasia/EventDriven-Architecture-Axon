package com.appsdeveloperblog.photoapp.api.users.controller;

import com.appsdeveloperblog.photoapp.api.users.model.JwtRequest;
import com.appsdeveloperblog.photoapp.api.users.model.JwtResponse;
import com.appsdeveloperblog.photoapp.api.users.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {

    private static final Logger logger = LoggerFactory.getLogger(JwtController.class);

    @Autowired
    private JwtService jwtService;

    @PostMapping("/generate")
    public ResponseEntity<JwtResponse> generateToken(@RequestBody JwtRequest request) {
        logger.info("Received token generation request for username: {}, userId: {}", request.getUsername(), request.getUserId());
        String token = jwtService.generateToken(request.getUsername(), request.getUserId());
        logger.info("Generated token: {}", token);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        logger.info("Received token validation request");
        boolean isValid = jwtService.validateToken(token);
        String response = isValid ? "Token is valid" : "Token is invalid";
        logger.info("Token validation result: {}", response);
        return ResponseEntity.ok(response);
    }
}
