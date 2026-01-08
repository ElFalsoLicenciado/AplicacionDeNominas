package com.albalatro.controller;

import java.net.URL;
import java.util.ResourceBundle;

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

public class SalarioController implements Initializable{
    @FXML private Label labelNormal, labelDomingo;
    @FXML private CheckBox choiceTodos;
    @FXML private RadioButton choiceHora, choiceFijo;
    @FXML private Spinner<Double> spinnerNormal, spinnerDomingo;
    @FXML private Button btnGuardar, btnCancelar;
    private Salario salario;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
        SpinnerValueFactory<Double> valueFactory = 
        new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 999.99);
        
        valueFactory.setValue(40.0);
        
        spinnerNormal.setValueFactory(valueFactory);
        spinnerDomingo.setValueFactory(valueFactory);
        
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

    }

    public void cancelar() {
        
    }
}
