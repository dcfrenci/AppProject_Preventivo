package com.preventivoapp.appproject_preventivo;

import javafx.beans.property.ObjectProperty;

import java.time.LocalDate;
import java.util.List;

public class Quote {
    private final Person person;
    private final List<Service> servicesChosen;
    private final ObjectProperty<LocalDate> quoteDate;

    public Quote(Person person, List<Service> servicesChosen, ObjectProperty<LocalDate> quoteDate) {
        this.person = person;
        this.servicesChosen = servicesChosen;
        this.quoteDate = quoteDate;
    }

    public Person getPerson() {
        return person;
    }

    public List<Service> getServicesChosen() {
        return servicesChosen;
    }

    public LocalDate getQuoteDate() {
        return quoteDate.get();
    }

    public ObjectProperty<LocalDate> quoteDateProperty() {
        return quoteDate;
    }

    public void setQuoteDate(LocalDate quoteDate) {
        this.quoteDate.set(quoteDate);
    }
}
