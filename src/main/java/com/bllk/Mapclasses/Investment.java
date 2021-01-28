package com.bllk.Mapclasses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "INVESTMENTS")
public class Investment {
    @Id
    @Column(name = "INVESTMENT_ID")
    Integer id;
    @Column(name = "OWNER_ID")
    Integer ownerid;
    @Column(name = "NAME")
    String name;
    @Column(name = "VALUE")
    Long value;
    @Column(name = "PROFIT_RATE")
    Double profit;
    @Column(name = "YEAR_PROFIT_RATE")
    Double yearprofit;
    @Column(name = "CAP_PERIOD")
    Integer capperiod;
    @Column(name = "DATE_CREATED")
    Date datecreated;
    @Column(name = "DATE_ENDED")
    Date dateend;
    @Column(name = "CURRENCY_ID")
    Integer currencyid;

    public Investment(Integer id, String name, Integer ownerid, Long value, Double profit, Double yearprofit, Integer capperiod, Date datecreated, Date dateend, Integer currencyid) {
        this.id = id;
        this.name = name;
        this.ownerid = ownerid;
        this.value = value;
        this.profit = profit;
        this.yearprofit = yearprofit;
        this.capperiod = capperiod;
        this.datecreated = datecreated;
        this.dateend = dateend;
        this.currencyid = currencyid;
    }
    public Investment(Integer id, String name, Integer ownerid, Long value, Double profit, Double yearprofit, Integer capperiod, Integer currencyid) {
        this.id = id;
        this.name = name;
        this.ownerid = ownerid;
        this.value = value;
        this.profit = profit;
        this.yearprofit = yearprofit;
        this.capperiod = capperiod;
        this.currencyid = currencyid;
    }
    public Investment() {}

    public Integer getID() {
        return id;
    }
    public void setID(Integer id) {
        this.id = id;
    }
    public Integer getOwnerID() {
        return ownerid;
    }
    public void setOwnerID(Integer ownerid) {
        this.ownerid = ownerid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getValue() {
        return value;
    }
    public void setValue(Long value) {
        this.value = value;
    }
    public Double getProfit() { return profit; }
    public void setProfit(Double profit) {
        this.profit = profit;
    }
    public Double getYearProfit() {
        return yearprofit;
    }
    public void setYearProfit(Double yearprofit) {
        this.yearprofit = yearprofit;
    }
    public Integer getCapPeriod() { return capperiod; }
    public void setCapPeriod(Integer capperiod) { this.capperiod = capperiod; }
    public Date getDateCreated() {
        return datecreated;
    }
    public String getDateCreatedFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return formatter.format(datecreated);
    }
    public void setDateCreated(Date datecreated) {
        this.datecreated = datecreated;
    }
    public Date getDateEnd() {
        return dateend;
    }
    public String getDateEndedFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return formatter.format(dateend);
    }
    public void setDateEnd(Date dateend) {
        this.dateend = dateend;
    }
    public Integer getCurrencyID() {
        return currencyid;
    }
    public void setCurrencyID(Integer currencyid) {
        this.currencyid = currencyid;
    }
}