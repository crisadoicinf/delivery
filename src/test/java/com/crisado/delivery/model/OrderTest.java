package com.crisado.delivery.model;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OrderTest {

    @Test
    public void getItemsPriceZeroWhenNoItems() {
        var order = new Order();

        assertThat(order.getItemsPrice())
                .isEqualTo(0D);
    }

    @Test
    public void getItemsPrice() {
        var order = Order.builder()
                .items(Set.of(
                        OrderItem.builder().totalPrice(1D).build(),
                        OrderItem.builder().totalPrice(2D).build()
                ))
                .build();

        assertThat(order.getItemsPrice())
                .isEqualTo(3D);
    }

    @Test
    public void getDeliveryPriceZeroWhenNoDelivery() {
        var order = new Order();

        assertThat(order.getDeliveryPrice())
                .isEqualTo(0D);
    }

    @Test
    public void getDeliveryPrice() {
        var order = Order.builder()
                .delivery(OrderDelivery.builder()
                        .price(1.5D)
                        .build())
                .build();

        assertThat(order.getDeliveryPrice())
                .isEqualTo(1.5D);
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
    public void isPaidFalseWhenNoPayments() {
        var order = new Order();

        assertThat(order.isPaid())
                .isFalse();

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
    public void isPaidFalseWhenPaymentsMatchTotalPrice() {
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
