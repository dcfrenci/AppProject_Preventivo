module com.preventivoapp.appproject_preventivo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.preventivoapp.appproject_preventivo to javafx.fxml;
    exports com.preventivoapp.appproject_preventivo;
}