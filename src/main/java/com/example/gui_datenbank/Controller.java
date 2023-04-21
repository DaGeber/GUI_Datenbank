package com.example.gui_datenbank;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private String url = "jdbc:mysql://localhost:3306";
    private String user = "root";
    private String pass = "";
    private String selectedDatabase = "";
    Connection con = DriverManager.getConnection(url, user, pass);
    Statement stm = con.createStatement();
    @FXML
    private TextArea abfrageEingabe;
    @FXML
    private TableView<String> ausgabeTableErgebnis;
    @FXML
    private Button abfrageButton;

    @FXML
    private Button resetButton;

    @FXML
    private Label fehlerAusgabe;

    @FXML
    private Button beendenButton;
    @FXML
    private ComboBox datenbankAuswahl ;

    public Controller() throws SQLException {
        Runnable runnable = () -> {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            initialisieren();
        };
        Thread t = new Thread(runnable);
        t.start();
    }

    private void initialisieren() {
        ObservableList<String> options = FXCollections.observableArrayList();
        try {
            ResultSet rs = con.createStatement().executeQuery("SHOW DATABASES");
            while(rs.next()){
                options.add(rs.getString(1));
                System.out.println(rs.getString(1));
            }
            datenbankAuswahl.setItems(options);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void auswahlDatenbank(ActionEvent event) {
        try {
            con.setCatalog(datenbankAuswahl.getValue().toString());
        } catch (SQLException e) {
            fehlerAusgabe.setText(e.getMessage());
        }
    }

    @FXML
    void onAbfrageButtonClick(ActionEvent event) {
        fehlerAusgabe.setText("");
        try {
            ResultSet rs = con.createStatement().executeQuery(abfrageEingabe.getText());
            parseResultSetToTableView(rs);
        } catch (SQLException e) {
            fehlerAusgabe.setText(e.getMessage());
        }
    }

    private void parseResultSetToTableView(ResultSet resSet) {
        List<String> data = new ArrayList<>();

        try {
            ResultSetMetaData resSetMetDat = resSet.getMetaData();
            int col = resSetMetDat.getColumnCount();
            while (resSet.next()){
                for (int i = 1; i <= col; i++) {
                    data.add(resSet.getString(i));
                }
            }

            ObservableList<String> observableList = FXCollections.observableArrayList(data);
            ausgabeTableErgebnis.setItems(observableList);
            for (int i = 1; i <= col; i++) {
                ausgabeTableErgebnis.getColumns().add(new TableColumn<>(resSetMetDat.getColumnName(i)).setCellValueFactory());
            }
        } catch (SQLException e){
            fehlerAusgabe.setText(e.getMessage());
        }
    }

    @FXML
    void onBeendenButtonClick(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onResetButtonClick(ActionEvent event) {
        abfrageEingabe.clear();
        ausgabeTableErgebnis.getItems().clear();
        ausgabeTableErgebnis.getColumns().clear();
        fehlerAusgabe.setText("");
    }
}
