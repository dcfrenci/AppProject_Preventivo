package com.preventivoapp.appproject_preventivo.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {
    private final StringProperty firstName;
    private final StringProperty lastName;

    public Person() {
        this.firstName = new SimpleStringProperty("");
        this.lastName = new SimpleStringProperty("");
    }

    public Person(StringProperty firstName, StringProperty lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName.startsWith(" ")) {
            setFirstName(firstName.substring(1));
            return;
        }
        if (firstName.endsWith(" ")) {
            setFirstName(firstName.substring(0, firstName.length() - 2));
            return;
        }
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName.startsWith(" ")) {
            setLastName(lastName.substring(1));
            return;
        }
        if (lastName.endsWith(" ")) {
            setLastName(lastName.substring(0, lastName.length() - 2));
            return;
        }
        this.lastName.set(lastName);
    }

    @Override
    public String toString() {
        return "firstName=" + firstName + ", lastName=" + lastName;
    }

    @Override
    public Object clone() {
        Person person;
        try {
            person = (Person) super.clone();
        } catch (CloneNotSupportedException e) {
            person = new Person(this.firstNameProperty(), this.lastNameProperty());
        }
        return person;
    }
}
