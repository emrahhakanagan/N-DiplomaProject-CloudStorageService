package com.agan.cloudstorage.model;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String email;
    private String password;
}