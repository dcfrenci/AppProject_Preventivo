package com.preventivoapp.appproject_preventivo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.preventivoapp.appproject_preventivo.classes.*;
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
import javafx.stage.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    //VARIABLES -->
    private ObservableList<Service> serviceList;
    private ObservableList<Quote> quoteList;
    private FilteredList<Quote> quoteSearched;
    private FilteredList<Service> serviceSearched;
    private Setting setting;

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
        //Load PROGRAM
        handleSetting();
    }

    /*
     * MENU BAR
     */
    public void handleSetting() {
        setting = new Setting(System.getProperty("user.dir"));
        try {
            //check if the directory exists
            File dir = new File(setting.getPathSetting());
            if (!dir.exists()){
                if (!new File(setting.getPathSetting()).mkdir()) new Alert(Alert.AlertType.ERROR, "Could not create setting directory").showAndWait();
                if (!new File(setting.getPathSetting() + "\\setting.json").createNewFile()) new Alert(Alert.AlertType.ERROR, "Could not create setting file").showAndWait();
                if (!new File(setting.getPathSetting() + "\\quoteList.json").createNewFile()) new Alert(Alert.AlertType.ERROR, "Could not create quoteList file").showAndWait();
                if (!new File(setting.getPathSetting() + "\\serviceList.json").createNewFile()) new Alert(Alert.AlertType.ERROR, "Could not create serviceList file").showAndWait();
                //set the quote directory
                handleSaveQuotePath();
                //load QUOTE and SERVICE table
                loadQuotes(false);
                loadServices(false);
            } else {
                //check if the file already exists
                if (!new File(setting.getPathSetting() + "\\setting.json").exists()) {
                    new Alert(Alert.AlertType.ERROR, "Could not find setting file").showAndWait();
                    return;
                }
                File file = new File(setting.getPathSetting() + "\\setting.json");
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.registerModule(new JavaTimeModule());
                String quoteSavePath = mapper.readValue(file, new TypeReference<>(){});
                setting.setPathQuote(quoteSavePath);
                //load QUOTE and SERVICE table
                loadQuotes(true);
                loadServices(true);
            }
        } catch (IOException e){
            new Alert(Alert.AlertType.ERROR, "Could not load data").showAndWait();
            e.printStackTrace();
        }
    }

    public void handleSaveQuotePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File previousDir = new File(setting.getPathQuote());
        if (!setting.getPathQuote().equals("")) directoryChooser.setInitialDirectory(previousDir);
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            if (file.toString().contains(" ")) {
                new Alert(Alert.AlertType.ERROR, "The directory name could not contain space character").showAndWait();
                return;
            }
            if (!new File(file + "\\quote").mkdir()) new Alert(Alert.AlertType.ERROR, "Could not create quote directory").showAndWait();
            setting.setPathQuote(file + "\\quote");
        }
    }

    public void handleImportServiceList() {
        //Select the file to import
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(null);
        //Scan the file
        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                ObservableList<Service> importServiceList = FXCollections.observableArrayList();
                while (scanner.hasNext()){
                    String serviceScan = scanner.nextLine();
                    if (serviceScan.startsWith("priceForTooth")){
                        //priceForTooth service -->
                        String substring = serviceScan.substring("priceForTooth_".length());
                        String serviceName = substring.substring(0, substring.indexOf("_"));
                        double servicePrice = Double.parseDouble(substring.substring(substring.indexOf("_") + 1));
                        if (servicePrice != 0){
                            Service service = new Service(new SimpleStringProperty(serviceName), 0, servicePrice);
                            importServiceList.add(service);
                        }
                    } else {
                        //price service -->
                        String substring = serviceScan.substring("price_".length());
                        String serviceName = substring.substring(0, substring.indexOf("_"));
                        double servicePrice = Double.parseDouble(substring.substring(substring.indexOf("_") + 1));
                        if (servicePrice != 0){
                            Service service = new Service(new SimpleStringProperty(serviceName), servicePrice, 0);
                            importServiceList.add(service);
                        }
                    }
                }
                //Refresh the serviceList
                serviceList.removeAll(getServicesList());
                serviceList.addAll(importServiceList);
                filteredServiceList();
                serviceTable.refresh();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleExportServiceList() {
        //Select name and position to Export .txt file
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(null);
        if (file != null){
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.toString()), StandardCharsets.UTF_8)) {
                for (Service service : serviceList) {
                    if (service.getServicePriceForTooth() == 0) writer.write("price_" + service.getServiceName() + "_" + service.getServicePrice() + "\n");
                    if (service.getServicePrice() == 0) writer.write("priceForTooth_" + service.getServiceName() + "_" + service.getServicePriceForTooth() + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * LOADING AND SAVING
     */
    private void loadQuotes(boolean load){
        quoteList = FXCollections.observableArrayList();
        if (load) handleLoadQuote();
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
        quoteTable.refresh();
    }

    private void loadServices(boolean load){
        serviceList  = FXCollections.observableArrayList();
        if (load) handleLoadService();
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
     * CONNECTION WITH FILES (loading, saving)
     */
    public void handleLoadQuote() {
        //load quoteList with preexisting element
        try {
            File file = new File(setting.getPathSetting() + "\\quoteList.json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            List<Quote> loadedQuote = mapper.readValue(file, new TypeReference<>() {});
            quoteList.addAll(loadedQuote);
        } catch (IOException e){
            new Alert(Alert.AlertType.ERROR, "Could not load data").showAndWait();
            e.printStackTrace();
        }
    }

    public void handleLoadService() {
        //load serviceList with preexisting element
        try {
            File file = new File(setting.getPathSetting() + "\\serviceList.json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            List<Service> loadedService = mapper.readValue(file, new TypeReference<>() {});
            serviceList.addAll(loadedService);
        } catch (IOException e){
            new Alert(Alert.AlertType.ERROR, "Could not load data").showAndWait();
            e.printStackTrace();
        }
    }

    public void handleSaveData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            //save quote
            File file = new File(setting.getPathSetting() + "\\quoteList.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, quoteTable.getItems());
            //save service
            file = new File(setting.getPathSetting() + "\\serviceList.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, serviceTable.getItems());
            //save setting
            file = new File(setting.getPathSetting() + "\\setting.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, setting.getPath());
        } catch (IOException e){
            new Alert(Alert.AlertType.ERROR, "Could not save data").showAndWait();
        }
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
        serviceSettingController.setServiceSettingController((Service) serviceTable.getItems().get(indexSelected).clone());
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
        quoteSettingController.setQuoteSettingController(getServicesList(), (Quote) quoteTable.getItems().get(indexSelected).clone());
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
}