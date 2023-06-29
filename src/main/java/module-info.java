module com.preventivoapp.appproject_preventivo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.apache.fontbox;
    requires org.apache.pdfbox;
    requires java.desktop;
    opens com.preventivoapp.appproject_preventivo to javafx.fxml;
    exports com.preventivoapp.appproject_preventivo;
    exports com.preventivoapp.appproject_preventivo.controller;
    opens com.preventivoapp.appproject_preventivo.controller to javafx.fxml;
    exports com.preventivoapp.appproject_preventivo.classes;
    opens com.preventivoapp.appproject_preventivo.classes to javafx.fxml;
}