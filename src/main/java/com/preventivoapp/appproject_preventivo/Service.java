package com.preventivoapp.appproject_preventivo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Service {
    private final StringProperty serviceName;
    private final double servicePrice;
    private final double servicePriceForTooth;

    public Service(StringProperty serviceName, double servicePrice, double servicePriceForTooth) {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.servicePriceForTooth = servicePriceForTooth;
    }

    public String getServiceName() {
        return serviceName.get();
    }

    public StringProperty serviceNameProperty() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName.set(serviceName);
    }

    public double getServicePrice() {
        return servicePrice;
    }

    public double getServicePriceForTooth() {
        return servicePriceForTooth;
    }
}
