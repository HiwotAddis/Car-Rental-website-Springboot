package com.example.carrentalsystem.dto;

import com.example.carrentalsystem.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private User user;
}
