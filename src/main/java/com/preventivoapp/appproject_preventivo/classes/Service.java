package com.preventivoapp.appproject_preventivo.classes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;

import java.time.LocalDate;

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
        this.serviceName = null;
        this.servicePrice = 0;
        this.servicePriceForTooth = 0;
    }

    public String getServiceName() {
        return serviceName.get();
    }

    public ObservableStringValue serviceNameProperty() {
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
        return  "serviceName=" + serviceName +
                ", servicePrice=" + servicePrice +
                ", servicePriceForTooth=" + servicePriceForTooth
                ;
    }
}
