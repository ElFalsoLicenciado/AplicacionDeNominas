package com.albalatro.controller;

import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;
import com.albalatro.utils.Utils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainController {
    
    @FXML private BorderPane mainContainer;
    @FXML private Button btnAtras;
    @FXML private Button btnGuardarCambios;
    
    @FXML
    public void initialize() {
        Navigation.setMainComponents(mainContainer, btnAtras);
        Navigation.cambiarVista("/View/HomeView.fxml");
    }
    
    @FXML
    public void regresar() {
        Navigation.goBack();
    }
    
    @FXML
    public void saveToOriginalJSON() {
        JSONService.saveChanges();
        Session.setChanges(false);
        Utils.showAlert("Cambios guardados", null, "Los cambios se han guardado correctamente", Alert.AlertType.INFORMATION);
    }

    // --- NUEVO MÉTODO PARA MOSTRAR CRÉDITOS ---
    @FXML
    public void showAbout() {
        Utils.showAlert(
            "Acerca del Proyecto", 
            "Créditos de Desarrollo", 
            "Alberto Montoya Arriaga - chopytrollface@gmail.com\n" +
            "Cándido Ortega Martínez - iowosyse@gmail.com\n\n", 
            Alert.AlertType.INFORMATION
        );
    }
}