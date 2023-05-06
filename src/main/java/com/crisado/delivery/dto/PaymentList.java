package com.crisado.delivery.dto;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = PaymentList.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CashPaymentList.class, name = "cash"),
        @JsonSubTypes.Type(value = TransferencePaymentList.class, name = "transference")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class PaymentList {

    private Long id;
    private Double amount;
    private ZonedDateTime date;
    private String recipientName;
    private String recipientType;

}
