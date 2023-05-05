package com.preventivoapp.appproject_preventivo.classes;

import javafx.beans.property.ObjectProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class Quote {
    private Person person;
    private List<ServiceDetail> servicesChosen;
    private ObjectProperty<LocalDate> quoteDate;

    public Quote(Person person, List<ServiceDetail> servicesChosen, ObjectProperty<LocalDate> quoteDate) {
        this.person = person;
        this.servicesChosen = servicesChosen;
        this.quoteDate = quoteDate;
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

    public int getTimeSelected(Service service) {
        int times = 0;
        for(ServiceDetail elem: getServicesChosen()){
            if (elem.getChosenService().getServiceName().compareTo(service.getServiceName()) == 0) times++;
        }
        return times;
    }

    public List<Integer> getTeethSelected(Service service){
        for(ServiceDetail elem: getServicesChosen()){
            if(elem.getChosenService().getServiceName().compareTo(service.getServiceName()) == 0) return elem.getChosenTeeth();
        }
        return null;
    }

}
