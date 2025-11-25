package com.eva3.huertohogar.service;
import java.util.ArrayList;
import java.util.List;
import com.eva3.huertohogar.dto.CarritoItemRequest;
import com.eva3.huertohogar.dto.CarritoItemResponse;
import com.eva3.huertohogar.dto.CarritoResponse;
import com.eva3.huertohogar.entity.*;
import com.eva3.huertohogar.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;

    private Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional
    protected Carrito obtenerOCrearCarritoActivo(Usuario usuario) {
        return carritoRepository.findByUsuarioAndEstado(usuario, EstadoCarrito.ACTIVO)
                .orElseGet(() -> {
                    Carrito c = new Carrito();
                    c.setUsuario(usuario);
                    c.setEstado(EstadoCarrito.ACTIVO);
                    return carritoRepository.save(c);
                });
    }

    @Transactional(readOnly = true)
    public CarritoResponse verCarrito(String email) {
        Usuario usuario = obtenerUsuarioPorEmail(email);

        Carrito carrito = carritoRepository.findByUsuarioAndEstado(usuario, EstadoCarrito.ACTIVO)
                .orElse(null);

        if (carrito == null) {
            carrito = new Carrito();
            carrito.setUsuario(usuario);
            carrito.setEstado(EstadoCarrito.ACTIVO);
        }

        return toResponse(carrito);
    }

    @Transactional
    public CarritoResponse agregarItem(String email, CarritoItemRequest request) {
        Usuario usuario = obtenerUsuarioPorEmail(email);
        Carrito carrito = obtenerOCrearCarritoActivo(usuario);

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        CarritoItem item = carritoItemRepository.findByCarritoAndProducto(carrito, producto)
                .orElseGet(() -> {
                    CarritoItem nuevo = new CarritoItem();
                    nuevo.setCarrito(carrito);
                    nuevo.setProducto(producto);
                    nuevo.setCantidad(0);
                    nuevo.setPrecioUnitario(producto.getPrecio());
                    carrito.getItems().add(nuevo);
                    return nuevo;
                });

        item.setCantidad(item.getCantidad() + request.getCantidad());
        carritoItemRepository.save(item);

        return toResponse(carrito);
    }

    @Transactional
    public CarritoResponse actualizarItem(String email, CarritoItemRequest request) {
        Usuario usuario = obtenerUsuarioPorEmail(email);
        Carrito carrito = obtenerOCrearCarritoActivo(usuario);

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        CarritoItem item = carritoItemRepository.findByCarritoAndProducto(carrito, producto)
                .orElseThrow(() -> new RuntimeException("El producto no está en el carrito"));

        if (request.getCantidad() <= 0) {
            carrito.getItems().remove(item);
            carritoItemRepository.delete(item);
        } else {
            item.setCantidad(request.getCantidad());
            carritoItemRepository.save(item);
        }

        return toResponse(carrito);
    }

    @Transactional
    public CarritoResponse eliminarItem(String email, Long productoId) {
        Usuario usuario = obtenerUsuarioPorEmail(email);
        Carrito carrito = obtenerOCrearCarritoActivo(usuario);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        CarritoItem item = carritoItemRepository.findByCarritoAndProducto(carrito, producto)
                .orElseThrow(() -> new RuntimeException("El producto no está en el carrito"));

        carrito.getItems().remove(item);
        carritoItemRepository.delete(item);

        return toResponse(carrito);
    }

    @Transactional
    public CarritoResponse vaciarCarrito(String email) {
        Usuario usuario = obtenerUsuarioPorEmail(email);
        Carrito carrito = obtenerOCrearCarritoActivo(usuario);

        carritoItemRepository.deleteAll(carrito.getItems());
        carrito.getItems().clear();

        return toResponse(carrito);
    }

    @Transactional(readOnly = true)
    public List<CarritoResponse> historial(String email) {
        Usuario usuario = obtenerUsuarioPorEmail(email);

        return carritoRepository.findAllByUsuarioAndEstado(usuario, EstadoCarrito.COMPLETADO)
        .stream()
        .map(this::toResponse)
        .toList();
    }


    @Transactional
    public CarritoResponse checkout(String email) {
        Usuario usuario = obtenerUsuarioPorEmail(email);

        Carrito carrito = carritoRepository.findByUsuarioAndEstado(usuario, EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new RuntimeException("No hay carrito activo"));

        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("No puedes finalizar una compra vacía");
        }

   
        carrito.setEstado(EstadoCarrito.COMPLETADO);
        carritoRepository.save(carrito);

        
        Carrito nuevo = new Carrito();
        nuevo.setUsuario(usuario);
        nuevo.setEstado(EstadoCarrito.ACTIVO);
        carritoRepository.save(nuevo);

        return toResponse(carrito);
    }

    private CarritoResponse toResponse(Carrito carrito) {
        CarritoResponse res = new CarritoResponse();
        res.setId(carrito.getId());
        res.setEstado(carrito.getEstado().name());

        var items = carrito.getItems().stream()
                .map(ci -> {
                    CarritoItemResponse ir = new CarritoItemResponse();
                    ir.setProductoId(ci.getProducto().getId());
                    ir.setCodigo(ci.getProducto().getCodigo());
                    ir.setNombre(ci.getProducto().getNombre());
                    ir.setImagenUrl(ci.getProducto().getImagenUrl());
                    ir.setPrecioUnitario(ci.getPrecioUnitario());
                    ir.setCantidad(ci.getCantidad());
                    ir.setSubtotal(ci.getPrecioUnitario() * ci.getCantidad());
                    return ir;
                })
                .collect(Collectors.toList());

        res.setItems(items);
        int total = items.stream()
                .mapToInt(CarritoItemResponse::getSubtotal)
                .sum();
        res.setTotal(total);

        return res;
    }
}
