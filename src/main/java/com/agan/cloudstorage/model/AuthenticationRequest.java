package com.agan.cloudstorage.model;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String login;
    private String password;
}