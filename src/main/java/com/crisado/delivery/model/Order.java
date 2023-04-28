package com.crisado.delivery.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Set;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "order_seq_gen")
    @SequenceGenerator(name = "order_seq_gen", sequenceName = "order_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "date_created")
    private ZonedDateTime date;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "discount")
    private double discount;

    @Column(name = "note")
    private String note;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position asc")
    private Set<OrderItem> items;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private OrderDelivery delivery;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "order_payment", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "payment_id"))
    @OrderBy("date asc")
    private Set<Payment> payments;

    public boolean isPaid() {
        double totalPrice = getTotalPrice();
        if (totalPrice == 0 || payments == null || payments.isEmpty()) {
            return false;
        }
        double amountPaid = payments.stream()
                .map(Payment::getAmount)
                .reduce(0d, Double::sum);
        return amountPaid >= totalPrice;
    }

    public double getTotalPrice() {
        double itemsPrice = getItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(0d, Double::sum);
        double deliveryPrice = getDelivery().getPrice();
        return itemsPrice + deliveryPrice - getDiscount();
    }

}
