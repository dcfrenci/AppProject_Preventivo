package com.preventivoapp.appproject_preventivo.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;

public class Service {
    private StringProperty serviceName;
    private double servicePrice;
    private double servicePriceForTooth;

    public Service(StringProperty serviceName, double servicePrice, double servicePriceForTooth) {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.servicePriceForTooth = servicePriceForTooth;
    }

    public Service(){
        this.serviceName = new SimpleStringProperty("");
        this.servicePrice = 0;
        this.servicePriceForTooth = 0;
    }

    public Service(Service copyOf){
        this.serviceName = new SimpleStringProperty(copyOf.getServiceName());
        this.servicePrice = copyOf.getServicePrice();
        this.servicePriceForTooth = copyOf.getServicePriceForTooth();
    }
    public String getServiceName() {
        return serviceName.get();
    }

    public ObservableStringValue serviceNameProperty() {
        return serviceName;
    }
    public StringProperty getServiceNameProperty(){
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName.set(serviceName);
    }

    public double getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(double servicePrice) {
        this.servicePrice = servicePrice;
    }

    public double getServicePriceForTooth() {
        return servicePriceForTooth;
    }

    public void setServicePriceForTooth(double servicePriceForTooth) {
        this.servicePriceForTooth = servicePriceForTooth;
    }

    @Override
    public String toString() {
        return  "Service:\n" +
                "\tserviceName=" + getServiceName() +
                "\n\tservicePrice=" + servicePrice +
                "\n\tservicePriceForTooth=" + servicePriceForTooth;
    }

    @Override
    public Object clone(){
        Service service;
        try {
            service = (Service) super.clone();
        } catch (CloneNotSupportedException e){
            service = new Service(this.getServiceNameProperty(), this.getServicePrice(), this.getServicePriceForTooth());
        }
        return service;
    }
}
