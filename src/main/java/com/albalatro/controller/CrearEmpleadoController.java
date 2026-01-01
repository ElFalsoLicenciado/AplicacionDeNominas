package com.albalatro.controller;

import com.albalatro.model.Empleado;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.UUID; // Para generar ID único

public class CrearEmpleadoController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTarifa;
    @FXML private Label lblError;

    @FXML
    public void guardar() {
        try {
            // 1. Obtener datos
            String nombre = txtNombre.getText();
            String tarifaStr = txtTarifa.getText();

            // 2. Validaciones básicas
            if (nombre.isEmpty() || tarifaStr.isEmpty()) {
                mostrarError("Por favor llena todos los campos.");
                return;
            }

            // 3. Crear el objeto Empleado
            // Asumo que tu clase Empleado tiene un constructor así o setters
            Empleado nuevoEmpleado = new Empleado();
            nuevoEmpleado.setId(UUID.randomUUID().toString()); // Generamos ID único
            nuevoEmpleado.setNombre(nombre);
            // nuevoEmpleado.setHistorial(new HashMap<>()); // Si hace falta inicializar

            // 4. Guardar usando JSONService
            // PASO A: Leer los que ya existen
            ArrayList<Empleado> listaActual = JSONService.readWorkers();
            
            // PASO B: Agregar el nuevo
            listaActual.add(nuevoEmpleado);

            // PASO C: Guardar todo
            boolean exito = JSONService.writeWorkers(listaActual);

            if (exito) {
                System.out.println("Empleado guardado con éxito.");
                Navigation.goBack(); // Regresamos al menú
            } else {
                mostrarError("Error al escribir en el archivo JSON.");
            }

        } catch (NumberFormatException e) {
            mostrarError("La tarifa debe ser un número válido (ej. 50.0).");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Ocurrió un error inesperado.");
        }
    }

    @FXML
    public void cancelar() {
        Navigation.goBack();
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}