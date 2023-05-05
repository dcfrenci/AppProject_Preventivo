package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.classes.Quote;
import com.preventivoapp.appproject_preventivo.classes.Service;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class quoteSettingController {
    //NEW QUOTE -->
    @FXML private Button newQuoteAdd;
    @FXML private Button newQuoteCancel;
    @FXML private DatePicker newQuoteDate;
    @FXML private TextField newQuoteLastName;
    @FXML private TextField newQuoteName;
    @FXML private TableColumn<Service, String> newQuoteNameChosenColumn;
    @FXML private TableColumn<Service, String> newQuoteNameColumn;
    @FXML private TableColumn<Quote, Integer> newQuoteNumberColumn;
    @FXML private Button newQuotePreview;
    @FXML private TableColumn<Service, Double> newQuotePriceColumn;
    @FXML private TableColumn<Service, Double> newQuotePriceForToothColumn;
    @FXML private Button newQuoteRemove;
    @FXML private Button newQuoteSave;
    @FXML private TextField newQuoteSearch;
    @FXML private TableColumn<Quote, Integer> newQuoteSelectedTooth;
    @FXML private TableView<Service> quoteAllService;
    @FXML private TableView<Service> quoteSelectedService;
    Quote quote;

    @FXML
    public void initialized(){
        //Add listener for PERSON and DATE of the new quote
        newQuoteName.textProperty().addListener(((observable, oldValue, newValue) -> quote.getPerson().setFirstName(newValue)));
        newQuoteLastName.textProperty().addListener(((observable, oldValue, newValue) -> quote.getPerson().setLastName(newValue)));
        newQuoteDate.valueProperty().addListener(((observable, oldValue, newValue) -> quote.setQuoteDate(newValue)));

        //Initialized the table of ALL services
        newQuoteNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        newQuotePriceColumn.setCellValueFactory(new PropertyValueFactory<>("servicePrice"));
        newQuotePriceForToothColumn.setCellValueFactory(new PropertyValueFactory<>("servicePriceForTooth"));

        //Initialized the table of SELECTED services
        newQuoteNameChosenColumn.setCellValueFactory(new PropertyValueFactory<>("serviceChosenName"));
        newQuoteNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serviceChosenNumber"));
        newQuoteSelectedTooth.setCellValueFactory(new PropertyValueFactory<>("serviceChosenTooth"));

        //Add listener for TABLE of ALL services
        quoteAllService.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> showServiceAll(newValue)));
        //quoteSelectedService.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> showChosenService(newValue)));

        //Add listener for TABLE of SELECTED services


    }

    /*
     * Used to display the content in the columns in the ALL SERVICE table
     */
    private void showServiceAll(Service service){
        if (service != null) {
            newQuoteNameColumn.setText(service.getServiceName());
            newQuotePriceColumn.setText(Double.toString(service.getServicePrice()));
            newQuotePriceForToothColumn.setText(Double.toString(service.getServicePriceForTooth()));
        }
        else{
            newQuoteNameColumn.setText("");
            newQuotePriceColumn.setText("");
            newQuotePriceForToothColumn.setText("");
        }
    }

    private void showChosenService(Service service){
        if(service != null){
            newQuoteNameChosenColumn.setText(service.getServiceName());
            newQuoteNumberColumn.setText(getNumberOfSpecificService(service));
        }
    }
    /*
     * @return the number of a specific service in a quote
     */
    private String getNumberOfSpecificService(Service service){
        //for(Service cmp: quote.getServicesChosen().)
        return "filo";
    }
}