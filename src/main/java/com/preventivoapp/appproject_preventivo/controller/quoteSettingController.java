package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.classes.Person;
import com.preventivoapp.appproject_preventivo.classes.Quote;
import com.preventivoapp.appproject_preventivo.classes.Service;
import com.preventivoapp.appproject_preventivo.classes.ServiceDetail;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.action.Action;

import java.io.Console;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class quoteSettingController extends quoteMainController{
    //NEW QUOTE -->
    @FXML private Button newQuoteAdd;
    @FXML private DatePicker newQuoteDate;
    @FXML private TextField newQuoteLastName;
    @FXML private TextField newQuoteName;
    @FXML private TableColumn<ServiceDetail, String> newQuoteNameChosenColumn;
    @FXML private TableColumn<Service, String> newQuoteNameColumn;
    @FXML private TableColumn<ServiceDetail, Integer> newQuoteNumberColumn;
    @FXML private Button newQuotePreview;
    @FXML private Label windowName;
    @FXML private TableColumn<Service, Double> newQuotePriceColumn;
    @FXML private TableColumn<Service, Double> newQuotePriceForToothColumn;
    @FXML private Button newQuoteRemove;
    @FXML private Button newQuoteSave;
    @FXML private TextField newQuoteSearch;
    @FXML private TableColumn<Quote, Integer> newQuoteSelectedTooth;
    @FXML private TableView<Service> quoteAllService;
    @FXML private TableView<ServiceDetail> quoteSelectedService;
    private Quote quote;
    private ObservableList<Service> serviceList;
    private FilteredList<Service> serviceSearchedToSelect;
    private boolean toSave;
    @FXML
    public void initialize() {
        //Add listener for PERSON and DATE of the new quote
        newQuoteName.textProperty().addListener(((observable, oldValue, newValue) -> quote.getPerson().setFirstName(newValue)));
        newQuoteLastName.textProperty().addListener(((observable, oldValue, newValue) -> quote.getPerson().setLastName(newValue)));
        newQuoteDate.valueProperty().addListener(((observable, oldValue, newValue) -> quote.setQuoteDate(newValue)));

        //Initialized the table of ALL services
        newQuoteNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        newQuotePriceColumn.setCellValueFactory(new PropertyValueFactory<>("servicePrice"));
        newQuotePriceForToothColumn.setCellValueFactory(new PropertyValueFactory<>("servicePriceForTooth"));

        //Initialized the table of SELECTED services
        newQuoteNameChosenColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ServiceDetail, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ServiceDetail, String> param) {
                return param.getValue().getChosenService().serviceNameProperty();
            }
        });
        newQuoteNumberColumn.setCellValueFactory(new PropertyValueFactory<>("TimeSelected"));
        newQuoteSelectedTooth.setCellValueFactory(new PropertyValueFactory<>("ChosenTeeth"));

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

    public Quote getQuote(){
        return quote;
    }

    public Boolean getToSave(){
        return toSave;
    }

    public void setToSave(boolean status){
        this.toSave = status;
    }

    /**
     * Load the serviceList of the controller with the ObservableList and create the filteredList used to show services in the table
     * @param setterServiceList to set the list of ALL services
     */
    public void setQuoteSettingController(ObservableList<Service> setterServiceList, Quote oldQuote) {
        this.serviceList = setterServiceList;
        this.serviceSearchedToSelect = new FilteredList<>(setterServiceList, service -> true);
        if (oldQuote != null){
            windowName.setText("Edit Quote");
            this.quote = oldQuote;
            newQuoteName.setText(quote.getPerson().getFirstName());
            newQuoteLastName.setText(quote.getPerson().getLastName());
            newQuoteDate.setValue(quote.getQuoteDate());
            quoteSelectedService.setItems(FXCollections.observableList(quote.getServicesChosen()));
            quoteSelectedService.refresh();
        } else {
            this.quote = new Quote();
        }
        quoteAllService.setItems(serviceSearchedToSelect);
        setToSave(false);
    }

    /**
     * Method to handle ADD SERVICE TO QUOTE
     * Get the service from quoteAllService table and add it to the quote if already present update N°
     */
    public void handleNewQuoteAdd(){
        try{
            int selectedIndex = selectedIndex(quoteAllService);
            ServiceDetail serviceDetail = new ServiceDetail(quoteAllService.getItems().get(selectedIndex));
            for(ServiceDetail service: quote.getServicesChosen()){
                if(service.getChosenService().getServiceName().compareTo(serviceDetail.getChosenService().getServiceName()) == 0){
                    service.setTimeSelected(service.getTimeSelected() + 1);
                    quoteSelectedService.refresh();
                    return;
                }
            }
            quote.getServicesChosen().add(serviceDetail);
            quoteSelectedService.getItems().add(serviceDetail);
        } catch (NoSuchElementException e){
            showNoElementSelected();
        }
    }

    /**
     * Method to handle SAVE QUOTE.
     * Control if the quote has all the fields completed then close the newQuote window
     * (the new quote will be saved in the quoteMainController.handleNewQuote method
     */
    public void handleNewQuoteSave(ActionEvent actionEvent){
        if (quote.getPerson().firstNameProperty().toString().compareTo("") == 0 || quote.getPerson().lastNameProperty().toString().compareTo("") == 0 || quote.getQuoteDate().equals(LocalDate.of(0, 1, 1))){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not all fields were inserted");
            alert.setContentText("Please insert all the person detail and the date.");
            alert.showAndWait();
            return;
        }
        if (quote.getServicesChosen().size() == 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No services selected");
            alert.setContentText("Please select one or more services.");
            alert.showAndWait();
            return;
        }
        setToSave(true);
        Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        thisWindow.close();
    }

    public void handleNewQuoteRemove(){
        try{
            int selectedIndex = selectedIndex(quoteSelectedService);
            for(ServiceDetail serviceDetail: quote.getServicesChosen()){
                if(serviceDetail.getChosenService().getServiceName().compareTo(quoteSelectedService.getItems().get(selectedIndex).getChosenService().getServiceName()) == 0){
                    if (serviceDetail.getTimeSelected() > 1) {
                        serviceDetail.setTimeSelected(serviceDetail.getTimeSelected() - 1);
                        quoteSelectedService.refresh();
                        return;
                    }
                }
            }
            quote.getServicesChosen().remove(quoteSelectedService.getItems().get(selectedIndex));
            quoteSelectedService.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e){
            showNoElementSelected();
        }
    }
}