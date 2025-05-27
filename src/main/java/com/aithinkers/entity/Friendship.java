package com.aithinkers.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Table(name = "friendship")
@Data
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private RegisterUser requester;

    @ManyToOne
    @JoinColumn(name = "addressee_id")
    private RegisterUser addressee;
}
