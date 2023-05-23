package com.crisado.delivery.model;

import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {

    @Test
    public void getTotalItemsZeroWhenNoItems() {
        var order = new Order();

        assertThat(order.getTotalItems())
                .isEqualTo(0);
    }

    @Test
    public void getTotalItems() {
        var order = Order.builder()
                .items(Set.of(
                        OrderItem.builder().quantity(1).build(),
                        OrderItem.builder().quantity(2).build()
                ))
                .build();

        assertThat(order.getTotalItems())
                .isEqualTo(3);
    }

    @Test
    public void getItemsTotalPriceZeroWhenNoItems() {
        var order = new Order();

        assertThat(order.getItemsTotalPrice())
                .isEqualTo(0D);
    }

    @Test
    public void getItemsTotalPrice() {
        var order = Order.builder()
                .items(Set.of(
                        OrderItem.builder().totalPrice(1D).build(),
                        OrderItem.builder().totalPrice(2D).build()
                ))
                .build();

        assertThat(order.getItemsTotalPrice())
                .isEqualTo(3D);
    }

    @Test
    public void getPaymentsTotalAmountZeroWhenNoPayments() {
        var order = new Order();

        assertThat(order.getPaymentsTotalAmount())
                .isEqualTo(0D);
    }

    @Test
    public void getPaymentsTotalAmountWhenNoPayments() {
        var order = Order.builder()
                .payments(Set.of(
                        CashPayment.builder().amount(10D).build(),
                        TransferencePayment.builder().amount(5D).build()
                ))
                .build();

        assertThat(order.getPaymentsTotalAmount())
                .isEqualTo(15D);
    }

    @Test
    public void getTotalPriceZeroWhenDiscountBiggerThanItemsAndDeliveryPrice() {
        var order = Order.builder()
                .discount(10D)
                .items(Set.of(
                        OrderItem.builder().totalPrice(1D).build(),
                        OrderItem.builder().totalPrice(2D).build()
                ))
                .delivery(OrderDelivery.builder()
                        .price(1.5D)
                        .build())
                .build();

        assertThat(order.getTotalPrice())
                .isEqualTo(0D);
    }

    @Test
    public void getTotalPriceWhenNoDelivery() {
        var order = Order.builder()
                .discount(2D)
                .items(Set.of(
                        OrderItem.builder().totalPrice(10D).build(),
                        OrderItem.builder().totalPrice(20D).build()
                ))
                .build();

        assertThat(order.getTotalPrice())
                .isEqualTo(28D);
    }

    @Test
    public void getTotalPrice() {
        var order = Order.builder()
                .discount(2D)
                .items(Set.of(
                        OrderItem.builder().totalPrice(10D).build(),
                        OrderItem.builder().totalPrice(20D).build()
                ))
                .delivery(OrderDelivery.builder()
                        .price(1.5D)
                        .build())
                .build();

        assertThat(order.getTotalPrice())
                .isEqualTo(29.5D);
    }

    @Test
    public void isDeliveredFalseWhenNoDelivery() {
        var order = new Order();

        assertThat(order.isDelivered())
                .isFalse();
    }

    @Test
    public void isDeliveredFalseWhenIsUndelivered() {
        var order = Order.builder()
                .delivery(OrderDelivery.builder()
                        .delivered(false)
                        .build())
                .build();

        assertThat(order.isDelivered())
                .isFalse();
    }

    @Test
    public void isDeliveredFalseWhenIsDelivered() {
        var order = Order.builder()
                .delivery(OrderDelivery.builder()
                        .delivered(true)
                        .build())
                .build();

        assertThat(order.isDelivered())
                .isTrue();
    }

    @Test
    public void isPaidFalseWhenPaymentsAreLessThanTotalPrice() {
        var order = Order.builder()
                .discount(2D)
                .items(Set.of(
                        OrderItem.builder().totalPrice(10D).build(),
                        OrderItem.builder().totalPrice(20D).build()
                ))
                .delivery(OrderDelivery.builder()
                        .price(1.5D)
                        .build())
                .payments(Set.of(
                        CashPayment.builder().amount(5D).build(),
                        TransferencePayment.builder().amount(5D).build()
                ))
                .build();

        assertThat(order.isPaid())
                .isFalse();
    }

    @Test
    public void isPaidTrueWhenPaymentsMatchTotalPrice() {
        var order = Order.builder()
                .discount(2D)
                .items(Set.of(
                        OrderItem.builder().totalPrice(10D).build(),
                        OrderItem.builder().totalPrice(20D).build()
                ))
                .delivery(OrderDelivery.builder()
                        .price(1.5D)
                        .build())
                .payments(Set.of(
                        CashPayment.builder().amount(20D).build(),
                        TransferencePayment.builder().amount(9.5D).build()
                ))
                .build();

        assertThat(order.isPaid())
                .isTrue();
    }

}
