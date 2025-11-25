package com.eva3.huertohogar.repository;

import com.eva3.huertohogar.entity.Carrito;
import com.eva3.huertohogar.entity.CarritoItem;
import com.eva3.huertohogar.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    Optional<CarritoItem> findByCarritoAndProducto(Carrito carrito, Producto producto);
}
