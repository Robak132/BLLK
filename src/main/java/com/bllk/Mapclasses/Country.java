package com.bllk.Mapclasses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="COUNTRIES")
public class Country {
    @Id
    @Column(name = "COUNTRY_ID")
    Integer id;
    @Column(name = "NAME")
    String name;
    @Column(name = "CURRENCY_ID")
    Integer currency_id;

    public Country(Integer id, String name, Integer currency_id) {
        this.id = id;
        this.name = name;
        this.currency_id = currency_id;
    }
    public Country() {}

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getCurrency_id() {
        return currency_id;
    }
    public void setCurrency_id(Integer currency_id) {
        this.currency_id = currency_id;
    }
}
