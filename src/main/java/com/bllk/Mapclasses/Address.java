package com.bllk.Mapclasses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name ="ADDRESSES")
public class Address {
    @Id
    @Column(name = "ADDRESS_ID")
    Integer id;
    @Column(name = "STREET")
    String street;
    @Column(name = "NUM")
    String num;
    @Column(name = "CITY")
    String city;
    @Column(name = "POSTCODE")
    String postcode;
    @Column(name = "COUNTRY_ID")
    String countryid;

    public Address(Integer id, String street, String num, String city, String postcode, String countryid) {
        this.id = id;
        this.street = street;
        this.num = num;
        this.city = city;
        this.postcode = postcode;
        this.countryid = countryid;
    }
    public Address() {

    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getNum() {
        return num;
    }
    public void setNum(String num) {
        this.num = num;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getPostcode() {
        return postcode;
    }
    public void setPostcode(String postal_code) {
        this.postcode = postal_code;
    }
    public String getCountryID() {
        return countryid;
    }
    public void setCountryID(String countryid) {
        this.countryid = countryid;
    }
}
