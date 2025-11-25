package com.eva3.huertohogar.repository;

import com.eva3.huertohogar.entity.Categoria;
import com.eva3.huertohogar.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Producto> findByCategoria(Categoria categoria);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
