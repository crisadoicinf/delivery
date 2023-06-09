package com.crisado.delivery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Optional;
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
        if (bankAccount == null) {
            return null;
        }
        return bankAccount.getOwner();
    }

    @Override
    public String getRecipientType() {
        String bankName = "Unknown";
        if (bankAccount != null && bankAccount.getName() != null) {
            bankName = bankAccount.getName();
        }
        return "transference - " + bankName;
    }
}
