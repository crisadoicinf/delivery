package com.crisado.delivery.model;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "rider")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rider {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "rider_seq_gen")
    @SequenceGenerator(name = "rider_seq_gen", sequenceName = "rider_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "name")
    private String name;

}
