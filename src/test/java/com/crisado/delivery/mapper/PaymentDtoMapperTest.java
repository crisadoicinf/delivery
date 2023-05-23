package com.crisado.delivery.mapper;

import com.crisado.delivery.config.MapperConfig;
import com.crisado.delivery.dto.CashPaymentDto;
import com.crisado.delivery.dto.PaymentDto;
import com.crisado.delivery.dto.TransferencePaymentDto;
import com.crisado.delivery.model.BankAccount;
import com.crisado.delivery.model.CashPayment;
import com.crisado.delivery.model.Rider;
import com.crisado.delivery.model.TransferencePayment;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.assertj.core.api.InstanceOfAssertFactories;

@ExtendWith(MockitoExtension.class)
public class PaymentDtoMapperTest {

    private final ModelMapper mapper = new MapperConfig().getModelMapper();

    @Test
    public void mapCashPayment() {
        var date = ZonedDateTime.now();
        var cashPayment = CashPayment.builder()
                .id(1L)
                .amount(10D)
                .date(date)
                .rider(Rider.builder()
                        .name("rider1")
                        .build())
                .build();

        assertThat(mapper.map(cashPayment, PaymentDto.class))
                .isInstanceOf(CashPaymentDto.class)
                .asInstanceOf(InstanceOfAssertFactories.type(CashPaymentDto.class))
                .extracting(
                        CashPaymentDto::getId,
                        CashPaymentDto::getAmount,
                        CashPaymentDto::getDate,
                        CashPaymentDto::getRecipientType,
                        CashPaymentDto::getRecipientName
                )
                .containsExactly(
                        1L,
                        10D,
                        date,
                        "cash",
                        "rider1"
                );
    }

    @Test
    public void mapTransferencePayment() {
        var date = ZonedDateTime.now();
        var transferencePayment = TransferencePayment.builder()
                .id(1L)
                .amount(10D)
                .date(date)
                .bankAccount(BankAccount.builder()
                        .name("bank1")
                        .owner("owner1")
                        .build())
                .build();

        assertThat(mapper.map(transferencePayment, PaymentDto.class))
                .isInstanceOf(TransferencePaymentDto.class)
                .asInstanceOf(InstanceOfAssertFactories.type(TransferencePaymentDto.class))
                .extracting(
                        TransferencePaymentDto::getId,
                        TransferencePaymentDto::getAmount,
                        TransferencePaymentDto::getDate,
                        TransferencePaymentDto::getRecipientType,
                        TransferencePaymentDto::getRecipientName
                )
                .containsExactly(
                        1L,
                        10D,
                        date,
                        "transference - bank1",
                        "owner1"
                );
    }


}
