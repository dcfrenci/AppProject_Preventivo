package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.classes.Person;
import com.preventivoapp.appproject_preventivo.classes.Quote;
import com.preventivoapp.appproject_preventivo.classes.Service;
import com.preventivoapp.appproject_preventivo.classes.ServiceDetail;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
    @FXML private Button quoteDateOfCreation;
    @FXML private Button quoteEdit;
    @FXML private TableColumn<Quote, String> quoteLastNameColumn;
    @FXML private Button quoteNameAZ;
    @FXML private TableColumn<Quote, String> quoteNameColumn;
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

    private ObservableList<Service> serviceList = FXCollections.observableArrayList();
    private ObservableList<Quote> quoteList = FXCollections.observableArrayList();

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
            //--> quote
        //setServiceList(getServiceListTemp());
            serviceList.addAll(getServiceListTemp());
        serviceTable.setItems(getServicesList());
            quoteList.addAll(getQuoteListTemp());
        quoteTable.setItems(getQuoteList());

        //Listener for changes of element in the quote table
        //quoteTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> showQuoteDetails(newValue)));

        //Listener for changes of element in the list table
        //serviceTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> showServiceDetail(newValue)));
    }

    /*
     * Used to display the content in the columns in the QUOTE TAB PAGE, PRICE LIST TAB PAGE
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
     * Handler of NEW BUTTON in the quote tab page and price list tab page
     */
    @FXML
    public void handleNewService(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("serviceSetting-view.fxml"));
            DialogPane view = loader.load();
            serviceSettingController controller = loader.getController();

            //controller.addServiceToList(new Service(String, "servicePrice", "servicePriceForTooth"));
            controller.addServiceToList(new Service(new SimpleStringProperty("serviceName"), 0, 0));
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("New Service");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);


            /*Optional<ButtonType> clickedButton = dialog.showAndWait();
            if(clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK){
                serviceTable.getItems().add(controller.)
            }*/
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    @FXML
    public void handleNewQuote() throws IOException{
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("quoteSetting-view.fxml"));
        Parent parent = loader.load();
        //Create a controller of the new page used to load the serviceList into the new controller
        quoteSettingController quoteSettingController = (quoteSettingController) loader.getController();
        quoteSettingController.setServiceListInNewQuote(getServicesList());
        //Create a new stage = new window with all its properties
        Stage stage = new Stage();
        stage.setTitle("New Quote");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(quoteNew.getScene().getWindow());
        stage.show();
        addQuoteToList(quoteSettingController.getQuote());
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


    /*
     * QUOTE-LIST SERVICE-LIST METHODS: -------------------------------------------
     */

    /**
     * Add a service to SERVICE-LIST
     * @param service to be added
     */
    public void addServiceToList (Service service) {
        serviceList.add(service);
    }
    /**
     * Add a quote to QUOTE-LIST
     * @param quote to be added
     */
    public void addQuoteToList (Quote quote){
        quoteList.add(quote);
        quoteTable.setItems(getQuoteList());
    }

    /**
     * Return the ObservableList of ALL services that were been created
     * @return the list of all services
     */
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
