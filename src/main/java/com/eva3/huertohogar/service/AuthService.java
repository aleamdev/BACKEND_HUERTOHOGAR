package com.eva3.huertohogar.service;

import com.eva3.huertohogar.dto.AuthResponse;
import com.eva3.huertohogar.dto.LoginRequest;
import com.eva3.huertohogar.dto.RegisterRequest;
import com.eva3.huertohogar.entity.Rol;
import com.eva3.huertohogar.entity.Usuario;
import com.eva3.huertohogar.repository.RolRepository;
import com.eva3.huertohogar.repository.UsuarioRepository;
import com.eva3.huertohogar.security.CustomUserDetails;
import com.eva3.huertohogar.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_CLIENTE no existe"));

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoles(Collections.singleton(rolCliente));

        usuarioRepository.save(usuario);

        CustomUserDetails userDetails = new CustomUserDetails(usuario);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, "Bearer", usuario.getEmail(), rolCliente.getNombre());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        String rol = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("");

        return new AuthResponse(token, "Bearer", userDetails.getUsername(), rol);
    }

    public AuthResponse refreshToken(String token) {
        // token viene sin "Bearer ", es solo el string JWT
        String email = jwtService.extractUsername(token);

        // Cargar el usuario para regenerar el token
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CustomUserDetails userDetails = new CustomUserDetails(usuario);

        // Validar que el token sea válido antes de refrescar
        if (!jwtService.isTokenValid(token, userDetails)) {
            throw new RuntimeException("Token inválido o expirado");
        }

        String nuevoToken = jwtService.generateToken(userDetails);

        String rol = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("");

        return new AuthResponse(nuevoToken, "Bearer", userDetails.getUsername(), rol);
    }
}
