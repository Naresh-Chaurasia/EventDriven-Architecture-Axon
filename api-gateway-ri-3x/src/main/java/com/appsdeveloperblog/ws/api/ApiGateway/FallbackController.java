package com.appsdeveloperblog.ws.api.ApiGateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    
    @GetMapping("/fallback/authorization")
    public ResponseEntity<String> authorizationFallback() {
        return ResponseEntity.status(503)
            .body("Authorization service temporarily unavailable");
    }
}