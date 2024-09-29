package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.AuthenticationRequest;
import com.agan.cloudstorage.model.AuthenticationResponse;
import com.agan.cloudstorage.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class AuthController {
    /*
     * Getting the auth-token from the header is required by the specification, but we do not use this variable directly.
     * The authentication process is automatically handled by Spring Security through JwtRequestFilter.
     *
     * authToken The authentication token provided in the request header.
     */

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {

        String jwt = authenticationService.authenticate(
                authenticationRequest.getLogin(), authenticationRequest.getPassword()
        );

        return ResponseEntity.ok()
                .header("auth-token", jwt)
                .body(new AuthenticationResponse(jwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "auth-token", required = true) String authToken) {

        return ResponseEntity.ok("Successfully logged out");
    }
}