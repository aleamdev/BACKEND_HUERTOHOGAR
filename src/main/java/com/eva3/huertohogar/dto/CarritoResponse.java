package com.eva3.huertohogar.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarritoResponse {

    private Long id;
    private String estado; // ACTIVO / COMPLETADO
    private List<CarritoItemResponse> items;
    private Integer total; // suma de subtotales
}
