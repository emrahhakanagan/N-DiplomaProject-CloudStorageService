package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.AuthenticationRequest;
import com.agan.cloudstorage.model.AuthenticationResponse;
import com.agan.cloudstorage.service.MyUserDetailsService;
import com.agan.cloudstorage.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
            throws Exception {

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getLogin());

        logger.info("Stored password hash for user {}: {}", authenticationRequest.getLogin(), userDetails.getPassword());

        boolean passwordMatches = passwordEncoder.matches(authenticationRequest.getPassword(), userDetails.getPassword());

        if (passwordMatches) {
            logger.info("Password matches for user: {}", authenticationRequest.getLogin());
        } else {
            logger.error("Password does not match for user: {}", authenticationRequest.getLogin());
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getLogin(),
                            authenticationRequest.getPassword())
            );
            logger.info("Authentication successful for user: {}", authenticationRequest.getLogin());
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user: {}", authenticationRequest.getLogin());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        logger.info("Stored password hash for user {}: {}", authenticationRequest.getLogin(), userDetails.getPassword());

        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        logger.info("JWT created for user: {}", authenticationRequest.getLogin());

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return ResponseEntity.ok("Successfully logged out");
    }

}