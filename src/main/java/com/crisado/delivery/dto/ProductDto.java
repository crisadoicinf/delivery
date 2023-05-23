package com.crisado.delivery.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private int id;
    private String name;
    private Double price;
}
