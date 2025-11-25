package com.eva3.huertohogar.controller;

import com.eva3.huertohogar.dto.CarritoItemRequest;
import com.eva3.huertohogar.dto.CarritoResponse;
import com.eva3.huertohogar.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/carrito")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CarritoController {

    private final CarritoService carritoService;

    
    @GetMapping
    public CarritoResponse verCarrito(@AuthenticationPrincipal UserDetails userDetails) {
        return carritoService.verCarrito(userDetails.getUsername());
    }

    
    @PostMapping("/items")
    public CarritoResponse agregarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CarritoItemRequest request
    ) {
        return carritoService.agregarItem(userDetails.getUsername(), request);
    }

    
    @PutMapping("/items")
    public CarritoResponse actualizarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CarritoItemRequest request
    ) {
        return carritoService.actualizarItem(userDetails.getUsername(), request);
    }

    
    @DeleteMapping("/items/{productoId}")
    public CarritoResponse eliminarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productoId
    ) {
        return carritoService.eliminarItem(userDetails.getUsername(), productoId);
    }

    
    @DeleteMapping
    public CarritoResponse vaciar(@AuthenticationPrincipal UserDetails userDetails) {
        return carritoService.vaciarCarrito(userDetails.getUsername());
    }

    
    @PostMapping("/checkout")
    public CarritoResponse checkout(@AuthenticationPrincipal UserDetails userDetails) {
        return carritoService.checkout(userDetails.getUsername());
    }
    @GetMapping("/historial")
    public List<CarritoResponse> historial(@AuthenticationPrincipal UserDetails userDetails) {
    return carritoService.historial(userDetails.getUsername());
}
}
