package com.crisado.delivery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "cash_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CashPayment extends Payment {

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @Override
    public String getRecipientName() {
        if (rider == null) {
            return null;
        }
        return rider.getName();
    }

    @Override
    public String getRecipientType() {
        return "cash";
    }

}
