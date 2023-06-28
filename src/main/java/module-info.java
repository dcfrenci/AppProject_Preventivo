module com.preventivoapp.appproject_preventivo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    /*requires com.itextpdf.core;
    requires com.itextpdf.barcodes;
    requires com.itextpdf.forms;
    requires com.itextpdf.hyph;
    requires com.itextpdf.io;
    requires com.itextpdf.kernel;
    requires com.itextpdf.layout;
    requires com.itextpdf.pdfa;
    requires com.itextpdf.sign;
    requires com.itextpdf.styled-xml-parser;
    requires com.itextpdf.svg;
    requires com.itextpdf.bouncy-castle-connector;*/
    opens com.preventivoapp.appproject_preventivo to javafx.fxml;
    exports com.preventivoapp.appproject_preventivo;
    exports com.preventivoapp.appproject_preventivo.controller;
    opens com.preventivoapp.appproject_preventivo.controller to javafx.fxml;
    exports com.preventivoapp.appproject_preventivo.classes;
    opens com.preventivoapp.appproject_preventivo.classes to javafx.fxml;
}