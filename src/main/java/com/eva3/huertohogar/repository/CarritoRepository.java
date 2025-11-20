package com.eva3.huertohogar.repository;

import com.eva3.huertohogar.entity.Carrito;
import com.eva3.huertohogar.entity.EstadoCarrito;
import com.eva3.huertohogar.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioAndEstado(Usuario usuario, EstadoCarrito estado);
}
