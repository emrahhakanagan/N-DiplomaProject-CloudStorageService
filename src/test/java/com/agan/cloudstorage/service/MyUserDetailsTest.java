package com.agan.cloudstorage.service;

import com.agan.cloudstorage.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class MyUserDetailsTest {

    private User user;
    private MyUserDetails myUserDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRoles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
        myUserDetails = new MyUserDetails(user);
    }

    @Test
    @DisplayName("Should return correct authorities based on user roles")
    void shouldReturnCorrectAuthorities() {
        Collection<? extends GrantedAuthority> authorities = myUserDetails.getAuthorities();

        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Should return empty authorities when user has no roles")
    void shouldReturnEmptyAuthoritiesWhenUserHasNoRoles() {
        user.setRoles(Collections.emptyList());
        myUserDetails = new MyUserDetails(user);

        Collection<? extends GrantedAuthority> authorities = myUserDetails.getAuthorities();

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    @DisplayName("Should return authorities with roles without modifying them")
    void shouldReturnAuthoritiesWithUnmodifiedRoles() {
        user.setRoles(Arrays.asList("USER", "ADMIN"));
        myUserDetails = new MyUserDetails(user);

        Collection<? extends GrantedAuthority> authorities = myUserDetails.getAuthorities();

        assertTrue(authorities.contains(new SimpleGrantedAuthority("USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ADMIN")));
    }

    @Test
    @DisplayName("Should return correct password")
    void shouldReturnCorrectPassword() {
        String password = myUserDetails.getPassword();

        assertEquals("password123", password);
    }

    @Test
    @DisplayName("Should return empty string when user password is not set")
    void shouldReturnEmptyStringWhenUserPasswordIsNotSet() {
        user.setPassword("");
        myUserDetails = new MyUserDetails(user);

        String password = myUserDetails.getPassword();

        assertEquals("", password);
    }

    @Test
    @DisplayName("Should return correct username")
    void shouldReturnCorrectUsername() {
        String username = myUserDetails.getUsername();

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should return true for isAccountNonExpired")
    void shouldReturnTrueForIsAccountNonExpired() {
        assertTrue(myUserDetails.isAccountNonExpired());
    }

    @Test
    @DisplayName("Should return true for isAccountNonLocked")
    void shouldReturnTrueForIsAccountNonLocked() {
        assertTrue(myUserDetails.isAccountNonLocked());
    }

    @Test
    @DisplayName("Should return true for isCredentialsNonExpired")
    void shouldReturnTrueForIsCredentialsNonExpired() {
        assertTrue(myUserDetails.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Should return true for isEnabled")
    void shouldReturnTrueForIsEnabled() {
        assertTrue(myUserDetails.isEnabled());
    }
}