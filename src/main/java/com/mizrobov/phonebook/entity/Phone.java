package com.mizrobov.phonebook.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "Phone")
@Table(name = "phones")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "type")
    private String type;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;

    public Phone() {
    }

    public Phone(String number, String type) {
        this.number = number;
        this.type = type;
    }

    public Phone(Long id, String number, String type) {
        this.id = id;
        this.number = number;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Phone phone = (Phone) o;
        return id.equals(phone.id) &&
                number.equals(phone.number) &&
                Objects.equals(type, phone.type) &&
                Objects.equals(person, phone.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, type, person);
    }
}
