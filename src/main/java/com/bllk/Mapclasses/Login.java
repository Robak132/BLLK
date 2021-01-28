package com.bllk.Mapclasses;

import javax.persistence.*;

@Entity
@Table(name = "LOGINS")
public class Login {
    @Id
    @Column(name = "LOGIN_ID")
    private Integer id;
    @Column(name = "LOGIN")
    private String login;
    @Column(name = "PASSWORD_HASH")
    private String passwordhash;

    public Login(String login, String passwordhash) {
        this.login = login;
        this.passwordhash = passwordhash;
    }
    public Login(Integer id, String login, String passwordhash) {
        this.id = id;
        this.login = login;
        this.passwordhash = passwordhash;
    }
    public Login() {
    }

    public Integer getID() {
        return id;
    }
    public void setID(Integer id) {
        this.id = id;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPasswordHash() {
        return passwordhash;
    }
    public void setPasswordHash(String passwordhash) {
        this.passwordhash = passwordhash;
    }
}
