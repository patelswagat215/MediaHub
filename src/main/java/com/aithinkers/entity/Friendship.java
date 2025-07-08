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

    @ManyToOne						//Many Friends with one user
    @JoinColumn(name = "requester_id") 
    private RegisterUser requester; //Foreign key

    @ManyToOne
    @JoinColumn(name = "addressee_id") //Many Friends with one user
    private RegisterUser addressee; //Foreign key
}
