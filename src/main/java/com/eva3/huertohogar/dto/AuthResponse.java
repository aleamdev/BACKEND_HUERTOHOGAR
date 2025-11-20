package com.eva3.huertohogar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tipo;   // "Bearer"
    private String email;
    private String rol;    // por simplicidad, primer rol
}
