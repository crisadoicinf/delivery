package com.crisado.delivery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static jakarta.persistence.InheritanceType.JOINED;

@Entity
@Table(name = "payment")
@Inheritance(strategy = JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Payment {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "payment_seq_gen")
    @SequenceGenerator(name = "payment_seq_gen", sequenceName = "payment_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "date")
    private ZonedDateTime date;

    @Column(name = "amount")
    private Double amount;

}
