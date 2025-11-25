    package com.eva3.huertohogar.controller;

    import com.eva3.huertohogar.dto.AuthResponse;
    import com.eva3.huertohogar.dto.LoginRequest;
    import com.eva3.huertohogar.dto.RegisterRequest;
    import com.eva3.huertohogar.dto.RefreshTokenRequest;   
    import com.eva3.huertohogar.entity.Usuario;
    import com.eva3.huertohogar.repository.UsuarioRepository;
    import com.eva3.huertohogar.service.AuthService;
    import jakarta.validation.Valid;                        
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/auth")
    @RequiredArgsConstructor
    @CrossOrigin(origins = "*") 
    public class AuthController {

        private final AuthService authService;
        private final UsuarioRepository usuarioRepository;

        @PostMapping("/register")
        public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
            return authService.register(request);
        }

        @PostMapping("/login")
        public AuthResponse login(@Valid @RequestBody LoginRequest request) {
            return authService.login(request);
        }

        @GetMapping("/me")
        public Usuario me(@AuthenticationPrincipal UserDetails userDetails) {
            return usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }

        @PostMapping("/refresh")
        public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
            return authService.refreshToken(request.getToken());
        }

    }
