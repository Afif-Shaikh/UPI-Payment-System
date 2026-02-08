package com.project.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "id_sequences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdSequence {

    @Id
    @Column(name = "sequence_name", length = 50)
    private String sequenceName;

    @Column(name = "current_value", nullable = false)
    private Long currentValue;

    @Version
    private Long version;
}