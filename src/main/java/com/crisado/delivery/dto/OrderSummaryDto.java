package com.crisado.delivery.dto;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {

    private long id;
    private String customerName;
    private String customerPhone;
    private boolean delivered;
    private String deliveryAddress;
    private ZonedDateTime deliveryDate;
    private double totalPrice;
    private int totalItems;
    private String deliveryRiderName;
    private boolean paid;
}
