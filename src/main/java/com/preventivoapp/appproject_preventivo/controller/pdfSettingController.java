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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Float.MIN_NORMAL;

public class pdfSettingController extends quoteMainController{
    @FXML private TextArea pdfDescription;
    @FXML private TextArea pdfHead;
    @FXML private TextArea pdfPayment;
    @FXML private TextField boxCharacterDimension;
    @FXML private TextField boxLeading;
    @FXML private TextField boxSpaceLong;
    @FXML private TextField boxSpaceShort;
    @FXML private TextField boxSpaceTop;
    @FXML private ComboBox<String> fontSelection;

    private Pdf pdf;
    private boolean toSave;

    @FXML
    public void initialize(){
        pdfHead.setText(pdf.getPdfHead());
        pdfDescription.setText(pdf.getPdfDescription());
        pdfPayment.setText(pdf.getPdfPayment());

        ObservableList<String> fontList = FXCollections.observableArrayList("Helvetica", "Times Roman", "Courier");
        fontSelection.setItems(fontList);
    }

    public void setPdfSettingController(Pdf pdf) {
        if (pdf != null) this.pdf = pdf;
        else this.pdf = new Pdf(MIN_NORMAL, MIN_NORMAL, MIN_NORMAL, MIN_NORMAL, MIN_NORMAL, null, "");
        setToSave(false);
    }

    public boolean getToSave() {
        return toSave;
    }

    public void setToSave(boolean toSave) {
        this.toSave = toSave;
        if (toSave){
            pdf.setPdfHead(pdfHead.getText());
            pdf.setPdfDescription(pdfDescription.getText());
            pdf.setPdfPayment(pdfPayment.getText());
        }
    }

    public Pdf getPdf() {
        return pdf;
    }

    public void handlePreviewPdfSetting() throws IOException {
        java.util.List<ServiceDetail> serviceDetails = new ArrayList<>();
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service example 1"), 0.0, 175.0), java.util.List.of(18, 19, 20), 1));
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service example 2"), 750.0, 0.0), null, 3));
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service example 3 with a very long name that's written onto two lines"), 0.0, 175.0), List.of(35), 1));
        Quote quote = new Quote(new Person(new SimpleStringProperty("Name"), new SimpleStringProperty("Surname")), serviceDetails, new SimpleObjectProperty<>(LocalDate.now()));
        handlePreview(quote);
    }
    public void handleSavePdfSetting() {
        //check fields
        if (checkValueBox(boxSpaceTop)) return;
        if (checkValueBox(boxSpaceShort)) return;
        if (checkValueBox(boxSpaceLong)) return;
        if (checkValueBox(boxLeading)) return;
        if (checkValueBox(boxCharacterDimension)) return;
        //ask confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle("Save confirmation");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(QuoteMainApplication.class.getResourceAsStream("Images/program-icon.png"))));
        alert.setHeaderText("Are you sure you want to save the current PDF setting ?");
        //alert.setContentText("Are you sure you want to delete this quote?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            setToSave(true);
            PDFont font = getSelectedFont();
            pdf = new Pdf(Float.parseFloat(boxCharacterDimension.getText()), Float.parseFloat(boxLeading.getText()), Float.parseFloat(boxSpaceTop.getText()), Float.parseFloat(boxSpaceLong.getText()), Float.parseFloat(boxSpaceShort.getText()), font, "");
        }
    }

    public void handleCancelPdfSetting(ActionEvent actionEvent) {
        Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        thisWindow.close();
    }

    private boolean checkValueBox(TextField textField) {
        String string = textField.getText();
        if (string.equals("")) return true;
        for (char c: string.toCharArray()){
            if (!Character.isDigit(c)) {
                createAlertError("For " + textField + " is possible to accept only positive number");
                return true;
            }
        }
        return false;
    }

    private PDFont getSelectedFont() {
        String font = fontSelection.getSelectionModel().getSelectedItem();
        if (font.equals("Times Roman")) return PDType1Font.TIMES_ROMAN;
        if (font.equals("Courier")) return PDType1Font.COURIER;
        return PDType1Font.HELVETICA;
    }
}
