package com.example.hibernate.model;

import javax.persistence.*;

@Entity
@Table(name = "contact")
@Access(AccessType.FIELD)
public class Contact {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String phoneNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 50)
    private String type; // e.g., "Mobile", "Office", "Home"

    public Contact() {}

    public Contact(String phoneNumber, String email, String type) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

