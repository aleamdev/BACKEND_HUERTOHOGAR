package com.eva3.huertohogar.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarritoResponse {

    private Long id;
    private String estado; 
    private List<CarritoItemResponse> items;
    private Integer total;
}
