package com.albalatro.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import com.albalatro.model.Pago;
import com.albalatro.model.Salario;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

public class SalarioController implements Initializable{
    @FXML private Label labelNormal, labelDomingo;
    @FXML private TextField fieldNombre;
    @FXML private RadioButton choiceHora, choiceFijo;
    @FXML private CheckBox choiceTodos;
    @FXML private Spinner<Double> spinnerNormal, spinnerDomingo;
    @FXML private Button btnGuardar, btnCancelar;
    private Salario salario;
    private String motivo;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
        SpinnerValueFactory<Double> valueFactory = 
        new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 999.99);
        
        valueFactory.setValue(40.0);
        spinnerNormal.setValueFactory(valueFactory);

        valueFactory.setValue(50.0);
        spinnerDomingo.setValueFactory(valueFactory);
        
    }
    
    public void cargarDatos(Salario salario, String motivo) {
        this.salario = salario;
        
        if (salario != null) {
            switch (salario.getPago()) {
                case Pago.HORA -> choiceHora.setSelected(true);
                case Pago.FIJO -> choiceFijo.setSelected(true);
            }
            fieldNombre.setText(salario.getNombre());
            
            if(salario.getNormal().equals(salario.getDomingo())) {
                choiceTodos.setSelected(true);
            }
            
            SpinnerValueFactory<Double> valueFactory = 
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 999.99);
            valueFactory.setValue(salario.getNormal());
            spinnerNormal.setValueFactory(valueFactory);
            
            valueFactory.setValue(salario.getDomingo());
            spinnerDomingo.setValueFactory(valueFactory);
        } else {
            salario = new Salario();
            salario.setId(UUID.randomUUID().toString());
            
            salario.setPago(Pago.HORA);
            choiceHora.setSelected(true);

            salario.setNombre("Nuevo salario");
            fieldNombre.setText(salario.getNombre());

            salario.setNormal(spinnerNormal.getValue());
            salario.setDomingo(spinnerDomingo.getValue());
        }
        spinnerShow();
        
    }
    
    
    public void setPago(ActionEvent event) {
        if(choiceHora.isSelected()) {
            salario.setPago(Pago.HORA);
        } 
        else if(choiceFijo.isSelected()) {
            salario.setPago(Pago.FIJO);
        }
    }
    
    @FXML
    public void setDias(ActionEvent event) {
        spinnerShow();
    }
    
    private void spinnerShow(){
        if(choiceTodos.isSelected()) {
            labelNormal.setText("Todos los días");
            labelDomingo.setVisible(false);
            spinnerDomingo.setVisible(false);
        } else if(! choiceTodos.isSelected()) {
            labelNormal.setText("Entre semana");
            labelDomingo.setVisible(true);
            spinnerDomingo.setVisible(true);
            
        }
    }
    
    public void guardar() {
        salario.setNombre(fieldNombre.getText());

        salario.setNormal(spinnerNormal.getValue());
        
        if(choiceTodos.isSelected()) 
            salario.setDomingo(spinnerNormal.getValue());
        else
            salario.setDomingo(spinnerDomingo.getValue()); 
        
    }

    public void cancelar() {
        
    }
}
