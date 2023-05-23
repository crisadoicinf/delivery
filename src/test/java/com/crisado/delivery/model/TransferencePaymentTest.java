package com.crisado.delivery.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class TransferencePaymentTest {
    
    @Test
    public void getRecipientNameWhenBankAccountIsNull() {
        var payment = new TransferencePayment();

        assertThat(payment.getRecipientName())
                .isNull();
    }

    @Test
    public void getRecipientName() {
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
    public void getRecipientTypeWhenBankAccountIsNull() {
        var payment = new TransferencePayment();

        assertThat(payment.getRecipientType())
                .isEqualTo("transference - Unknown");
    }

    @Test
    public void getRecipientType() {
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
