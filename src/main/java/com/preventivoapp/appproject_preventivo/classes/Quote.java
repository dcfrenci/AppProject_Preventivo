package com.preventivoapp.appproject_preventivo.classes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Quote {
    private Person person;
    private List<ServiceDetail> servicesChosen;
    private ObjectProperty<LocalDate> quoteDate;

    public Quote(Person person, List<ServiceDetail> servicesChosen, ObjectProperty<LocalDate> quoteDate) {
        this.person = person;
        this.servicesChosen = servicesChosen;
        this.quoteDate = quoteDate;
    }

    public Quote() {
        this.person = new Person(new SimpleStringProperty(""), new SimpleStringProperty(""));
        this.servicesChosen = new ArrayList<>();
        this.quoteDate = new SimpleObjectProperty<>(LocalDate.of(0, 1,1));
    }

    public Quote(Quote copyOf){
        this.person = new Person(copyOf.getPerson().firstNameProperty(), copyOf.getPerson().lastNameProperty());
        this.servicesChosen = copyOf.getServicesChosen();
        this.quoteDate = copyOf.quoteDateProperty();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<ServiceDetail> getServicesChosen() {
        return servicesChosen;
    }

    public void setServicesChosen(List<ServiceDetail> servicesChosen) {
        this.servicesChosen = servicesChosen;
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

    public List<Integer> getTeethSelected(Service service){
        for(ServiceDetail elem: getServicesChosen()){
            if(elem.getChosenService().getServiceName().compareTo(service.getServiceName()) == 0) return elem.getChosenTeeth();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Quote={\n" +
                "\tPerson: " + getPerson().getFirstName() + " (name), " + getPerson().getLastName() + " (lastname)\n" +
                "\tLocalDate: " + getQuoteDate() + "\n" +
                "\tChosenServices:\n" +
                servicesChosen + "}";
    }

    @Override
    public Object clone(){
        Quote quote;
        try {
            quote = (Quote) super.clone();
        } catch (CloneNotSupportedException e){
            quote = new Quote(this.getPerson(), this.getServicesChosen(), this.quoteDateProperty());
        }
        quote.person = (Person) this.person.clone();
        quote.servicesChosen = cloneServiceChosen();
        quote.quoteDate = this.quoteDateProperty();
        //quote.servicesChosen = (List<ServiceDetail>) this.servicesChosen.clone();
        return quote;
    }
    private List<ServiceDetail> cloneServiceChosen(){
        List<ServiceDetail> list = new ArrayList<>();
        for(ServiceDetail serviceDetail: this.getServicesChosen()){
            list.add((ServiceDetail) serviceDetail.clone());
        }
        return list;
    }
}
