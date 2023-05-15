package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.classes.Person;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class quoteMainController {
    //QUOTE TAB -->
    @FXML private TableColumn<Quote, LocalDate> quoteDateColumn;
    @FXML private TableColumn<Quote, String> quoteLastNameColumn;
    @FXML private TableColumn<Quote, String> quoteNameColumn;
    @FXML private Button quoteNew;
    @FXML private TextField quoteSearchField;
    @FXML private TableView<Quote> quoteTable;

    //PRICE LIST TAB -->
    @FXML private TableColumn<Service, String> serviceNameColumn;
    @FXML private TableColumn<Service, Double> servicePriceColumn;
    @FXML private TableColumn<Service, Double> servicePriceForToothColumn;
    @FXML private TextField serviceSearchField;
    @FXML private TableView<Service> serviceTable;
    private ObservableList<Service> serviceList;
    private ObservableList<Quote> quoteList;
    private FilteredList<Quote> quoteSearched;
    private FilteredList<Service> serviceSearched;

    /*
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        //Load QUOTE table
        quoteNameColumn.setCellValueFactory(param -> param.getValue().getPerson().firstNameProperty());
        quoteLastNameColumn.setCellValueFactory(param -> param.getValue().getPerson().lastNameProperty());
        quoteDateColumn.setCellValueFactory(new PropertyValueFactory<>("quoteDate"));

        //Load PRICE LIST table
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        servicePriceColumn.setCellValueFactory(param -> {
            if (param.getValue().getServicePrice() == 0) return null;
            return new SimpleObjectProperty<>(param.getValue().getServicePrice());
        });
        servicePriceForToothColumn.setCellValueFactory(new PropertyValueFactory<>("servicePriceForTooth"));
        servicePriceForToothColumn.setCellValueFactory(param -> {
            if (param.getValue().getServicePriceForTooth() == 0) return null;
            return new SimpleObjectProperty<>(param.getValue().getServicePriceForTooth());
        });

        //Load QUOTE and SERVICE table
        loadQuotes();
        loadServices();
    }
    private void loadQuotes(){
        //Permanent
        quoteList = FXCollections.observableArrayList();

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
        //Permanent
        serviceList  = FXCollections.observableArrayList();

        //temporary (must be implemented through SQLite
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
        serviceSettingController serviceSettingController = loader.getController();
        serviceSettingController.setServiceSettingController(null);
        //Create a new dialog pane
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Service");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setDialogPane(parent);
        dialog.show();
        Button save = (Button) dialog.getDialogPane().lookupButton(ButtonType.APPLY);
        save.addEventFilter(ActionEvent.ACTION, event -> {
            if (!serviceSettingController.handleServiceSave()){
                event.consume();
            } else {
                dialog.show();
                addServiceToList(serviceSettingController.getService());
            }
        });
    }
    @FXML
    public void handleNewQuote() throws IOException{
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("quoteSetting-view.fxml"));
        Parent parent = loader.load();
        //Create a controller of the new page used to load the serviceList into the new controller
        quoteSettingController quoteSettingController = loader.getController();
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
    public void handleEditService() throws IOException{
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("serviceSetting-view.fxml"));
        DialogPane parent = loader.load();
        //Create a controller of the new page used to load the serviceList into the new controller
        serviceSettingController serviceSettingController = loader.getController();
        int indexSelected;
        try {
            indexSelected = selectedIndexInServiceTable(serviceTable);
        } catch (NoSuchElementException e ){
            showNoElementSelected();
            return;
        }
        Service serviceBackup = new Service(serviceTable.getItems().get(indexSelected));
        serviceSettingController.setServiceSettingController(serviceTable.getItems().get(indexSelected));
        //Create a new dialog pane
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Service");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setDialogPane(parent);
        Optional<ButtonType> clickedButton = dialog.showAndWait();
        if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.APPLY){
            if (serviceSettingController.handleServiceSave()){
                addServiceToList(serviceSettingController.getService(), indexSelected);
            }
        } else {
            addServiceToList(serviceBackup, indexSelected);
        }
    }
    public void handleEditQuote() throws IOException{
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("quoteSetting-view.fxml"));
        Parent parent = loader.load();
        //Create a controller of the new page used to set quoteSettingController
        quoteSettingController quoteSettingController = loader.getController();
        int indexSelected;
        try {
            indexSelected = selectedIndexInQuoteTable(quoteTable);
        } catch (NoSuchElementException e ){
            showNoElementSelected();
            return;
        }
        //System.out.println("---TABLE---\n" + quoteTable.getItems().get(indexSelected));
        System.out.println("---LISTA---\n" + quoteList.get(indexSelected));
        Quote quoteBackup = new Quote(quoteTable.getItems().get(indexSelected));
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
        } else {
            addQuoteToList(quoteBackup, indexSelected);
            System.out.println("---BACKUP---\n" + quoteBackup);
        }
        System.out.println("---LISTA AFTER---\n" + quoteList.get(indexSelected));
        //System.out.println("---TABLE AFTER---\n" + quoteTable.getItems().get(indexSelected));
    }

    /*
     * Handler of DELETE BUTTON in the quote tab page and price list tab page
     */
    @FXML
    public void handleDeleteQuote(){
        try{
            int selectedIndex = selectedIndexInQuoteTable(quoteTable);
            removeQuoteToList(selectedIndex);
        } catch (NoSuchElementException e){
            showNoElementSelected();
        }
    }
    @FXML
    public void handleDeleteService(){
        try{
            int selectedIndex = selectedIndexInServiceTable(serviceTable);
            removeServiceToList(selectedIndex);
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
    public void addServiceToList (Service service, int index) {
        serviceList.set(index, service);
        filteredServiceList();
        serviceTable.refresh();
    }
    public void addServiceToList (Service service) {
        serviceList.add(service);
        filteredServiceList();
        serviceTable.refresh();
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

    public void removeServiceToList(int index){
        serviceList.remove(index);
        filteredServiceList();
        serviceTable.refresh();
    }
    public void removeQuoteToList (int index){
        quoteList.remove(index);
        filteredQuoteList();
        quoteTable.refresh();
    }

    public ObservableList<Service> getServicesList (){
        return this.serviceList;
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
    public int selectedIndexInServiceTable(TableView<Service> tableView){
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if(selectedIndex < 0) throw new NoSuchElementException();
        return selectedIndex;
    }
    public int selectedIndexInQuoteTable(TableView<Quote> tableView){
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if(selectedIndex < 0) throw new NoSuchElementException();
        return selectedIndex;
    }
    public int selectedIndexInServiceDetailTable(TableView<ServiceDetail> tableView){
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if(selectedIndex < 0) throw new NoSuchElementException();
        return selectedIndex;
    }

    public ObservableList<Service> getServiceListTemp() {
        ObservableList<Service> observableList = FXCollections.observableArrayList();
        observableList.add(new Service(new SimpleStringProperty("Dent 1"), 120, 0));
        observableList.add(new Service(new SimpleStringProperty("Dent 2"), 0, 12));
        observableList.add(new Service(new SimpleStringProperty("Dent 3"), 451, 0));
        observableList.add(new Service(new SimpleStringProperty("Dent 4"), 615.45, 0));
        observableList.add(new Service(new SimpleStringProperty("Dent 5"), 0,   1818));
        observableList.add(new Service(new SimpleStringProperty("Dent 6"), 0, 156));
        return observableList;
    }

    public ObservableList<Quote> getQuoteListTemp() {
        ObservableList<Quote> observableList = FXCollections.observableArrayList();
        observableList.add(new Quote(new Person(new SimpleStringProperty("This me"), new SimpleStringProperty("Mario")), temp(), new SimpleObjectProperty<>(LocalDate.now())));
        return observableList;
    }

    public List<ServiceDetail> temp(){
        List<ServiceDetail> list = new ArrayList<>();
        list.add(new ServiceDetail(new Service(new SimpleStringProperty("Dent 1"), 120, 0)));
        return list;
    }
}
