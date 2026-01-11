package com.albalatro.controller;

import java.util.ArrayList;

import com.albalatro.model.Empleado;
import com.albalatro.model.Salario;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;
import com.albalatro.utils.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

public class SelectorSalarioController {
    
    @FXML private ChoiceBox<Salario> choiceBoxSalario;
    @FXML private Button btnNuevo, btnBorrar, btnAceptar, btnCancelar;
    private ArrayList<Salario> salariosDisponibles = new ArrayList<>();
    private Salario salarioSeleccionado; 
    private Empleado emp;
    
    @FXML public void initialize() {
        emp =  Session.getEmpleadoSeleccionado();
        
        //Obtener salario base
        salariosDisponibles.add(JSONService.getSalario("BASE"));
        
        
        //Obtener salario individual
        if (! emp.getSalario().equals("BASE"))
            salariosDisponibles.add(JSONService.getSalario(emp.getSalario()));
        
        //Obtener salario custom
        if( (!Session.getSalarioSeleccionado().getId().equals("BASE") 
            && !Session.getSalarioSeleccionado().getId().equals(emp.getSalario()))) {
            salariosDisponibles.add(Session.getSalarioSeleccionado());
        }
        
        choiceBoxSalario.getItems().addAll(salariosDisponibles);
        choiceBoxSalario.setOnAction(this::seleccionarSalario);
        choiceBoxSalario.setValue(Session.getSalarioSeleccionado());
    }
    
    @FXML public void nuevoSalario() {
        Salario nuevo = new Salario();

        salariosDisponibles.add(nuevo);

        choiceBoxSalario.getItems().clear();
        choiceBoxSalario.getItems().addAll(salariosDisponibles);
    }
    
    @FXML public void borrarSalario() {
        if(salarioSeleccionado.getId().equals("BASE")) {
            Utils.showAlert("Alerta", "No se puede borrar el salario base.", "", AlertType.WARNING);
        }

        salariosDisponibles.remove(salarioSeleccionado);

        choiceBoxSalario.getItems().clear();
        choiceBoxSalario.getItems().addAll(salariosDisponibles);

        salarioSeleccionado = null;
    }
    
    @FXML public void seleccionarSalario(ActionEvent event) {
        salarioSeleccionado = choiceBoxSalario.getValue();
        
        if(salarioSeleccionado.getId().equals("BASE")) {
            btnBorrar.setVisible(false);
            btnNuevo.setVisible(true);
        } else if (salarioSeleccionado.getId().equals(emp.getSalario())) {
            btnBorrar.setVisible(true);
            btnNuevo.setVisible(true);
        } else if (salarioSeleccionado.getId().equals("custom")) {
            btnBorrar.setVisible(true);
            btnNuevo.setVisible(false);
        }
    }
    
    @FXML public void aceptar() {
        Session.setSalarioSeleccionado(salarioSeleccionado);
        Navigation.goBack();
    }
    
    @FXML public void cancelar() { Navigation.goBack(); }
    
}
