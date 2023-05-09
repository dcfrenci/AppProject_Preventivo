package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.classes.Service;
import com.preventivoapp.appproject_preventivo.classes.ServiceDetail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.NoSuchElementException;

public class serviceSettingController extends quoteMainController {
    @FXML private TextField serviceNameField;
    @FXML private TextField servicePriceField;
    @FXML private TextField servicePriceForToothField;

    private Service service;
    private boolean toSave;

    @FXML
    public void initialize() {
        //Add listener for service property
        serviceNameField.textProperty().addListener(((observable, oldValue, newValue) -> service.setServiceName(newValue)));
        servicePriceField.textProperty().addListener(((observable, oldValue, newValue) -> service.setServicePrice(Double.parseDouble(newValue))));
        servicePriceForToothField.textProperty().addListener(((observable, oldValue, newValue) -> service.setServicePriceForTooth(Double.parseDouble(newValue))));
    }

    public void setServiceSettingController(Service oldService){
        setToSave(false);
        if (oldService != null){
            this.service = oldService;
            serviceNameField.setText(service.getServiceName());
            servicePriceField.setText(Double.toString(service.getServicePrice()));
            servicePriceForToothField.setText(Double.toString(service.getServicePriceForTooth()));
        } else {
            service = new Service();
        }
    }

    public Service getService(){
        return service;
    }

    private void setToSave(boolean state){
        this.toSave = state;
    }

    private boolean getToSave(){
        return toSave;
    }

    private void handleServiceSave(ActionEvent actionEvent){
        if (service.getServiceName().length() == 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not all fields were inserted");
            alert.setContentText("Please insert the service's name.");
            alert.showAndWait();
            return;
        }
        if (service.getServicePrice() != 0 && service.getServicePriceForTooth() != 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not all fields were inserted correctly");
            alert.setContentText("Please choose between \"Price\" and \"Price For Tooth\".");
            alert.showAndWait();
            return;
        }
        if (service.getServicePrice() <= 0 || service.getServicePriceForTooth() <= 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not all fields were inserted correctly");
            alert.setContentText("Please choose a positive \"Price\" or \"Price For Tooth\".");
            alert.showAndWait();
            return;
        }
        setToSave(true);
        Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        thisWindow.close();
    }












    /*@FXML
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
                addServiceToList(service);
            } else throw new NoSuchElementException();
        } catch (NoSuchElementException e) {
            showIncorretcInsertionAlert();
        }

    }*/
}
