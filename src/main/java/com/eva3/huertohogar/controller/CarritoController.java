package com.eva3.huertohogar.controller;

import com.eva3.huertohogar.dto.CarritoItemRequest;
import com.eva3.huertohogar.dto.CarritoResponse;
import com.eva3.huertohogar.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrito")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CarritoController {

    private final CarritoService carritoService;

    // Ver carrito del usuario logueado
    @GetMapping
    public CarritoResponse verCarrito(@AuthenticationPrincipal UserDetails userDetails) {
        return carritoService.verCarrito(userDetails.getUsername());
    }

    // Agregar producto al carrito (suma cantidad)
    @PostMapping("/items")
    public CarritoResponse agregarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CarritoItemRequest request
    ) {
        return carritoService.agregarItem(userDetails.getUsername(), request);
    }

    // Actualizar cantidad de un producto en el carrito
    @PutMapping("/items")
    public CarritoResponse actualizarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CarritoItemRequest request
    ) {
        return carritoService.actualizarItem(userDetails.getUsername(), request);
    }

    // Eliminar un producto del carrito
    @DeleteMapping("/items/{productoId}")
    public CarritoResponse eliminarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productoId
    ) {
        return carritoService.eliminarItem(userDetails.getUsername(), productoId);
    }

    // (Opcional) Vaciar carrito entero
    @DeleteMapping
    public CarritoResponse vaciar(@AuthenticationPrincipal UserDetails userDetails) {
        return carritoService.vaciarCarrito(userDetails.getUsername());
    }

    // Checkout: marcar carrito como COMPLETADO
    @PostMapping("/checkout")
    public CarritoResponse checkout(@AuthenticationPrincipal UserDetails userDetails) {
        return carritoService.checkout(userDetails.getUsername());
    }
}
