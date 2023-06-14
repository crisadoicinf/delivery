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
public class OrderDto {

    private long id;
    private String customerName;
    private String customerPhone;
    private String note;
    private String deliveryAddress;
    private ZonedDateTime deliveryDate;
    private ZonedDateTime deliveryDateRange;
    private Integer deliveryRiderId;
    private double deliveryPrice;
    private boolean delivered;
    private boolean paid;
    private double discount;
    private double itemsTotalPrice;
    private double paymentsTotalAmount;
    private double totalPrice;
    private List<OrderItem> items;

    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {

        private long id;
        private int productId;
        private int position;
        private int quantity;
        private double unitPrice;
        private double totalPrice;
        private String note;
    }
}
