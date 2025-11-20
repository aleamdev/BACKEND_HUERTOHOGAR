package com.eva3.huertohogar.dto;

import lombok.Data;

@Data
public class CarritoItemResponse {

    private Long productoId;
    private String codigo;
    private String nombre;
    private String imagenUrl;

    private Integer precioUnitario;
    private Integer cantidad;
    private Integer subtotal; // precioUnitario * cantidad
}
