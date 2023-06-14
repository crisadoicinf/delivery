package com.crisado.delivery.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class TransferencePaymentTest {
    
    @Test
    void getRecipientNameWhenBankAccountIsNull() {
        var payment = new TransferencePayment();

        assertThat(payment.getRecipientName())
                .isNull();
    }

    @Test
    void getRecipientName() {
        var payment = TransferencePayment.builder()
                .bankAccount(BankAccount.builder()
                        .owner("owner1")
                        .name("bank1")
                        .build())
                .build();

        assertThat(payment.getRecipientName())
                .isEqualTo("owner1");
    }

    @Test
    void getRecipientTypeWhenBankAccountIsNull() {
        var payment = new TransferencePayment();

        assertThat(payment.getRecipientType())
                .isEqualTo("transference - Unknown");
    }

    @Test
    void getRecipientType() {
        var payment = TransferencePayment.builder()
                .bankAccount(BankAccount.builder()
                        .owner("owner1")
                        .name("bank1")
                        .build())
                .build();


        assertThat(payment.getRecipientType())
                .isEqualTo("transference - bank1");
    }
    
}
