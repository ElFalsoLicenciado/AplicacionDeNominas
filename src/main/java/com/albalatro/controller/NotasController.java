package com.albalatro.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

import com.albalatro.model.Empleado;
import com.albalatro.model.Observacion;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class NotasController {
    
    @FXML private Button btnPrevious;
    @FXML private Button btnNext;
    @FXML private Button btnNuevo;
    @FXML private Button btnBorrar;
    @FXML private Button btnGuardar;
    
    @FXML private DatePicker datePickerFecha;
    @FXML private TextArea txtObservacion;
    @FXML private Label lblContador;
    @FXML private Label lblSinObservaciones;
    
    private Empleado empleadoActual;
    private ArrayList<Observacion> observaciones;
    private int indiceActual = 0;
    private boolean estaEditando = false;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "ES"));
    
    @FXML
    public void initialize() {
        empleadoActual = Session.getEmpleadoSeleccionado();
        observaciones = empleadoActual.getObservaciones();
        
        // Configurar eventos
        configurarEventos();
        
        // Mostrar primera observación o estado vacío
        if (!observaciones.isEmpty()) {
            mostrarObservacion();
        } else {
            mostrarEstadoVacio();
        }
        
        actualizarBotonesNavegacion();
    }
    
    private void configurarEventos() {
        // Detectar cambios para habilitar guardar
        datePickerFecha.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (indiceActual >= 0 && indiceActual < observaciones.size()) {
                estaEditando = true;
                btnGuardar.setDisable(false);
            }
        });
        
        txtObservacion.textProperty().addListener((obs, oldVal, newVal) -> {
            if (indiceActual >= 0 && indiceActual < observaciones.size()) {
                estaEditando = true;
                btnGuardar.setDisable(false);
            }
        });
    }
    
    private void mostrarObservacion() {
        if (indiceActual >= 0 && indiceActual < observaciones.size()) {
            Observacion obs = observaciones.get(indiceActual);
            
            datePickerFecha.setValue(obs.getFecha());
            txtObservacion.setText(obs.getTexto());
            
            lblContador.setText(String.format("Observación %d de %d", 
            indiceActual + 1, observaciones.size()));
            
            // Ocultar indicador de vacío y mostrar controles
            lblSinObservaciones.setVisible(false);
            datePickerFecha.setDisable(false);
            txtObservacion.setDisable(false);
            
            // Resetear estado de edición
            estaEditando = false;
            btnGuardar.setDisable(true);
        }
    }
    
    private void mostrarEstadoVacio() {
        lblSinObservaciones.setVisible(true);
        datePickerFecha.setDisable(true);
        txtObservacion.setDisable(true);
        datePickerFecha.setValue(null);
        txtObservacion.setText("");
        lblContador.setText("No hay observaciones");
        btnBorrar.setDisable(true);
        btnGuardar.setDisable(true);
    }
    
    private void actualizarBotonesNavegacion() {
        btnPrevious.setVisible(! (observaciones.isEmpty() || indiceActual == 0));
        btnNext.setVisible(! (observaciones.isEmpty() || indiceActual == observaciones.size() - 1));
        btnBorrar.setDisable(observaciones.isEmpty());
    }
    
    @FXML
    private void anterior() {
        if (estaEditando) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Cambios sin guardar");
            alert.setHeaderText("Tiene cambios sin guardar");
            alert.setContentText("¿Desea guardar los cambios antes de navegar?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                guardarCambios();
            }
        }
        
        if (indiceActual > 0) {
            indiceActual--;
            mostrarObservacion();
            actualizarBotonesNavegacion();
        }
    }
    
    @FXML
    private void siguiente() {
        if (estaEditando) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Cambios sin guardar");
            alert.setHeaderText("Tiene cambios sin guardar");
            alert.setContentText("¿Desea guardar los cambios antes de navegar?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                guardarCambios();
            }
        }
        
        if (indiceActual < observaciones.size() - 1) {
            indiceActual++;
            mostrarObservacion();
            actualizarBotonesNavegacion();
        }
    }
    
    @FXML
    public void nuevo() {
        // Crear nueva observación con fecha actual
        Observacion nuevaObservacion = new Observacion();
        nuevaObservacion.setFecha(LocalDate.now());
        nuevaObservacion.setTexto("");
        
        observaciones.add(nuevaObservacion);
        indiceActual = observaciones.size() - 1;
        
        mostrarObservacion();
        actualizarBotonesNavegacion();
        
        // Enfocar el área de texto
        txtObservacion.requestFocus();
    }
    
    @FXML
    public void borrar() {
        if (indiceActual >= 0 && indiceActual < observaciones.size()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Eliminar esta observación?");
            alert.setContentText("Esta acción no se puede deshacer.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                observaciones.remove(indiceActual);
                
                if (observaciones.isEmpty()) {
                    mostrarEstadoVacio();
                    indiceActual = 0;
                } else if (indiceActual >= observaciones.size()) {
                    indiceActual = observaciones.size() - 1;
                    mostrarObservacion();
                } else {
                    mostrarObservacion();
                }
                
                actualizarBotonesNavegacion();
                
                // Marcar que hay cambios pendientes de guardar en el empleado
                empleadoActual.setObservaciones(observaciones);
            }
        }
    }
    
    @FXML
    public void guardarCambios() {
        if (indiceActual >= 0 && indiceActual < observaciones.size()) {
            Observacion obs = observaciones.get(indiceActual);
            
            // Validar que haya fecha
            if (datePickerFecha.getValue() == null) {
                mostrarError("La fecha es obligatoria");
                return;
            }
            
            // Validar que haya texto
            if (txtObservacion.getText() == null || txtObservacion.getText().trim().isEmpty()) {
                mostrarError("El texto de la observación es obligatorio");
                return;
            }
            
            // Actualizar observación
            obs.setFecha(datePickerFecha.getValue());
            obs.setTexto(txtObservacion.getText().trim());
            
            // Actualizar en la lista
            observaciones.set(indiceActual, obs);
            
            // Actualizar en el empleado
            empleadoActual.setObservaciones(observaciones);
            
            // Resetear estado de edición
            estaEditando = false;
            btnGuardar.setDisable(true);
            
            
            ArrayList<Empleado> listaActualizada = JSONService.readWorkersEdit();
            
            for(int i=0; i<listaActualizada.size(); i++) {
                if(listaActualizada.get(i).getId().equals(empleadoActual.getId())) {
                    listaActualizada.set(i, empleadoActual);
                    break;
                }                
            }
            if(JSONService.writeWorkersEdit(listaActualizada)) 
                mostrarExito("Observación guardada correctamente");
        }
    }
    
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // Método para obtener las observaciones actualizadas
    public ArrayList<Observacion> getObservacionesActualizadas() {
        return observaciones;
    }
}