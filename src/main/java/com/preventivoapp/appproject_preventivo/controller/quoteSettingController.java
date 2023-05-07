package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.classes.Person;
import com.preventivoapp.appproject_preventivo.classes.Quote;
import com.preventivoapp.appproject_preventivo.classes.Service;
import com.preventivoapp.appproject_preventivo.classes.ServiceDetail;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.io.Console;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class quoteSettingController extends quoteMainController{
    //NEW QUOTE -->
    @FXML private Button newQuoteAdd;
    @FXML private Button newQuoteCancel;
    @FXML private DatePicker newQuoteDate;
    @FXML private TextField newQuoteLastName;
    @FXML private TextField newQuoteName;
    @FXML private TableColumn<ServiceDetail, Service> newQuoteNameChosenColumn;
    @FXML private TableColumn<Service, String> newQuoteNameColumn;
    @FXML private TableColumn<ServiceDetail, Integer> newQuoteNumberColumn;
    @FXML private Button newQuotePreview;
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
        newQuoteNameChosenColumn.setCellValueFactory(new PropertyValueFactory<>("ChosenService"));
        newQuoteNumberColumn.setCellValueFactory(new PropertyValueFactory<>("TimeSelected"));
        newQuoteSelectedTooth.setCellValueFactory(new PropertyValueFactory<>("ChosenTeeth"));

        //Initialized the QUOTE
        quote = new Quote();

        //Initialized the SERVICES TABLE


        //Add listener for TABLE of ALL services
        quoteAllService.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> showServiceAll(newValue)));

        //Add listener for TABLE of SELECTED services
        quoteSelectedService.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> showChosenService(newValue)));
    }

    /*
     * Used to display the content in the columns in the ALL SERVICE table and CHOSEN SERVICE table
     */
    private void showServiceAll(Service service){
        if (service != null) {
            System.out.println("ciao belli ");
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

    private void showChosenService(ServiceDetail service){
        if(service != null){
            System.out.println(service.getChosenService().getServiceName());

            newQuoteNameChosenColumn.setText(service.getChosenService().getServiceName());
            newQuoteNumberColumn.setText("Working on");
            newQuoteSelectedTooth.setText(service.showTeeth());
        }
        else{
            newQuoteNameChosenColumn.setText("");
            newQuoteNumberColumn.setText("");
            newQuoteSelectedTooth.setText("");
        }
    }
    void update(){
        newQuoteName.textProperty().set(quote.getPerson().getFirstName());
        newQuoteLastName.textProperty().set(quote.getPerson().getLastName());
        newQuoteDate.valueProperty().set(quote.getQuoteDate());
    }

    public Quote getQuote(){
        return quote;
    }

    /**
     * Load the serviceList of the controller with the ObservableList passed as param
     * @param setterServiceList to set the list of ALL services
     */
    public void setServiceListInNewQuote(ObservableList<Service> setterServiceList) {
        this.serviceList = setterServiceList;
        quoteAllService.setItems(serviceList);
    }

    /*
     * Method to handle ADD SERVICE TO QUOTE
     * Get the service from quoteAllService table and add it to the quote
     */
    public void handleNewQuoteAdd(){
        try{
            int selectedIndex = selectedIndex(quoteAllService);
            ServiceDetail serviceDetail = new ServiceDetail(quoteAllService.getItems().get(selectedIndex));
            serviceDetail.setChosenTeeth(null);
            quote.getServicesChosen().add(serviceDetail);
            quoteSelectedService.setItems(FXCollections.observableArrayList(quote.getServicesChosen()));

            //System.out.println(quote.getServicesChosen().get(quote.getServicesChosen().indexOf(0)).getChosenService().getServiceName());
        } catch (NoSuchElementException e){
            showNoElementSelected();
        }
    }
    /*
     * Method to handle SAVE QUOTE
     * Save the quote and add it to the list of quote in the quote tab
     */
    public void handleNewQuoteSave(ActionEvent actionEvent){
        if (quote.getPerson().firstNameProperty() == null || quote.getPerson().lastNameProperty() == null || quote.getQuoteDate() == null){
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
        //The quote can be saved
        addQuoteToList(quote);

        Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        thisWindow.close();
    }
}