package com.albalatro.controller;

import com.albalatro.model.Empleado;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.UUID; // Para generar ID único

public class CrearEmpleadoController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoP;
    @FXML private TextField txtApellidoM;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;

    @FXML
    public void initialize() {
        // Navegación con Enter, QoL
        txtNombre.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                txtApellidoP.requestFocus();
        });

        txtApellidoP.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                txtApellidoM.requestFocus();
        });

        txtApellidoM.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                //Suponiendo que llenó todos los campos, se hace el intento por guardar. De todas formas, si falta algo, se mostrará el error correspondiente.
                btnGuardar.fire();
        });
    }

    @FXML
    public void guardar() {
        try {
            String nombre = txtNombre.getText();
            String apellidoP = txtApellidoP.getText();
            String apellidoM = txtApellidoM.getText();

            if (nombre.isEmpty() || apellidoP.isEmpty() || apellidoM.isEmpty()) {
                mostrarError("Por favor llena todos los campos.");
                return;
            }

            Empleado nuevoEmpleado = new Empleado();
            nuevoEmpleado.setId(UUID.randomUUID().toString());
            nuevoEmpleado.setNombre(nombre);
            nuevoEmpleado.setApellidoP(apellidoP);
            nuevoEmpleado.setApellidoM(apellidoM);

            ArrayList<Empleado> listaActual = JSONService.readWorkers();
            
            listaActual.add(nuevoEmpleado);
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