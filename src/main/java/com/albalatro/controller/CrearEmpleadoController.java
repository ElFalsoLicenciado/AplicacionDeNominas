package com.albalatro.controller;

import java.util.ArrayList;
import java.util.UUID;

import com.albalatro.model.Empleado;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class CrearEmpleadoController {
    
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoP;
    @FXML private TextField txtApellidoM;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;
    private Empleado empleado;
    
    @FXML
    public void initialize() {
        empleado = Session.getEmpleadoSeleccionado();
        
        if(empleado != null) {
            txtNombre.setText(empleado.getNombre());
            txtApellidoM.setText(empleado.getApellidoM());
            txtApellidoP.setText(empleado.getApellidoP());
        }
        
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
            ArrayList<Empleado> listaActual = JSONService.readWorkers();
            Empleado target;
            
            if (nombre.isEmpty() || apellidoP.isEmpty() || apellidoM.isEmpty()) {
                mostrarError("Por favor llena todos los campos.");
                return;
            }
            
            if (empleado == null) {
                System.out.println("Empleado null");
                Empleado nuevoEmpleado = new Empleado();
                nuevoEmpleado.setId(UUID.randomUUID().toString());
                setData(nuevoEmpleado, nombre, apellidoP, apellidoM);
                listaActual.add(nuevoEmpleado);
                target = nuevoEmpleado;
            } else {
                setData(empleado, nombre, apellidoP, apellidoM);
                boolean encontrado = false;

                for (int i = 0; i < listaActual.size(); i++) { // Buscar la coincidencia en la lista del JSON
                    if (listaActual.get(i).getId().equals(empleado.getId())) {
                        listaActual.set(i, empleado); // Reemplaza al empleado
                        encontrado = true;
                        break;
                    }
                }
                
                // Manejo de NullPoinerException
                if (!encontrado) {
                    mostrarError("No se encontró al empleado seleccionado");
                    return;
                }

                target = empleado;
            }
            
            boolean exito = JSONService.writeWorkers(listaActual);
            
            if (exito) {
                System.out.println("Empleado guardado con éxito.");
                Navigation.empleadoGuardadoCustomHistory(target);
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
    
    private void setData(Empleado emp, String nombre, String apellidoP, String apellidoM) {
        emp.setNombre(nombre);
        emp.setApellidoP(apellidoP);
        emp.setApellidoM(apellidoM);
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