package com.albalatro.controller;

import com.albalatro.utils.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class HomeController {

    @FXML
    public void initialize() {
        // ALERTA: Esto es lo que soluciona tu problema.
        // Al entrar al Home, borramos el historial para eliminar el rastro
        // de la pantalla "Cargando..." y ocultar el botón "Atrás".
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