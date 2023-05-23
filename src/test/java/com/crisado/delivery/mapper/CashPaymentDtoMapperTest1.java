package com.crisado.delivery.mapper;

import com.crisado.delivery.config.MapperConfig;
import com.crisado.delivery.dto.CashPaymentDto;
import com.crisado.delivery.model.CashPayment;
import com.crisado.delivery.model.Rider;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CashPaymentDtoMapperTest1 {

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

        assertThat(mapper.map(cashPayment, CashPaymentDto.class))
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
    void mapBankAccountWithNoRider() {
        var date = ZonedDateTime.now();
        var cashPayment = CashPayment.builder()
                .id(1L)
                .amount(10D)
                .date(date)
                .build();

        assertThat(mapper.map(cashPayment, CashPaymentDto.class))
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
                        null
                );
    }

}
