package com.crisado.delivery.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class CashPaymentTest {

    @Test
    void getRecipientNameWhenRiderIsNull() {
        var payment = new CashPayment();

        assertThat(payment.getRecipientName())
                .isNull();
    }

    @Test
    void getRecipientName() {
        var payment = CashPayment.builder()
                .rider(Rider.builder().name("rider1").build())
                .build();

        assertThat(payment.getRecipientName())
                .isEqualTo("rider1");
    }

    @Test
    void getRecipientType() {
        var payment = new CashPayment();

        assertThat(payment.getRecipientType())
                .isEqualTo("cash");
    }

}
