package com.crisado.delivery.dto;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = PaymentDto.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CashPaymentDto.class, name = "cash"),
        @JsonSubTypes.Type(value = TransferencePaymentDto.class, name = "transference")
})
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class PaymentDto {

    private Long id;
    private Double amount;
    private ZonedDateTime date;
    private String recipientName;
    private String recipientType;

}
