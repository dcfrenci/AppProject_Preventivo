package com.preventivoapp.appproject_preventivo.controller;

import com.preventivoapp.appproject_preventivo.QuoteMainApplication;
import com.preventivoapp.appproject_preventivo.classes.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Float.MIN_NORMAL;

public class pdfSettingController extends quoteMainController {
    @FXML
    private TextArea pdfDescription;
    @FXML
    private TextArea pdfHead;
    @FXML
    private TextArea pdfPayment;
    @FXML
    private TextField boxCharacterDimension;
    @FXML
    private TextField boxLeading;
    @FXML
    private TextField boxSpaceLong;
    @FXML
    private TextField boxSpaceShort;
    @FXML
    private TextField boxSpaceTop;
    @FXML
    private ComboBox<String> fontSelection;
    @FXML
    private ComboBox<String> languageSelection;
    private Pdf pdf;
    private boolean toSave;

    @FXML
    public void initialize() {
        ObservableList<String> fontList = FXCollections.observableArrayList("Helvetica", "Times Roman", "Courier");
        fontSelection.setItems(fontList);
        ObservableList<String> languageList = FXCollections.observableArrayList("English", "Italian");
        languageSelection.setItems(languageList);
    }

    public void setPdfSettingController(Pdf pdf) {
        if (pdf != null) {
            this.pdf = pdf;
            pdfHead.setText(pdf.getPdfHead());
            pdfDescription.setText(pdf.getPdfDescription());
            pdfPayment.setText(pdf.getPdfPayment());
        } else {
            this.pdf = new Pdf(MIN_NORMAL, MIN_NORMAL, MIN_NORMAL, MIN_NORMAL, MIN_NORMAL, null, "", null);
        }
        setToSave(false);
    }

    public boolean getToSave() {
        return toSave;
    }

    public void setToSave(boolean toSave) {
        this.toSave = toSave;
    }

    public Pdf getPdf() {
        return pdf;
    }

    public void handlePreviewPdfSetting() throws IOException {
        java.util.List<ServiceDetail> serviceDetails = new ArrayList<>();
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service example 1"), 0.0, 175.0), java.util.List.of(18, 19, 20), 1));
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service example 2"), 750.0, 0.0), null, 3));
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service example 3 with a very long name that's written onto two lines"), 0.0, 175.0), List.of(35), 1));
        Person person = new Person(new SimpleStringProperty("Name"), new SimpleStringProperty("Surname"));
        if (languageSelection.getSelectionModel().getSelectedItem() != null && languageSelection.getSelectionModel().getSelectedItem().equals("Italian")) {
            person.setFirstName("Nome");
            person.setLastName("Cognome");
        }
        Quote quote = new Quote(person, serviceDetails, new SimpleObjectProperty<>(LocalDate.now()));
        handlePreview(quote, checkFields());
    }

    public void handleSavePdfSetting(ActionEvent actionEvent) {
        //check fields
        Pdf savePdf = checkFields();
        if (savePdf == null)
            return;
        //ask confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle("Save confirmation");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
        alert.setHeaderText("Are you sure you want to save the current PDF setting ?");
        //alert.setContentText("Are you sure you want to delete this quote?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            setToSave(true);
            pdf = savePdf;
            Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            thisWindow.close();
        }
    }

    public void handleCancelPdfSetting(ActionEvent actionEvent) {
        Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        thisWindow.close();
    }

    private boolean checkValueBox(TextField textField) {
        String string = textField.getText();
        if (string.equals(""))
            return false;
        for (char c : string.toCharArray()) {
            if (!Character.isDigit(c)) {
                createAlertError("For " + textField.getId().substring("box".length()) + " is possible to accept only positive number");
                return true;
            }
        }
        return false;
    }

    private PDFont getSelectedFont() {
        String font = fontSelection.getSelectionModel().getSelectedItem();
        if (font == null)
            return PDType1Font.HELVETICA;
        if (font.equals("Times Roman"))
            return PDType1Font.TIMES_ROMAN;
        if (font.equals("Courier"))
            return PDType1Font.COURIER;
        return PDType1Font.HELVETICA;
    }

    private String getLanguage() {
        String language = languageSelection.getSelectionModel().getSelectedItem();
        if (language == null)
            return "English";
        return language;
    }

    private float getTextBox(TextField textField) {
        if (textField.getText().equals(""))
            return MIN_NORMAL;
        return Float.parseFloat(textField.getText());
    }

    private String getTextArea(TextArea textArea) {
        if (textArea.getText().equals(""))
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : textArea.getText().toCharArray()) {
            if (c >= ' ' || c == '\n')
                stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private Pdf checkFields() {
        if (checkValueBox(boxSpaceTop))
            return null;
        if (checkValueBox(boxSpaceShort))
            return null;
        if (checkValueBox(boxSpaceLong))
            return null;
        if (checkValueBox(boxLeading))
            return null;
        if (checkValueBox(boxCharacterDimension))
            return null;
        return getCurrentPdf();
    }

    private Pdf getCurrentPdf() {
        PDFont font = getSelectedFont();
        String language = getLanguage();
        Pdf currentPdf = new Pdf(getTextBox(boxCharacterDimension), getTextBox(boxLeading), getTextBox(boxSpaceTop), getTextBox(boxSpaceLong), getTextBox(boxSpaceShort), font, "", language);
        currentPdf.setPdfHead(getTextArea(pdfHead));
        currentPdf.setPdfDescription(getTextArea(pdfDescription));
        currentPdf.setPdfPayment(getTextArea(pdfPayment));
        return currentPdf;
    }
}
