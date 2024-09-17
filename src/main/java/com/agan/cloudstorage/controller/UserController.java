package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.User;
import com.agan.cloudstorage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.ok("User added successfully");
    }
}

