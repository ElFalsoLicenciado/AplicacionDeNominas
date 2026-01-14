package com.albalatro.controller;

import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class HomeController {

    @FXML
    public void initialize() {
        Navigation.clearHistory();
    }

    @FXML
    void irACrear(ActionEvent event) {
        // Nos evitamos de poner el null cada que retrocedemos, porque de todas formas se selecciona un nuevo empleado al cambiar a la vista del calendario
        Session.setEmpleadoSeleccionado(null);
        Navigation.cambiarVista("/View/CrearEmpleadoView.fxml");
    }

    @FXML
    void irAVer(ActionEvent event) {
        Navigation.cambiarVista("/View/ListaEmpleadosView.fxml");
    }

    @FXML
    void irSalario(ActionEvent event) {
        Navigation.cambiarVista("/View/ListaSalariosView.fxml");
    }
}