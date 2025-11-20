package com.eva3.huertohogar.controller;

import com.eva3.huertohogar.dto.ProductoRequest;
import com.eva3.huertohogar.dto.ProductoResponse;
import com.eva3.huertohogar.service.ProductoService;
import jakarta.validation.Valid;                 // <-- ESTE IMPORT
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    // Público: listado, con filtros opcionales (?nombre= / ?categoriaId=)
    @GetMapping
    public List<ProductoResponse> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId
    ) {
        return productoService.listarTodos(nombre, categoriaId);
    }

    // Público: detalle de un producto
    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }

    // Solo ADMIN crea
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
        return productoService.crear(request);
    }

    // Solo ADMIN actualiza
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    // Solo ADMIN elimina
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
