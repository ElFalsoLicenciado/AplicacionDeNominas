package com.albalatro.controller;

import java.util.ArrayList;
import java.util.UUID;

import com.albalatro.model.Empleado;
import com.albalatro.model.Status;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class CrearEmpleadoController {
    
    @FXML private TextField txtNombre, txtApellidoP, txtApellidoM;
    @FXML private Label lblError;
    @FXML private Button btnGuardar, btnAlta, btnBaja;
    private Empleado empleado;
    
    @FXML
    public void initialize() {
        empleado = Session.getEmpleadoSeleccionado();
        boolean isEdit = (empleado != null);

        if (isEdit) {
            txtNombre.setText(empleado.getNombre());
            txtApellidoP.setText(empleado.getApellidoP());
            txtApellidoM.setText(empleado.getApellidoM());
        }

        btnAlta.setVisible(isEdit && empleado.getStatus() == Status.BAJA);
        btnBaja.setVisible(isEdit && empleado.getStatus() == Status.ALTA);
        
        txtNombre.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) txtApellidoP.requestFocus(); });
        txtApellidoP.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) txtApellidoM.requestFocus(); });
        txtApellidoM.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) btnGuardar.fire(); });
    }
    
    @FXML
    public void guardar() {
        String n = txtNombre.getText(), ap = txtApellidoP.getText(), am = txtApellidoM.getText();
        if (n.isEmpty() || ap.isEmpty() || am.isEmpty()) {
            mostrarError("Por favor llena todos los campos.");
            return;
        }

        ArrayList<Empleado> lista = JSONService.readWorkersEdit();
        Empleado target = (empleado == null) ? new Empleado() : empleado;
        
        setData(target, n, ap, am);

        if (empleado == null) {
            target.setId(UUID.randomUUID().toString());
            lista.add(target);
        } else {
            actualizarEnLista(lista, target);
        }
        
        if (JSONService.writeWorkersEdit(lista)) Navigation.empleadoGuardadoCustomHistory(target);
        else mostrarError("Error al escribir en el archivo JSON.");
    }

    @FXML public void cancelar() { Navigation.goBack(); }

    @FXML public void darDeBaja() { 
        if (cambiarStatus(Status.BAJA)) {
            Navigation.irAListaEmpleados();
        } 
    }

    @FXML public void darDeAlta() { 
        if (cambiarStatus(Status.ALTA)) {
            Navigation.irAListaEmpleados();
        }
     }

    private boolean cambiarStatus(Status s) {
        empleado.setStatus(s);
        ArrayList<Empleado> lista = JSONService.readWorkersEdit();
        actualizarEnLista(lista, empleado);
        
        boolean exito = JSONService.writeWorkersEdit(lista);
        if (exito) System.out.println("Status actualizado a " + s);
        return exito;
    }

    private void actualizarEnLista(ArrayList<Empleado> lista, Empleado target) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId().equals(target.getId())) {
                lista.set(i, target);
                break;
            }
        }
    }
    
    private void setData(Empleado emp, String n, String ap, String am) {
        emp.setNombre(n);
        emp.setApellidoP(ap);
        emp.setApellidoM(am);
    }
    
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}