package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.classes.Person;
import com.preventivoapp.appproject_preventivo.classes.Quote;
import com.preventivoapp.appproject_preventivo.classes.Service;
import com.preventivoapp.appproject_preventivo.classes.ServiceDetail;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class quoteMainController {
    //QUOTE TAB -->
    @FXML private TableColumn<Quote, LocalDate> quoteDateColumn;
    @FXML private Button quoteEdit;
    @FXML private TableColumn<Quote, String> quoteLastNameColumn;
    @FXML private TableColumn<Quote, String> quoteNameColumn;
    @FXML private Button quoteNew;
    @FXML private Button quoteRemove;
    @FXML private TextField quoteSearchField;
    @FXML private TableView<Quote> quoteTable;

    //PRICE LIST TAB -->
    @FXML private Button serviceEdit;
    @FXML private TableColumn<Service, String> serviceNameColumn;
    @FXML private Button serviceNew;
    @FXML private TableColumn<Service, Double> servicePriceColumn;
    @FXML private TableColumn<Service, Double> servicePriceForToothColumn;
    @FXML private Button serviceRemove;
    @FXML private TextField serviceSearchField;
    @FXML private TableView<Service> serviceTable;

    private ObservableList<Service> serviceList = FXCollections.observableArrayList();
    private ObservableList<Quote> quoteList = FXCollections.observableArrayList();
    private FilteredList<Quote> quoteSearched;
    private FilteredList<Service> serviceSearched;

    /*
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        //Load quote table
        quoteNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Quote, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Quote, String> param) {
                return param.getValue().getPerson().firstNameProperty();
            }
        });
        quoteLastNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Quote, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Quote, String> param) {
                return param.getValue().getPerson().lastNameProperty();
            }
        });
        quoteDateColumn.setCellValueFactory(new PropertyValueFactory<>("quoteDate"));

        //Load price list table
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        servicePriceColumn.setCellValueFactory(new PropertyValueFactory<>("servicePrice"));
        servicePriceForToothColumn.setCellValueFactory(new PropertyValueFactory<>("servicePriceForTooth"));


        //Load QUOTE and SERVICE table
        loadQuotes();
        loadServices();
    }
    private void loadQuotes(){
        //temporary (must be implemented through SQLite
        quoteList.addAll(getQuoteListTemp());
        //Permanent
        filteredQuoteList();
        quoteSearchField.textProperty().addListener(observable -> {
            String string = quoteSearchField.getText();
            if (string == null || string.length() == 0){
                quoteSearched.setPredicate(quote -> true);
            } else {
                quoteSearched.setPredicate(quote -> {
                    if (quote.getPerson().getFirstName().toLowerCase().contains(string.toLowerCase())) return true;
                    return quote.getPerson().getLastName().toLowerCase().contains(string.toLowerCase());
                });
            }
        });
    }

    private void filteredQuoteList(){
        quoteSearched = new FilteredList<>(getQuoteList(), quote -> true);
        quoteTable.setItems(quoteSearched);
    }

    private void loadServices(){
        //temporary
        serviceList.addAll(getServiceListTemp());
        //Permanent
        filteredServiceList();
        serviceSearchField.textProperty().addListener(observable -> {
            String string = serviceSearchField.getText();
            if (string == null || string.length() == 0){
                serviceSearched.setPredicate(service -> true);
            } else {
                serviceSearched.setPredicate(service -> service.getServiceName().toLowerCase().contains(string.toLowerCase()));
            }
        });
    }

    private void filteredServiceList(){
        serviceSearched = new FilteredList<>(getServicesList(), service -> true);
        serviceTable.setItems(serviceSearched);
    }

    /*
     * Handler of NEW BUTTON in the quote tab page and price list tab page
     */
    @FXML
    public void handleNewService() throws IOException{
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("serviceSetting-view.fxml"));
        DialogPane parent = loader.load();
        //Create a controller of the new page used to load the serviceList into the new controller
        serviceSettingController serviceSettingController = (serviceSettingController) loader.getController();
        serviceSettingController.setServiceSettingController(null);
        //Create a new dialog pane
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Service");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setDialogPane(parent);
    }
    @FXML
    public void handleNewQuote() throws IOException{
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("quoteSetting-view.fxml"));
        Parent parent = loader.load();
        //Create a controller of the new page used to load the serviceList into the new controller
        quoteSettingController quoteSettingController = (quoteSettingController) loader.getController();
        quoteSettingController.setQuoteSettingController(getServicesList(), null);
        //Create a new stage = new window with all its properties
        Stage stage = new Stage();
        stage.setMaximized(true);
        stage.setTitle("New Quote");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(quoteNew.getScene().getWindow());
        stage.showAndWait();
        if (quoteSettingController.getToSave()) {
            addQuoteToList(quoteSettingController.getQuote());
        }
    }

    /*
     * Handler of EDIT BUTTON in the quote and price-list tab page
     */

    public void handleEditQuote() throws IOException{
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("quoteSetting-view.fxml"));
        Parent parent = loader.load();
        //Create a controller of the new page used to set quoteSettingController
        quoteSettingController quoteSettingController = (quoteSettingController) loader.getController();
        int indexSelected;
        try {
            indexSelected = selectedIndex(quoteTable);
        } catch (NoSuchElementException e ){
            showNoElementSelected();
            return;
        }
        quoteSettingController.setQuoteSettingController(getServicesList(), quoteTable.getItems().get(indexSelected));
        //Create a new stage = new window with all its properties
        Stage stage = new Stage();
        stage.setMaximized(true);
        stage.setTitle("Edit Quote");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(quoteNew.getScene().getWindow());
        stage.showAndWait();
        if (quoteSettingController.getToSave()){
            addQuoteToList(quoteSettingController.getQuote(), indexSelected);
        }
    }

    /*
     * Handler of DELETE BUTTON in the quote tab page and price list tab page
     */
    @FXML
    public void handleDeleteQuote(){
        try{
            int selectedIndex = selectedIndex(quoteTable);
            quoteTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e){
            showNoElementSelected();
        }
    }
    @FXML
    public void handleDeleteService(){
        try{
            int selectedIndex = selectedIndex(serviceTable);
            serviceTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e){
            showNoElementSelected();
        }
    }
    public void showNoElementSelected(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No element selected");
        alert.setContentText("Please select an element in the table.");
        alert.showAndWait();
    }

    /*
     * QUOTE-LIST SERVICE-LIST METHODS: -------------------------------------------
     */
    public void addServiceToList (Service service) {
        serviceList.add(service);
    }

    public void addQuoteToList (Quote quote, int index){
        quoteList.set(index, quote);
        filteredQuoteList();
        quoteTable.refresh();
    }
    public void addQuoteToList (Quote quote){
        quoteList.add(quote);
        filteredQuoteList();
        quoteTable.refresh();
    }

    public ObservableList<Service> getServicesList (){
        return this.serviceList;
    }
    public void setServiceList(ObservableList<Service> serviceList) {
        this.serviceList = serviceList;
    }
    public ObservableList<Quote> getQuoteList(){
        return this.quoteList;
    }
    @Override
    public String toString() {
        return "quoteMainController{" +
                "serviceList=" + serviceList +
                '}';
    }
    /*
     * MIXED METHODS: -----------------------------------------------------------
     */
    /**
     * Return the index of the selected element in the TableView component
     * @return Index in the selected table
     */
    public int selectedIndex(TableView tableView){
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if(selectedIndex < 0) throw new NoSuchElementException();
        return selectedIndex;
    }

    public ObservableList<Service> getServiceListTemp() {
        ObservableList<Service> observableList = FXCollections.observableArrayList();
        observableList.add(new Service(new SimpleStringProperty("Denti 1"), 120, 0));
        observableList.add(new Service(new SimpleStringProperty("Denti 2"), 5146, 12));
        observableList.add(new Service(new SimpleStringProperty("Denti 3"), 451, 1));
        observableList.add(new Service(new SimpleStringProperty("Denti 4"), 615.45, 345));
        observableList.add(new Service(new SimpleStringProperty("Denti 5"), 1598,   1818));
        observableList.add(new Service(new SimpleStringProperty("Denti 6"), 156, 156));
        return observableList;
    }

    public ObservableList<Quote> getQuoteListTemp() {
        ObservableList<Quote> observableList = FXCollections.observableArrayList();
        observableList.add(new Quote(new Person(new SimpleStringProperty("This me"), new SimpleStringProperty("Mario")), temp(), new SimpleObjectProperty<>(LocalDate.now())));
        return observableList;
    }

    public List<ServiceDetail> temp(){
        List<ServiceDetail> list = new ArrayList<>();
        list.add(new ServiceDetail(new Service(new SimpleStringProperty("Denti 1"), 120, 0)));
        return list;
    }
}
