package com.luxuryproductsholding.api.dto;

public class AuthenticationDTO {
    public String firstName;
    public String infix;
    public String lastName;
    public String address;
    public Integer houseNumber;
    public String postcode;
    public String dateOfBirth;
    public String phoneNumber;
    public String userType;
    public String email;
    public String password;

    public AuthenticationDTO(String firstName, String infix, String lastName, String address, Integer houseNumber,
                             String postcode, String dateOfBirth, String phoneNumber, String userType, String email,
                             String password) {
        this.firstName = firstName;
        this.infix = infix;
        this.lastName = lastName;
        this.address = address;
        this.houseNumber = houseNumber;
        this.postcode = postcode;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.email = email;
        this.password = password;
    }
}
