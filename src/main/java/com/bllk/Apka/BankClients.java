package com.bllk.Apka;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class BankClients {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String surname;

    public BankClients(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public BankClients(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public BankClients() {
    }

    @Id
    @GeneratedValue(generator = "incrementator-inator")
    @GenericGenerator(name = "incrementator-inator", strategy = "increment")
    public Long getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }

    public void setID(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        return "BankClients{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname);
    }
}
