package com.eva3.huertohogar.config;

import com.eva3.huertohogar.entity.Rol;
import com.eva3.huertohogar.entity.Usuario;
import com.eva3.huertohogar.repository.RolRepository;
import com.eva3.huertohogar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
       
        Rol adminRole = rolRepository.findByNombre("ROLE_ADMIN")
                .orElseGet(() -> rolRepository.save(new Rol(null, "ROLE_ADMIN")));
        Rol clienteRole = rolRepository.findByNombre("ROLE_CLIENTE")
                .orElseGet(() -> rolRepository.save(new Rol(null, "ROLE_CLIENTE")));

        
        if (!usuarioRepository.existsByEmail("admin@huerto.cl")) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@huerto.cl");
            admin.setNombre("Administrador");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRoles(Collections.singleton(adminRole));
            usuarioRepository.save(admin);
        }
    }
}
