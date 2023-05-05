package com.preventivoapp.appproject_preventivo.classes;

import javafx.beans.property.ObjectProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Quote {
    private final Person person;
    private final Map<Service, List<Integer>> servicesChosen;
    private final ObjectProperty<LocalDate> quoteDate;
    private final Integer timeSelected;

    public Quote(Person person, Map<Service, List<Integer>> servicesChosen, ObjectProperty<LocalDate> quoteDate, Integer timeSelected) {
        this.person = person;
        this.servicesChosen = servicesChosen;
        this.quoteDate = quoteDate;
        this.timeSelected = timeSelected;
    }

    public Person getPerson() {
        return person;
    }

    public Map<Service, List<Integer>> getServicesChosen() {
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

    public Integer getTimeSelected() {
        return timeSelected;
    }
}
