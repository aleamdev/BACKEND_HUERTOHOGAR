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
import java.util.List;
import java.util.stream.Collectors;

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

        List<String> roles = usuario.getRoles()
                .stream()
                .map(Rol::getNombre)
                .collect(Collectors.toList());

        return new AuthResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                roles
        );
    }

    public AuthResponse login(LoginRequest request) {
        Usuario u = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        String token = jwtService.generateToken(userDetails);

        List<String> roles = usuario.getRoles()
                .stream()
                .map(Rol::getNombre)
                .collect(Collectors.toList());

        return new AuthResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                roles
        );
    }

    public AuthResponse refreshToken(String token) {
        String email = jwtService.extractUsername(token);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CustomUserDetails userDetails = new CustomUserDetails(usuario);

        if (!jwtService.isTokenValid(token, userDetails)) {
            throw new RuntimeException("Token inválido o expirado");
        }

        String nuevoToken = jwtService.generateToken(userDetails);

        List<String> roles = usuario.getRoles()
                .stream()
                .map(Rol::getNombre)
                .collect(Collectors.toList());

        return new AuthResponse(
                nuevoToken,
                "Bearer",
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                roles
        );
    }
}
