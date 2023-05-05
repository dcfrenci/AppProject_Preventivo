module com.preventivoapp.appproject_preventivo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.preventivoapp.appproject_preventivo to javafx.fxml;
    exports com.preventivoapp.appproject_preventivo;
    exports com.preventivoapp.appproject_preventivo.controller;
    opens com.preventivoapp.appproject_preventivo.controller to javafx.fxml;
    exports com.preventivoapp.appproject_preventivo.classes;
    opens com.preventivoapp.appproject_preventivo.classes to javafx.fxml;
}