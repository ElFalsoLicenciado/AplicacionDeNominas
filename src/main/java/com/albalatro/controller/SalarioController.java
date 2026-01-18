package com.albalatro.controller;

import java.util.ArrayList;
import java.util.UUID;

import com.albalatro.model.Salario;
import com.albalatro.model.TipoPago;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SalarioController {
    @FXML private Label labelNormal, labelDomingo;
    @FXML private TextField fieldNombre;
    @FXML private RadioButton choiceHora, choiceFijo;
    @FXML private CheckBox choiceTodos;
    @FXML private Spinner<Double> spinnerNormal, spinnerDomingo;
    @FXML private Button btnGuardar;
    
    private Salario salario;
    private boolean modal = false; 
    
    @FXML
    public void initialize() {
        SpinnerValueFactory<Double> valueFactory1 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 999.99);
        
        valueFactory1.setValue(40.0);
        spinnerNormal.setValueFactory(valueFactory1);
        
        SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 999.99);
        
        valueFactory2.setValue(50.0);
        spinnerDomingo.setValueFactory(valueFactory2);
        
        cargarDatos();
    }
    
    public void cargarDatos() {
        this.salario = Session.getSalarioSeleccionado();
        
        if (salario != null) {
            switch (salario.getPago()) {
                case TipoPago.HORA -> choiceHora.setSelected(true);
                case TipoPago.FIJO -> choiceFijo.setSelected(true);
            }
            fieldNombre.setText(salario.getNombre());
            
            if (salario.getNormal().equals(salario.getDomingo())) {
                choiceTodos.setSelected(true);
            }
            
            SpinnerValueFactory<Double> valueFactory1 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 999.99);
            valueFactory1.setValue(salario.getNormal());
            spinnerNormal.setValueFactory(valueFactory1);
            
            SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 999.99);
            valueFactory2.setValue(salario.getDomingo());
            spinnerDomingo.setValueFactory(valueFactory2);
            custom();
        } else {
            salario = new Salario();
            salario.setId(UUID.randomUUID().toString());
            
            salario.setPago(TipoPago.HORA);
            choiceHora.setSelected(true);
            
            salario.setNombre("Nuevo salario");
            fieldNombre.setText(salario.getNombre());
            
            salario.setNormal(spinnerNormal.getValue());
            salario.setDomingo(spinnerDomingo.getValue());
        }
        spinnerShow();
    }
    
    public void setPago(ActionEvent event) {
        if (choiceHora.isSelected()) {
            salario.setPago(TipoPago.HORA);
        } else if (choiceFijo.isSelected()) {
            salario.setPago(TipoPago.FIJO);
        }
    }
    
    private void custom() {
        if(salario.getId().equals("custom")) {
            labelDomingo.setVisible(false);
            spinnerDomingo.setVisible(false);
            choiceTodos.setSelected(true);
            choiceTodos.setVisible(false);
        }
    } 
    
    @FXML
    public void setDias(ActionEvent event) {
        spinnerShow();
    }
    
    private void spinnerShow() {
        if (choiceTodos.isSelected()) {
            labelNormal.setText("Todos los días");
            labelDomingo.setVisible(false);
            spinnerDomingo.setVisible(false);
        } else if (!choiceTodos.isSelected()) {
            labelNormal.setText("Entre semana");
            labelDomingo.setVisible(true);
            spinnerDomingo.setVisible(true);
        }
    }
    
    @FXML
    private void cancelar() { 
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        if (this.modal) {
            // MODO MODAL: Solo cerrar la ventanita actual
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } else {
            // MODO NAVEGACIÓN: Volver a la pantalla anterior (Lista de Salarios)
            Navigation.cambiarVista("/View/ListaSalariosView.fxml");
        }
    }
    
    public void guardar() {
        salario.setNombre(fieldNombre.getText());
        
        salario.setNormal(spinnerNormal.getValue());
        
        TipoPago pago = choiceHora.isSelected() ? TipoPago.HORA : TipoPago.FIJO;
        salario.setPago(pago);
        
        if (choiceTodos.isSelected())
            salario.setDomingo(spinnerNormal.getValue());
        else
            salario.setDomingo(spinnerDomingo.getValue());
        
        Session.setSalarioSeleccionado(salario);
        
        if (!salario.getId().equals("custom")) {
            ArrayList<Salario> salarios = JSONService.readWagesEdit();
            
            boolean found = false;
            
            for (Salario w : salarios) {
                if (w.getId().equals(salario.getId())) {
                    salarios.set(salarios.indexOf(w), salario);
                    found = true;
                    break;
                }
            }
            
            if (!found)
                salarios.add(salario);
            
            JSONService.writeWagesEdit(salarios);
            Session.setChanges(true);
        }
        
        cerrarVentana();
    }
    
    public void setEsModal(boolean modal) {
        this.modal = modal;
    }
}