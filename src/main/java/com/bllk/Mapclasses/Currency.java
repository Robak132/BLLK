package com.bllk.Mapclasses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CURRENCIES")
public class Currency {
    @Id
    @Column(name = "CURRENCY_ID")
    Integer id;
    @Column(name = "NAME")
    String name;
    @Column(name = "SHORTCUT")
    String shortcut;
    @Column(name = "SYMBOL")
    String symbol;
    @Column(name = "VALUE_IN_USD")
    Double valueUSD;

    public Currency(Integer id, String name, String shortcut, String symbol, Double valueUSD) {
        this.id = id;
        this.name = name;
        this.shortcut = shortcut;
        this.symbol = symbol;
        this.valueUSD = valueUSD;
    }
    public Currency() {}

    public Integer getID() {
        return id;
    }
    public void setID(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getShortcut() {
        return shortcut;
    }
    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public Double getValueUSD() {
        return valueUSD;
    }
    public void setValueUSD(Double valueUSD) {
        this.valueUSD = valueUSD;
    }
}
