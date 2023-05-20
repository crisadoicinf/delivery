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
@Table(name = "transference_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TransferencePayment extends Payment {

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @Override
    public String getRecipientName() {
        return bankAccount.getOwner();
    }

    @Override
    public String getRecipientType() {
        return "transference - " + bankAccount.getName();
    }
}
