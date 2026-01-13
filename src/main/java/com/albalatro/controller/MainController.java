package com.albalatro.controller;

import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML private BorderPane mainContainer;
    @FXML private Button btnAtras;
    @FXML private Button btnGuardarCambios;

    @FXML
    public void initialize() {
        // 1. Configuramos el sistema de navegación
        Navigation.setMainComponents(mainContainer, btnAtras);
        
        // 2. Cargamos la primera vista automáticamente (El Dashboard o Menú)
        // Nota: Asegúrate de tener creado este FXML
        Navigation.cambiarVista("/View/HomeView.fxml");
    }

    @FXML
    public void regresar() {
        Navigation.goBack();
    }

    @FXML
    public void saveToOriginalJSON() {
        JSONService.saveChanges();
    }
}