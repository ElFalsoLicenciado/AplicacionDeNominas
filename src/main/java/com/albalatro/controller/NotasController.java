package com.albalatro.controller;

import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class NotasController {
    
    @FXML private Button btnGuardar, btnBorrar;
    
    @FXML private TextArea txtObservacion;
    
    private String notas;
    private boolean modal = false;
        
    
    @FXML
    public void initialize() {
        notas = Session.getNotas();
        
        if (notas != null) {
            txtObservacion.setText(notas);
        }
    }
    
    @FXML
    public void guardarCambios() {
        // Validar que haya texto
        if (txtObservacion.getText() == null || txtObservacion.getText().trim().isEmpty()) {
            borrar();
            cerrarVentana();
            return;
        }
        notas = txtObservacion.getText();
        Session.setNotas(notas);
        cerrarVentana();
    }

    @FXML
    public void borrar() {
        notas = null;
        txtObservacion.setText("");
        Session.setNotas(notas);
    }

    private void cerrarVentana() {
        if (this.modal) {
            // MODO MODAL: Solo cerrar la ventanita actual
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } else {
            // MODO NAVEGACIÓN: Volver a la pantalla anterior (Lista de Salarios)
            Navigation.cambiarVista("/View/DetalleView.fxml");
        }
    }

    public void setEsModal(boolean modal) {
        this.modal = modal;
    }
}