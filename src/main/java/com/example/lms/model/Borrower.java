package com.example.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "borrowers")
public class Borrower {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

//    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<BorrowingTransaction> transactions = new HashSet<>();

    public Borrower(String name, String email, String phoneNumber){
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
