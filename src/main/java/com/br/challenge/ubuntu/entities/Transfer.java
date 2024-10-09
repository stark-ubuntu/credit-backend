package com.br.challenge.ubuntu.entities;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transfer", schema = "credit")
public class Transfer extends PanacheEntityBase {

    @Id
    private UUID id;
    private String taxId;
    private Number amount;
    private String status;

}