package com.crisado.delivery.model;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "bank_account_seq_gen")
    @SequenceGenerator(name = "bank_account_seq_gen", sequenceName = "bank_account_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "owner")
    private String owner;

    @Column(name = "name")
    private String name;

}
