package com.albalatro.controller;

import java.time.LocalDate;
import java.util.ArrayList;

import com.albalatro.model.Corte;
import com.albalatro.model.Empleado;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Session;
import com.albalatro.utils.Utils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class HistorialPagosController {

    @FXML private TableView<Corte> tablaCortes;
    @FXML private TableColumn<Corte, LocalDate> colInicio;
    @FXML private TableColumn<Corte, LocalDate> colFin;
    @FXML private TableColumn<Corte, String> colMonto;

    private Empleado empleado;
    private Runnable onCambioRealizado; 

    @FXML
    public void initialize() {
        empleado = Session.getEmpleadoSeleccionado();
        
        colInicio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getInicio()));
        colFin.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFin()));
        colMonto.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getMonto())));

        cargarTabla();
    }

    private void cargarTabla() {
        if (empleado.getHistorialPagos() != null) {
            tablaCortes.setItems(FXCollections.observableArrayList(empleado.getHistorialPagos()));
            if (!empleado.getHistorialPagos().isEmpty()) {
                tablaCortes.scrollTo(empleado.getHistorialPagos().size() - 1);
            }
        }
    }

    @FXML
    public void eliminarUltimoCorte() {
        ArrayList<Corte> historial = empleado.getHistorialPagos();
        
        if (historial == null || historial.isEmpty()) {
            Utils.showAlert("Aviso", "No hay cortes registrados para eliminar.", "", Alert.AlertType.WARNING);
            return;
        }

        Corte ultimoCorte = historial.get(historial.size() - 1);

        boolean confirmar = Utils.showAlert("¿Eliminar Corte?", 
            "Estás a punto de eliminar el corte del " + ultimoCorte.getInicio() + " al " + ultimoCorte.getFin() + ".",
            "Esto revertirá el estado del empleado al periodo anterior.", 
            Alert.AlertType.CONFIRMATION);

        if (confirmar) {
            // 1. Eliminar de la lista
            historial.remove(historial.size() - 1);
            
            // 2. Revertir las fechas del empleado (AQUÍ ESTABA EL ERROR)
            if (historial.isEmpty()) {
                // Si no quedan cortes, limpiamos ambas fechas
                empleado.setFinCorte(null); 
                empleado.setInicioCorte(null); // CORRECCIÓN: Limpiar también el inicio
            } else {
                // Si quedan cortes, restauramos las fechas del penúltimo corte
                Corte penultimo = historial.get(historial.size() - 1);
                empleado.setFinCorte(penultimo.getFin());
                empleado.setInicioCorte(penultimo.getInicio()); // CORRECCIÓN: Restaurar el inicio correcto
            }

            // 3. Guardar cambios
            guardarEmpleado();
            
            Utils.showAlert("Éxito", "Corte eliminado. El calendario se ha actualizado.", "", Alert.AlertType.INFORMATION);
            cargarTabla();
            
            if (onCambioRealizado != null) onCambioRealizado.run();
        }
    }

    private void guardarEmpleado() {
        ArrayList<Empleado> listaEmpleados = JSONService.readWorkersEdit();
        for (int i = 0; i < listaEmpleados.size(); i++) {
            if (listaEmpleados.get(i).getId().equals(empleado.getId())) {
                listaEmpleados.set(i, empleado);
                break;
            }
        }
        JSONService.writeWorkersEdit(listaEmpleados);
        Session.setChanges(true);
    }

    @FXML
    public void cerrar() {
        Stage stage = (Stage) tablaCortes.getScene().getWindow();
        stage.close();
    }

    public void setOnCambioRealizado(Runnable onCambioRealizado) {
        this.onCambioRealizado = onCambioRealizado;
    }
}