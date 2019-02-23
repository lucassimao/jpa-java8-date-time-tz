package com.lucassimao;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Person{

    @Id
    @GeneratedValue
    private long id;
    private String nome;
    ZonedDateTime birthdate;

    /**
     * @param birthdate the birthdate to set
     */
    public void setBirthdate(ZonedDateTime birthdate) {
        this.birthdate = birthdate;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


}