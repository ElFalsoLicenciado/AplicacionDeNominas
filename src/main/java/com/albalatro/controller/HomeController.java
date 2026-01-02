package com.albalatro.controller;

import com.albalatro.utils.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class HomeController {

    @FXML
    public void initialize() {
        Navigation.clearHistory();
    }

    @FXML
    void irACrear(ActionEvent event) {
        Navigation.cambiarVista("/View/CrearEmpleadoView.fxml");
    }

    @FXML
    void irAVer(ActionEvent event) {
        Navigation.cambiarVista("/View/ListaEmpleadosView.fxml");
    }
}