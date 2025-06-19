package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity(name= "users")
public class CustomUser {
    @Id
    @GeneratedValue
    private Long userId;
    private String firstName;
    private String infix;
    private String lastName;
    private String address;
    private Integer houseNumber;
    private String postcode;
    private String dateOfBirth;
    private String phoneNumber;
    private String email;
    private String password;

    private String role;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Order> orders;

    @OneToMany(mappedBy = "linkedUser")
    @JsonManagedReference
    private List<Giftcard> giftcards;

    public CustomUser() {}

    public CustomUser(String firstName, String infix, String lastName, String address, Integer houseNumber, String postcode,
                      String dateOfBirth, String phoneNumber, String email, String password
                ) {
        this.firstName = firstName;
        this.infix = infix;
        this.lastName = lastName;
        this.address = address;
        this.houseNumber = houseNumber;
        this.postcode = postcode;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getInfix() {
        return infix;
    }

    public void setInfix(String infix) {
        this.infix = infix;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Giftcard> getGiftcards() {
        return giftcards;
    }

    public void setGiftcards(List<Giftcard> giftcards) {
        this.giftcards = giftcards;
    }
}
