package com.crisado.delivery.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductList {
    private long id;
    private String name;
    private String price;
}
