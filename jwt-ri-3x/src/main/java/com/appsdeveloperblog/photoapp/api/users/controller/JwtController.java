package com.appsdeveloperblog.photoapp.api.users.controller;

import com.appsdeveloperblog.photoapp.api.users.model.JwtRequest;
import com.appsdeveloperblog.photoapp.api.users.model.JwtResponse;
import com.appsdeveloperblog.photoapp.api.users.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/generate")
    public ResponseEntity<JwtResponse> generateToken(@RequestBody JwtRequest request) {
        String token = jwtService.generateToken(request.getUsername(), request.getUserId());
        System.out.println("Generated token: " + token);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        boolean isValid = jwtService.validateToken(token);
        return ResponseEntity.ok(isValid ? "Token is valid" : "Token is invalid");
    }
}
