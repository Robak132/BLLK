package com.bllk.Mapclasses;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="TRANSACTIONS")
public class TransactionRecord {
    @Id
    @Column(name = "TRANSACTION_ID")
    private Integer id;
    @Column(name = "SENDER_ID")
    private Integer senderID;
    @Column(name = "RECEIVER_ID")
    private Integer receiverID;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "VALUE")
    private Long value;
    @Column(name = "CURRENCY_ID")
    private Integer currencyID;
    @Column(name = "TRANSACTION_DATE")
    private Date date;

    public TransactionRecord(Integer senderID, Integer receiverID, String title, Long value, Integer currencyID) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.title = title;
        this.value = value;
        this.currencyID = currencyID;
    }
    public TransactionRecord(Integer id, Integer senderID, Integer receiverID, String title, Long value, Integer currencyID) {
        this.id = id;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.title = title;
        this.value = value;
        this.currencyID = currencyID;
    }
    public TransactionRecord() {
    }

    public Integer getID() {
        return id;
    }
    public void setID(Integer id) {
        this.id = id;
    }
    public Integer getSenderID() {
        return senderID;
    }
    public void setSenderID(Integer senderid) {
        this.senderID = senderid;
    }
    public Integer getReceiverID() {
        return receiverID;
    }
    public void setReceiverID(Integer receiverid) {
        this.receiverID = receiverid;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Long getValue() {
        return value;
    }
    public void setValue(Long value) {
        this.value = value;
    }
    public Integer getCurrencyID() {
        return currencyID;
    }
    public void setCurrencyID(Integer _currencyID) {
        this.currencyID = _currencyID;
    }
    public Date getDate() {
        return date;
    }
    public String getDateFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return formatter.format(date);
    }
    public void setDate(Date date) {
        this.date = date;
    }
}