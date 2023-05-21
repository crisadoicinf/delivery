package com.crisado.delivery.model;

import static jakarta.persistence.GenerationType.SEQUENCE;

import java.time.ZonedDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public double getItemsPrice() {
        double itemsPrice = 0;
        if (items != null) {
            itemsPrice = items
                    .stream()
                    .map(OrderItem::getTotalPrice)
                    .reduce(0d, Double::sum);
        }
        return itemsPrice;
    }

    public double getDeliveryPrice() {
        double deliveryPrice = 0;
        if (delivery != null) {
            deliveryPrice = delivery.getPrice();
        }
        return deliveryPrice;
    }

    public double getTotalPrice() {
        double itemsPrice = getItemsPrice();
        double deliveryPrice = getDeliveryPrice();
        return Math.max(0D, itemsPrice + deliveryPrice - getDiscount());
    }

    public boolean isDelivered() {
        if (delivery == null) {
            return false;
        }
        return delivery.isDelivered();
    }

    public boolean isPaid() {
        if (payments == null) {
            return false;
        }
        double totalPrice = getTotalPrice();
        double amountPaid = payments
                .stream()
                .map(Payment::getAmount)
                .reduce(0d, Double::sum);
        return amountPaid >= totalPrice;
    }

}
