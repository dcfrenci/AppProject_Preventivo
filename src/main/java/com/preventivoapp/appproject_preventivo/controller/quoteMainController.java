package com.preventivoapp.appproject_preventivo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.preventivoapp.appproject_preventivo.QuoteMainApplication;
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
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.*;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

import static java.lang.Float.MIN_NORMAL;

public class quoteMainController {
    //QUOTE TAB -->
    @FXML
    private TableColumn<Quote, LocalDate> quoteDateColumn;
    @FXML
    private TableColumn<Quote, String> quoteLastNameColumn;
    @FXML
    private TableColumn<Quote, String> quoteNameColumn;
    @FXML
    private Button quoteNew;
    @FXML
    private TextField quoteSearchField;
    @FXML
    private TableView<Quote> quoteTable;

    //PRICE LIST TAB -->
    @FXML
    private TableColumn<Service, String> serviceNameColumn;
    @FXML
    private TableColumn<Service, Double> servicePriceColumn;
    @FXML
    private TableColumn<Service, Double> servicePriceForToothColumn;
    @FXML
    private TextField serviceSearchField;
    @FXML
    private TableView<Service> serviceTable;
    @FXML
    private Button serviceNew;
    @FXML
    private Button serviceEdit;

    //VARIABLES -->
    private ObservableList<Service> serviceList;
    private ObservableList<Quote> quoteList;
    private FilteredList<Quote> quoteSearched;
    private FilteredList<Service> serviceSearched;
    private Setting setting;
    private Pdf pdf;

    /*
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        //Load QUOTE table
        quoteNameColumn.setCellValueFactory(param -> param.getValue().getPerson().firstNameProperty());
        quoteLastNameColumn.setCellValueFactory(param -> param.getValue().getPerson().lastNameProperty());
        quoteDateColumn.setCellValueFactory(new PropertyValueFactory<>("quoteDate"));
        quoteTable.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {
                try {
                    int selectedIndex = selectedIndexInQuoteTable(quoteTable);
                    Quote quote = getQuoteList().get(selectedIndex);
                    handlePreview(quote, null);
                } catch (NoSuchElementException | IOException e) {
                    showNoElementSelected();
                }
            }
        });

        //Load PRICE LIST table
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        servicePriceColumn.setCellValueFactory(param -> {
            if (param.getValue().getServicePrice() == 0)
                return null;
            return new SimpleObjectProperty<>(param.getValue().getServicePrice());
        });
        servicePriceForToothColumn.setCellValueFactory(new PropertyValueFactory<>("servicePriceForTooth"));
        servicePriceForToothColumn.setCellValueFactory(param -> {
            if (param.getValue().getServicePriceForTooth() == 0)
                return null;
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
            if (!dir.exists()) {
                if (!new File(setting.getPathSetting()).mkdir())
                    createAlertError("Could not create setting directory");
                if (!new File(setting.getPathSetting() + "\\setting.json").createNewFile())
                    createAlertError("Could not create setting file");
                if (!new File(setting.getPathSetting() + "\\quoteList.json").createNewFile())
                    createAlertError("Could not create quoteList file");
                if (!new File(setting.getPathSetting() + "\\serviceList.json").createNewFile())
                    createAlertError("Could not create serviceList file");
                if (!new File(setting.getPathSetting() + "\\pdfSetting.json").createNewFile())
                    createAlertError("Could not create PDF setting file");
                //set the quote directory
                handleSaveQuotePath();
                //load QUOTE and SERVICE table
                loadQuotes(false);
                loadServices(false);
                pdf = new Pdf(MIN_NORMAL, MIN_NORMAL, MIN_NORMAL, MIN_NORMAL, MIN_NORMAL, null, "", null);
            } else {
                //check if the file already exists
                if (!new File(setting.getPathSetting() + "\\setting.json").exists()) {
                    createAlertError("Could not find setting file");
                    return;
                }
                File file = new File(setting.getPathSetting() + "\\setting.json");
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.registerModule(new JavaTimeModule());
                String quoteSavePath = mapper.readValue(file, new TypeReference<>() {
                });
                setting.setPathQuote(quoteSavePath);
                //load QUOTE and SERVICE table
                loadQuotes(true);
                loadServices(true);
                handleLoadPDF();
            }
        } catch (IOException e) {
            createAlertError("Could not load data");
            e.printStackTrace();
        }
    }

    public void handleSaveQuotePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File previousDir = new File(setting.getPathQuote());
        if (!setting.getPathQuote().equals("") && previousDir.exists())
            directoryChooser.setInitialDirectory(previousDir);
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            if (file.toString().contains(" ")) {
                createAlertError("The directory name could not contain space character");
                return;
            }
            setting.setPathQuote(file.toString());
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
                while (scanner.hasNext()) {
                    String serviceScan = scanner.nextLine();
                    if (serviceScan.startsWith("priceForTooth")) {
                        //priceForTooth service -->
                        String substring = serviceScan.substring("priceForTooth_".length());
                        String serviceName = substring.substring(0, substring.indexOf("_"));
                        double servicePrice = Double.parseDouble(substring.substring(substring.indexOf("_") + 1));
                        if (servicePrice != 0) {
                            Service service = new Service(new SimpleStringProperty(serviceName), 0, servicePrice);
                            importServiceList.add(service);
                        }
                    } else {
                        //price service -->
                        String substring = serviceScan.substring("price_".length());
                        String serviceName = substring.substring(0, substring.indexOf("_"));
                        double servicePrice = Double.parseDouble(substring.substring(substring.indexOf("_") + 1));
                        if (servicePrice != 0) {
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
        if (file != null) {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.toString()), StandardCharsets.UTF_8)) {
                for (Service service : serviceList) {
                    if (service.getServicePriceForTooth() == 0)
                        writer.write("price_" + service.getServiceName() + "_" + service.getServicePrice() + "\n");
                    if (service.getServicePrice() == 0)
                        writer.write("priceForTooth_" + service.getServiceName() + "_" + service.getServicePriceForTooth() + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handlePdfSetting() throws IOException {
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pdfSetting-view.fxml"));
        Parent parent = loader.load();
        //Create a controller of the new page used to load the serviceList into the new controller
        pdfSettingController pdfSettingController = loader.getController();
        if (pdf == null)
            pdfSettingController.setPdfSettingController(null);
        else
            pdfSettingController.setPdfSettingController(pdf);
        //Create a new stage = new window with all its properties
        Stage stage = new Stage();
        stage.setMaximized(true);
        stage.setTitle("Pdf setting");
        stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(quoteNew.getScene().getWindow());
        stage.showAndWait();
        if (pdfSettingController.getToSave()) {
            //save the new scheme
            pdf = pdfSettingController.getPdf();
        }
    }

    public void handleUserGuide() throws IOException {
        //Open the user guide
        String path = System.getProperty("user.dir") + "/src/main/resources/com/preventivoapp/appproject_preventivo/Pdf/quoteProgram-manual.pdf";
        File file = new File(path);
        if (file.exists()) {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        }
    }

    public void createAlertError(String string) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
        alert.setContentText(string);
        alert.showAndWait();
    }

    /*
     * LOADING in the program
     */
    private void loadQuotes(boolean load) {
        quoteList = FXCollections.observableArrayList();
        if (load)
            handleLoadQuote();
        filteredQuoteList();
        quoteSearchField.textProperty().addListener(observable -> {
            String string = quoteSearchField.getText();
            if (string == null || string.length() == 0) {
                quoteSearched.setPredicate(quote -> true);
            } else {
                quoteSearched.setPredicate(quote -> {
                    if (quote.getPerson().getFirstName().toLowerCase().contains(string.toLowerCase()))
                        return true;
                    return quote.getPerson().getLastName().toLowerCase().contains(string.toLowerCase());
                });
            }
        });
    }

    private void filteredQuoteList() {
        quoteSearched = new FilteredList<>(getQuoteList(), quote -> true);
        quoteTable.setItems(quoteSearched);
        quoteTable.refresh();
    }

    private void loadServices(boolean load) {
        serviceList = FXCollections.observableArrayList();
        if (load)
            handleLoadService();
        filteredServiceList();
        serviceSearchField.textProperty().addListener(observable -> {
            String string = serviceSearchField.getText();
            if (string == null || string.length() == 0) {
                serviceSearched.setPredicate(service -> true);
            } else {
                serviceSearched.setPredicate(service -> service.getServiceName().toLowerCase().contains(string.toLowerCase()));
            }
        });
    }

    private void filteredServiceList() {
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
            List<Quote> loadedQuote = mapper.readValue(file, new TypeReference<>() {
            });
            quoteList.addAll(loadedQuote);
        } catch (IOException e) {
            createAlertError("Could not load quotes data");
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
            List<Service> loadedService = mapper.readValue(file, new TypeReference<>() {
            });
            serviceList.addAll(loadedService);
        } catch (IOException e) {
            createAlertError("Could not load services data");
            e.printStackTrace();
        }
    }

    private void handleLoadPDF() {
        //load pdf with preexisting element
        try {
            File file = new File(setting.getPathSetting() + "\\pdfSetting.json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            Pdf loadedPDF = mapper.readValue(file, new TypeReference<>() {
            });
            pdf = new Pdf(loadedPDF, "");
        } catch (IOException e) {
            createAlertError("Could not load pdf setting data");
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
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, setting.getPathQuote());
            //save pdf setting
            file = new File(setting.getPathSetting() + "\\pdfSetting.json");
            mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, pdf);
        } catch (IOException e) {
            createAlertError("Could not load data");
        }
    }

    /*
     * Handler of NEW BUTTON in the quote tab page and price list tab page
     */
    @FXML
    public void handleNewService() throws IOException {
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
        dialog.initOwner(serviceNew.getScene().getWindow());
        dialog.setDialogPane(parent);
        dialog.show();
        Button save = (Button) dialog.getDialogPane().lookupButton(ButtonType.APPLY);
        save.addEventFilter(ActionEvent.ACTION, event -> {
            if (!serviceSettingController.handleServiceSave()) {
                event.consume();
            } else {
                dialog.show();
                addServiceToList(serviceSettingController.getService());
            }
        });
    }

    @FXML
    public void handleNewQuote() throws IOException {
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("quoteSetting-view.fxml"));
        Parent parent = loader.load();
        //Create a controller of the new page used to load the serviceList into the new controller
        quoteSettingController quoteSettingController = loader.getController();
        quoteSettingController.setQuoteSettingController(getServicesList(), null, pdf);
        //Create a new stage = new window with all its properties
        Stage stage = new Stage();
        stage.setMaximized(true);
        stage.setTitle("New Quote");
        stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
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
    public void handleEditService() throws IOException {
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("serviceSetting-view.fxml"));
        DialogPane parent = loader.load();
        //Create a controller of the new page used to load the serviceList into the new controller
        serviceSettingController serviceSettingController = loader.getController();
        int indexSelected;
        try {
            indexSelected = selectedIndexInServiceTable(serviceTable);
        } catch (NoSuchElementException e) {
            showNoElementSelected();
            return;
        }
        serviceSettingController.setServiceSettingController((Service) serviceTable.getItems().get(indexSelected).clone());
        //Create a new dialog pane
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Service");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(serviceEdit.getScene().getWindow());
        dialog.setDialogPane(parent);
        Optional<ButtonType> clickedButton = dialog.showAndWait();
        if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.APPLY) {
            if (serviceSettingController.handleServiceSave()) {
                addServiceToList(serviceSettingController.getService(), indexSelected);
            }
        }
    }

    public void handleEditQuote() throws IOException {
        //Load the .fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("quoteSetting-view.fxml"));
        Parent parent = loader.load();
        //Create a controller of the new page used to set quoteSettingController
        quoteSettingController quoteSettingController = loader.getController();
        int indexSelected;
        try {
            indexSelected = selectedIndexInQuoteTable(quoteTable);
        } catch (NoSuchElementException e) {
            showNoElementSelected();
            return;
        }
        quoteSettingController.setQuoteSettingController(getServicesList(), (Quote) quoteTable.getItems().get(indexSelected).clone(), pdf);
        //Create a new stage = new window with all its properties
        Stage stage = new Stage();
        stage.setMaximized(true);
        stage.setTitle("Edit Quote");
        stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(quoteNew.getScene().getWindow());
        stage.showAndWait();
        if (quoteSettingController.getToSave()) {
            addQuoteToList(quoteSettingController.getQuote(), indexSelected);
        }
    }

    /*
     * Handler of DELETE BUTTON in the quote tab page and price list tab page
     */
    @FXML
    public void handleDeleteQuote() {

        try {
            int selectedIndex = selectedIndexInQuoteTable(quoteTable);
            Quote quote = getQuoteList().get(selectedIndex);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setTitle("Delete confirmation");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
            alert.setHeaderText("Delete the quote: \"" + quote.getPerson().getLastName() + " " + quote.getPerson().getFirstName() + "\" ?");
            alert.setContentText("Are you sure you want to delete this quote?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                removeQuoteToList(selectedIndex);
            }
        } catch (NoSuchElementException e) {
            showNoElementSelected();
        }
    }

    @FXML
    public void handleDeleteService() {
        try {
            int selectedIndex = selectedIndexInServiceTable(serviceTable);
            Service service = getServicesList().get(selectedIndex);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setTitle("Delete confirmation");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
            alert.setHeaderText("Delete the service: \"" + service.getServiceName() + "\" ?");
            alert.setContentText("Are you sure you want to delete this service?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                removeServiceToList(selectedIndex);
            }
        } catch (NoSuchElementException e) {
            showNoElementSelected();
        }
    }

    public void showNoElementSelected() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No element selected");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
        alert.setContentText("Please select an element in the table.");
        alert.showAndWait();
    }

    /*
     * Handler of PREVIEW in the quote tab page
     */
    public void handlePreview(Quote quote, Pdf toPreview) throws IOException {
        //create pdf
        String path = System.getProperty("user.dir") + "\\setting\\temp.pdf";
        Pdf pdfPreview;
        if (toPreview != null)
            pdfPreview = new Pdf(toPreview, path);
        else
            pdfPreview = new Pdf(pdf, path);
        pdfPreview.createQuote(quote);
        File file = new File(path);
        if (file.exists()) {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
                file.deleteOnExit();
                return;
            }
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
        alert.setContentText("Could not show the preview of the quote");
        alert.showAndWait();
    }

    /*
     * Handler of EXPORT AS PDF BUTTON in the quote tab page
     */
    public void handleExportAsPdf() {
        try {
            int selectedIndex = selectedIndexInQuoteTable(quoteTable);
            Quote quote = getQuoteList().get(selectedIndex);
            Pdf pdfExport = new Pdf(pdf, setting.getPathQuote() + "\\" + quote.getPerson().getFirstName() + "_" + quote.getPerson().getLastName() + "_" + LocalDate.now() + ".pdf");
            pdfExport.createQuote(quote);
        } catch (NoSuchElementException e) {
            showNoElementSelected();
        }
    }

    /*
     * QUOTE-LIST SERVICE-LIST METHODS: -------------------------------------------
     */
    public void addServiceToList(Service service, int index) {
        serviceList.set(index, service);
        filteredServiceList();
        serviceTable.refresh();
    }

    public void addServiceToList(Service service) {
        serviceList.add(service);
        filteredServiceList();
        serviceTable.refresh();
    }

    public void addQuoteToList(Quote quote, int index) {
        quoteList.set(index, quote);
        filteredQuoteList();
        quoteTable.refresh();
    }

    public void addQuoteToList(Quote quote) {
        quoteList.add(quote);
        filteredQuoteList();
        quoteTable.refresh();
    }

    public void removeServiceToList(int index) {
        serviceList.remove(index);
        filteredServiceList();
        serviceTable.refresh();
    }

    public void removeQuoteToList(int index) {
        quoteList.remove(index);
        filteredQuoteList();
        quoteTable.refresh();
    }

    public ObservableList<Service> getServicesList() {
        return this.serviceList;
    }

    public ObservableList<Quote> getQuoteList() {
        return this.quoteList;
    }

    @Override
    public String toString() {
        return "quoteMainController{" + "serviceList=" + serviceList + '}';
    }
    /*
     * MIXED METHODS: -----------------------------------------------------------
     */

    /**
     * Return the index of the selected element in the TableView component
     *
     * @return Index in the selected table
     */
    public int selectedIndexInServiceTable(TableView<Service> tableView) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0)
            throw new NoSuchElementException();
        return selectedIndex;
    }

    public int selectedIndexInQuoteTable(TableView<Quote> tableView) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0)
            throw new NoSuchElementException();
        return selectedIndex;
    }

    public int selectedIndexInServiceDetailTable(TableView<ServiceDetail> tableView) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0)
            throw new NoSuchElementException();
        return selectedIndex;
    }
}