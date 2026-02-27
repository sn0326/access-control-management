package com.sn0326.access_control_management.domain.action;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "actions")
@Getter
@Setter
@NoArgsConstructor
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** READ / WRITE / DELETE / EXECUTE */
    @Column(nullable = false, unique = true, length = 100)
    private String name;
}
