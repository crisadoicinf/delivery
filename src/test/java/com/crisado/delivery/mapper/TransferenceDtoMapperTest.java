package com.crisado.delivery.mapper;

import com.crisado.delivery.config.MapperConfig;
import com.crisado.delivery.dto.TransferencePaymentDto;
import com.crisado.delivery.model.BankAccount;
import com.crisado.delivery.model.TransferencePayment;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class TransferenceDtoMapperTest {

    private final ModelMapper mapper = new MapperConfig().getModelMapper();


    @Test
    void mapTransferencePayment() {
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

        assertThat(mapper.map(transferencePayment, TransferencePaymentDto.class))
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
