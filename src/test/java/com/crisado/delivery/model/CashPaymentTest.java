package com.crisado.delivery.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class CashPaymentTest {

    @Test
    public void getRecipientNameWhenRiderIsNull() {
        var payment = new CashPayment();

        assertThat(payment.getRecipientName())
                .isNull();
    }

    @Test
    public void getRecipientName() {
        var payment = CashPayment.builder()
                .rider(Rider.builder().name("rider1").build())
                .build();

        assertThat(payment.getRecipientName())
                .isEqualTo("rider1");
    }

    @Test
    public void getRecipientType() {
        var payment = new CashPayment();

        assertThat(payment.getRecipientType())
                .isEqualTo("cash");
    }

}
