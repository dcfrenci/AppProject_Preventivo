package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.QuoteMainApplication;
import com.preventivoapp.appproject_preventivo.classes.Pdf;
import com.preventivoapp.appproject_preventivo.classes.Quote;
import com.preventivoapp.appproject_preventivo.classes.Service;
import com.preventivoapp.appproject_preventivo.classes.ServiceDetail;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Objects;

public class quoteSettingController extends quoteMainController {
    //NEW QUOTE -->
    @FXML
    private DatePicker newQuoteDate;
    @FXML
    private TextField newQuoteLastName;
    @FXML
    private TextField newQuoteName;
    @FXML
    private TableColumn<ServiceDetail, String> newQuoteNameChosenColumn;
    @FXML
    private TableColumn<Service, String> newQuoteNameColumn;
    @FXML
    private TableColumn<ServiceDetail, Integer> newQuoteNumberColumn;
    @FXML
    private Label windowName;
    @FXML
    private TableColumn<Service, Double> newQuotePriceColumn;
    @FXML
    private TableColumn<Service, Double> newQuotePriceForToothColumn;
    @FXML
    private TextField newQuoteSearch;
    @FXML
    private TableColumn<ServiceDetail, String> newQuoteSelectedTooth;
    @FXML
    private TableView<Service> quoteAllService;
    @FXML
    private TableView<ServiceDetail> quoteSelectedService;
    private Quote quote;
    private Pdf pdf;
    private FilteredList<Service> serviceSearchedToSelect;
    private boolean toSave;

    @FXML
    public void initialize() {
        setToSave(false);
        //Add listener for PERSON and DATE of the new quote
        newQuoteName.textProperty().addListener(((observable, oldValue, newValue) -> quote.getPerson().setFirstName(newValue)));
        newQuoteLastName.textProperty().addListener(((observable, oldValue, newValue) -> quote.getPerson().setLastName(newValue)));
        newQuoteDate.valueProperty().addListener(((observable, oldValue, newValue) -> quote.setQuoteDate(newValue)));

        //Initialized the table of ALL services
        newQuoteNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        newQuotePriceColumn.setCellValueFactory(param -> {
            if (param.getValue().getServicePrice() == 0)
                return null;
            return new SimpleObjectProperty<>(param.getValue().getServicePrice());
        });
        newQuotePriceForToothColumn.setCellValueFactory(param -> {
            if (param.getValue().getServicePriceForTooth() == 0)
                return null;
            return new SimpleObjectProperty<>(param.getValue().getServicePriceForTooth());
        });

        //Initialized the table of SELECTED services
        newQuoteNameChosenColumn.setCellValueFactory(param -> param.getValue().getChosenService().serviceNameProperty());
        newQuoteNumberColumn.setCellValueFactory(new PropertyValueFactory<>("TimeSelected"));
        newQuoteSelectedTooth.setCellValueFactory(param -> {
            if (param.getValue().getChosenTeeth() == null)
                if (param.getValue().getChosenService().getServicePriceForTooth() > 0) return new SimpleStringProperty(" .....");
                else return new SimpleStringProperty("");
            return new SimpleStringProperty(param.getValue().showTeeth());
        });

        //Add a listener to the ALL service table to show only searched serviced
        newQuoteSearch.textProperty().addListener(observable -> {
            String nameSearched = newQuoteSearch.getText();
            if (nameSearched == null || nameSearched.length() == 0) {
                serviceSearchedToSelect.setPredicate(service -> true);
            } else {
                serviceSearchedToSelect.setPredicate(service -> service.getServiceName().toLowerCase().contains(nameSearched.toLowerCase()));
            }
        });
    }

    public Quote getQuote() {
        return quote;
    }

    public Boolean getToSave() {
        return toSave;
    }

    public void setToSave(boolean status) {
        this.toSave = status;
    }

    private void setObservableChosenService() {
        ObservableList<ServiceDetail> observableChosenService = FXCollections.observableArrayList(quote.getServicesChosen());
        quoteSelectedService.setItems(observableChosenService);
        quoteSelectedService.refresh();
    }

    /**
     * Load the serviceList of the controller with the ObservableList and create the filteredList used to show services in the table
     *
     * @param setterServiceList to set the list of ALL services
     */
    public void setQuoteSettingController(ObservableList<Service> setterServiceList, Quote oldQuote, Pdf oldPdf) {
        this.serviceSearchedToSelect = new FilteredList<>(setterServiceList, service -> true);
        quoteAllService.setItems(serviceSearchedToSelect);
        pdf = oldPdf;
        if (oldQuote != null) {
            windowName.setText("Edit Quote");
            this.quote = oldQuote;
            newQuoteName.setText(quote.getPerson().getFirstName());
            newQuoteLastName.setText(quote.getPerson().getLastName());
            newQuoteDate.setValue(quote.getQuoteDate());
            setObservableChosenService();
        } else {
            this.quote = new Quote();
            setObservableChosenService();
        }
    }

    /**
     * Method to handle ADD SERVICE TO QUOTE
     * Get the service from quoteAllService table and add it to the quote if already present update N°
     */
    public void handleNewQuoteAdd() {
        try {
            int selectedIndex = selectedIndexInServiceTable(quoteAllService);
            ServiceDetail serviceDetail = new ServiceDetail(quoteAllService.getItems().get(selectedIndex));
            serviceDetail.setChosenTeeth(null);
            if (serviceDetail.getChosenService().getServicePrice() > 0) {
                for (ServiceDetail service : quote.getServicesChosen()) {
                    if (service.getChosenService().getServiceName().compareTo(serviceDetail.getChosenService().getServiceName()) == 0) {
                        service.setTimeSelected(service.getTimeSelected() + 1);
                        setObservableChosenService();
                        return;
                    }
                }
            } else {
                for (ServiceDetail service : quote.getServicesChosen()) {
                    if (service.getChosenService().getServiceName().compareTo(serviceDetail.getChosenService().getServiceName()) == 0) return;
                }
            }
            quote.getServicesChosen().add(serviceDetail);
            setObservableChosenService();
        } catch (NoSuchElementException e) {
            showNoElementSelected();
        }
    }

    /**
     * Method to handle SAVE QUOTE.
     * Control if the quote has all the fields completed then close the newQuote window
     * (the new quote will be saved in the quoteMainController.handleNewQuote method)
     */
    public void handleNewQuoteSave(ActionEvent actionEvent) {
        if (quote.getPerson().firstNameProperty().toString().compareTo("") == 0 || quote.getPerson().lastNameProperty().toString().compareTo("") == 0 || quote.getQuoteDate().equals(LocalDate.of(0, 1, 1))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not all fields were inserted");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
            alert.setContentText("Please insert all the person detail and the date.");
            alert.showAndWait();
            return;
        }
        if (quote.getServicesChosen().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No services selected");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
            alert.setContentText("Please select one or more services.");
            alert.showAndWait();
            return;
        }
        for (ServiceDetail serviceDetail : quote.getServicesChosen()) {
            if (serviceDetail.getChosenService().getServicePriceForTooth() > 0) {
                if (serviceDetail.getChosenTeeth() == null || serviceDetail.getChosenTeeth().size() == 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Not all fields were inserted");
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
                    alert.setContentText("Please select the teeth for \"" + serviceDetail.getChosenService().getServiceName() + "\" service.");
                    alert.showAndWait();
                    return;
                }
            }
        }
        setToSave(true);
        Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        thisWindow.close();
    }

    /**
     * Method to handle REMOVE SERVICE FROM SELECTED
     * Remove the service selected from the selected service list in the quote
     */
    public void handleNewQuoteRemove() {
        try {
            int selectedIndex = selectedIndexInServiceDetailTable(quoteSelectedService);
            for (ServiceDetail serviceDetail : quote.getServicesChosen()) {
                if (serviceDetail.getChosenService().getServiceName().compareTo(quoteSelectedService.getItems().get(selectedIndex).getChosenService().getServiceName()) == 0) {
                    if (serviceDetail.getTimeSelected() > 1) {
                        serviceDetail.setTimeSelected(serviceDetail.getTimeSelected() - 1);
                        quoteSelectedService.refresh();
                        return;
                    }
                }
            }
            quote.getServicesChosen().remove(quoteSelectedService.getItems().get(selectedIndex));
            quoteSelectedService.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoElementSelected();
        }
    }

    /**
     * Method to handle SELECT TEETH
     */
    public void handleTeethSelected() throws IOException {
        int selectedIndex;
        try {
            selectedIndex = selectedIndexInServiceDetailTable(quoteSelectedService);
        } catch (NoSuchElementException e) {
            showNoElementSelected();
            return;
        }
        if (quoteSelectedService.getItems().get(selectedIndex).getChosenService().getServicePriceForTooth() <= 0)
            return;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("teethSelection-view.fxml"));
        Parent parent = loader.load();
        teethSelectionController teethSelectionController = loader.getController();
        teethSelectionController.setTeethSelectionController(quoteSelectedService.getItems().get(selectedIndex).getChosenTeeth());
        Stage stage = new Stage();
        stage.setTitle("Select teeth");
        stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(windowName.getScene().getWindow());
        stage.showAndWait();
        if (teethSelectionController.getToSave()) {
            for (ServiceDetail serviceDetail : quote.getServicesChosen()) {
                if (serviceDetail.getChosenService().getServiceName().compareTo(quoteSelectedService.getItems().get(selectedIndex).getChosenService().getServiceName()) == 0) {
                    serviceDetail.setChosenTeeth(teethSelectionController.mapToList());
                    quoteSelectedService.refresh();
                    break;
                }
            }
        }
    }

    /**
     * Method to handle QUOTE PREVIEW
     */
    public void handlePreviewButton() throws IOException {
        boolean complete = quote.getPerson().firstNameProperty().toString().compareTo("") != 0 && quote.getPerson().lastNameProperty().toString().compareTo("") != 0 && !quote.getQuoteDate().equals(LocalDate.of(0, 1, 1));
        if (quote.getServicesChosen().size() == 0)
            complete = false;
        for (ServiceDetail serviceDetail : quote.getServicesChosen()) {
            if (serviceDetail.getChosenService().getServicePriceForTooth() > 0) {
                if (serviceDetail.getChosenTeeth() == null || serviceDetail.getChosenTeeth().size() == 0) {
                    complete = false;
                    break;
                }
            }
        }
        if (complete)
            handlePreview(quote, pdf);
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete quote");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
            alert.setContentText("Please insert all the quote requirement");
            alert.showAndWait();
        }
    }
}