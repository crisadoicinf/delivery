package com.crisado.delivery.dto;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private Long id;
    private String customerName;
    private String customerPhone;
    private String note;
    private String deliveryAddress;
    private ZonedDateTime deliveryDate;
    private ZonedDateTime deliveryDateRange;
    private Integer deliveryRiderId;
    private double deliveryPrice;
    private double discount;
    private List<OrderItem> items;

    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private Long id;
        private Integer productId;
        private int quantity;
        private String note;
    }
}