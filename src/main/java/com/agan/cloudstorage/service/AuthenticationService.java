package com.agan.cloudstorage.service;

import com.agan.cloudstorage.exception.GeneralServiceException;
import com.agan.cloudstorage.exception.InvalidInputException;
import com.agan.cloudstorage.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String authenticate(String login, String password) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(login);

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password)
            );

            return jwtUtil.generateToken(userDetails);

        } catch (UsernameNotFoundException e) {
            throw new InvalidInputException("User not found");
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralServiceException("Error during authentication", e);
        }
    }
}
