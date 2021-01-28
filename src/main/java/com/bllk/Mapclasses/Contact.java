package com.bllk.Mapclasses;

import javax.persistence.*;

@Entity
@Table(name = "CONTACTS")
public class Contact {
    @Id
    @Column(name = "CONTACT_ID")
    Integer id;
    @Column(name = "OWNER_ID")
    Integer ownerid;
    @Column(name = "TARGET_ID")
    Integer targetid;
    @Column(name = "NAME")
    String name;

    public Contact(Integer id, Integer ownerid, Integer targetid, String name) {
        this.id = id;
        this.ownerid = ownerid;
        this.targetid = targetid;
        this.name = name;
    }
    public Contact() {}

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
    public Integer getTargetID() {
        return targetid;
    }
    public void setTargetID(Integer targetid) {
        this.targetid = targetid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
