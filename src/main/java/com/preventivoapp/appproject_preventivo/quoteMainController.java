package com.preventivoapp.appproject_preventivo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.NoSuchElementException;

public class quoteMainController {
    //QUOTE TAB -->
    @FXML private TableColumn<Quote, LocalDate> quoteDateColumn;
    @FXML private Button quoteDateOfCreation;
    @FXML private Button quoteEdit;
    @FXML private TableColumn<Person, String> quoteLastNameColumn;
    @FXML private Button quoteNameAZ;
    @FXML private TableColumn<Person, String> quoteNameColumn;
    @FXML private Button quoteNew;
    @FXML private Button quoteRemove;
    @FXML private TextField quoteSearch;
    @FXML private TableView<Quote> quoteTable;

    //PRICE LIST TAB -->
    @FXML private Button serviceEdit;
    @FXML private TableColumn<Service, String> serviceNameColumn;
    @FXML private Button serviceNew;
    @FXML private TableColumn<Service, Double> servicePriceColumn;
    @FXML private TableColumn<Service, Double> servicePriceForToothColumn;
    @FXML private Button serviceRemove;
    @FXML private TextField serviceSearch;
    @FXML private TableView<Service> serviceTable;

    /*
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        //Load quote table
        quoteNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        quoteLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        quoteDateColumn.setCellValueFactory(new PropertyValueFactory<>("quoteDate"));

        //Load price list table
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        servicePriceColumn.setCellValueFactory(new PropertyValueFactory<>("servicePrice"));
        servicePriceForToothColumn.setCellValueFactory(new PropertyValueFactory<>("servicePriceForTooth"));

        //Listener for changes of element in the quote table
        quoteTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> showQuoteDetails(newValue)));

        //Listener for changes of element in the list table
        serviceTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> showServiceDetail(newValue)));
    }

    /*
     * Used to display the content in the columns in the QUOTE TAB PAGE
     */
    private void showQuoteDetails(Quote quote){
        if(quote != null){
            quoteNameColumn.setText(quote.getPerson().getFirstName());
            quoteLastNameColumn.setText(quote.getPerson().getLastName());
            quoteDateColumn.setText(quote.getQuoteDate().toString());
        }
        else{
            quoteNameColumn.setText("");
            quoteLastNameColumn.setText("");
            quoteDateColumn.setText("");
        }
    }

    /*
     * Used to display the content in the columns in the SERVICE TAB PAGE
     */
    private void showServiceDetail(Service service){
        if(service != null){
            serviceNameColumn.setText(service.getServiceName());
            servicePriceColumn.setText(Double.toString(service.getServicePrice()));
            servicePriceForToothColumn.setText(Double.toString(service.getServicePriceForTooth()));
        }
        else{
            serviceNameColumn.setText("");
            servicePriceColumn.setText("");
            servicePriceForToothColumn.setText("");
        }
    }

    /*
     * Return the index of the selected element in the TableView component
     * @return the index of the selected element
     */
     int selectedIndex(){
        int selectedIndex = quoteTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex < 0) throw new NoSuchElementException();
        return selectedIndex;
    }
    /*
     * Show a warning dialog when tried to delete an element whose not selected
     */
    void showNoElementSelected(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No element selected");
        alert.setContentText("Please select an element in the table.");
        alert.showAndWait();
    }
    @FXML
    public void handleDeleteQuote(){
        try{
            int selectedIndex = selectedIndex();
            quoteTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e){
            showNoElementSelected();
        }
    }
    @FXML
    public void handleDeleteService(){
        try{
            int selectedIndex = selectedIndex();
            serviceTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e){
            showNoElementSelected();
        }
    }
}
