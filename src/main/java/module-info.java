module com.example.gui_datenbank {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.gui_datenbank to javafx.fxml;
    exports com.example.gui_datenbank;
}