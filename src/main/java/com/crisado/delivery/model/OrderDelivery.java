package com.crisado.delivery.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "orders_delivery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDelivery {

    @Id
    @Column(name = "order_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "date")
    private ZonedDateTime date;

    @Column(name = "date_range")
    private ZonedDateTime dateRange;

    @Column(name = "address")
    private String address;

    @Column(name = "price")
    private double price;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @Column(name = "delivered")
    private boolean delivered;

}
