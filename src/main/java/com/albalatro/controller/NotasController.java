package com.albalatro.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class NotasController {
    
    @FXML private Button btnGuardar;
    
    @FXML private TextArea txtObservacion;
    
        
    
    @FXML
    public void initialize() {
        
        // Configurar eventos
        configurarEventos();
    }
    
    private void configurarEventos() {
        // Detectar cambios para habilitar guardar
        // datePickerFecha.valueProperty().addListener((obs, oldVal, newVal) -> {
        //     if (indiceActual >= 0 && indiceActual < observaciones.size()) {
        //         estaEditando = true;
        //         btnGuardar.setDisable(false);
        //     }
        // });
        
        // txtObservacion.textProperty().addListener((obs, oldVal, newVal) -> {
        //     if (indiceActual >= 0 && indiceActual < observaciones.size()) {
        //         estaEditando = true;
        //         btnGuardar.setDisable(false);
        //     }
        // });
    }
    
    @FXML
    public void guardarCambios() {
        // Validar que haya texto
        if (txtObservacion.getText() == null || txtObservacion.getText().trim().isEmpty()) {
            mostrarError("El texto de la observación es obligatorio");
            return;
        }
        
        
    }
    
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}