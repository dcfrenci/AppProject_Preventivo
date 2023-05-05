package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.classes.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.NoSuchElementException;

public class serviceSettingController {
    @FXML
    private Button newServiceCancel;
    @FXML
    private TextField newServiceNameColumn;
    @FXML
    private TextField newServicePriceColumn;
    @FXML
    private TextField newServicePriceForToothColumn;
    @FXML
    private Button newServiceSave;

    Service service;

    @FXML
    public void initialize() {
        newServiceNameColumn.textProperty().addListener((observable, oldValue, newValue) -> service.setServiceName(newValue));
        newServicePriceColumn.textProperty().addListener((observable, oldValue, newValue) -> service.setServicePrice(Double.valueOf(newValue)));
        newServicePriceForToothColumn.textProperty().addListener((observable, oldValue, newValue) -> service.setServicePrice(Double.valueOf(newValue)));
    }

    void showIncorretcInsertionAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Field filled incorrectly");
        alert.setContentText("Please re-enter che correct values");
        alert.showAndWait();
    }


    public void handleSavePerson() {
        try {
            if ( service.getServiceName() != null && ((service.getServicePrice() > 0 && service.getServicePriceForTooth() <= 0) || (service.getServicePrice() <= 0 && service.getServicePriceForTooth() > 0)) ) {

            } else throw new NoSuchElementException();
        } catch (NoSuchElementException e) {
            showIncorretcInsertionAlert();
        }

    }
}
