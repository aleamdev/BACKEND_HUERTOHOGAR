package com.eva3.huertohogar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type;
    private Long id;        
    private String email;   
    private String nombre;  
    private List<String> roles; 
}