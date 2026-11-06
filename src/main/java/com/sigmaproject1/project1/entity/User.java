package com.sigmaproject1.project1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sigmaproject1.project1.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    private User(String email, String passwordHash, Role role, Customer customer) {
        if(role == null){
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (role == Role.CLIENT && customer == null) {
            throw new IllegalArgumentException("Client account requires a customer");
        }

        if (role != Role.CLIENT && customer != null) {
            throw new IllegalArgumentException("Only client accounts can be linked to a customer");
        }
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.customer = customer;
    }
}
