package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.QuoteMainApplication;
import com.preventivoapp.appproject_preventivo.classes.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class serviceSettingController extends quoteMainController {
    @FXML
    private TextField serviceNameField;
    @FXML
    private TextField servicePriceField;
    @FXML
    private TextField servicePriceForToothField;
    @FXML
    private Label windowName;
    private Service service;

    @FXML
    public void initialize() {
        //Add listener for service property
        serviceNameField.textProperty().addListener(((observable, oldValue, newValue) -> service.setServiceName(newValue)));
        servicePriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 0 || containsAlphabetic(newValue)) {
                service.setServicePrice(0);
                return;
            }
            service.setServicePrice(Double.parseDouble(newValue));
        });
        servicePriceForToothField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 0 || containsAlphabetic(newValue)) {
                service.setServicePriceForTooth(0);
                return;
            }
            service.setServicePriceForTooth(Double.parseDouble(newValue));
        });
    }

    public void setServiceSettingController(Service oldService) {
        if (oldService != null) {
            windowName.setText("Edit Service");
            this.service = new Service((Service) oldService.clone());
            serviceNameField.setText(service.getServiceName());
            servicePriceField.setText(Double.toString(service.getServicePrice()));
            servicePriceForToothField.setText(Double.toString(service.getServicePriceForTooth()));
        } else {
            service = new Service();
        }
    }

    public Service getService() {
        return service;
    }

    private boolean containsAlphabetic(String string) {
        for (char elem : string.toCharArray()) {
            if (Character.isAlphabetic(elem)) {
                service.setServicePriceForTooth(0);
                return true;
            }
        }
        return false;
    }

    public boolean handleServiceSave() {
        if (service.getServiceName().length() == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not all fields were inserted");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
            alert.setContentText("Please insert the service's name.");
            alert.showAndWait();
            return false;
        }
        if (service.getServicePrice() != 0 && service.getServicePriceForTooth() != 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not all fields were inserted correctly");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
            alert.setContentText("Please choose between \"Price\" and \"Price For Tooth\".");
            alert.showAndWait();
            return false;
        }
        if ((service.getServicePrice() <= 0 && service.getServicePriceForTooth() <= 0) || (containsAlphabetic(servicePriceField.getText()) || containsAlphabetic(servicePriceForToothField.getText()))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not all fields were inserted correctly");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
            alert.setContentText("Please choose a positive number for \"Price\" or \"Price For Tooth\".");
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
