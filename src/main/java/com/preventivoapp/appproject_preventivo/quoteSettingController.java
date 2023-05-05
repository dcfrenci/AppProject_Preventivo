package com.preventivoapp.appproject_preventivo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

public class QuoteSettingController {
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
    @FXML private TableColumn<?, ?> newQuotePriceColumn;
    @FXML private TableColumn<?, ?> newQuotePriceForToothColumn;
    @FXML private Button newQuoteRemove;
    @FXML private Button newQuoteSave;
    @FXML private TextField newQuoteSearch;
    @FXML private TableColumn<?, ?> newQuoteSelectedTooth;
}
