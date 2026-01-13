package com.albalatro.controller;

import java.util.ArrayList;

import com.albalatro.model.Empleado;
import com.albalatro.model.Salario;
import com.albalatro.model.TipoPago;
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
    @FXML private Button btnNuevo, btnBorrar, btnEditar, btnAceptar, btnCancelar;
    private ArrayList<Salario> listaSalarios = new ArrayList<>();
    private Salario salarioSeleccionado;
    private Empleado emp;
    private boolean salarioIndividual, salarioCustom = false;
    private String casoSalario;

    @FXML
    public void initialize() {
        emp = Session.getEmpleadoSeleccionado();
        casoSalario = Session.getSalarioString();

        // Obtener salario base
        listaSalarios.add(JSONService.getSalario("BASE"));

        // Obtener salario individual
        if (JSONService.getSalario(emp.getSalario()) != null) {
            listaSalarios.add(JSONService.getSalario(emp.getSalario()));
            salarioIndividual = true;
        }

        // Obtener salario custom
        if ((!Session.getSalarioSeleccionado().getId().equals("BASE")
                && !Session.getSalarioSeleccionado().getId().equals(emp.getSalario()))) {
            listaSalarios.add(Session.getSalarioSeleccionado());
            salarioCustom = true;
        }

        choiceBoxSalario.getItems().addAll(listaSalarios);
        choiceBoxSalario.setOnAction(this::seleccionarSalario);
        choiceBoxSalario.setValue(Session.getSalarioSeleccionado());
    }

    @FXML
    public void nuevoSalario() {
        Salario nuevoSalario = new Salario();

        switch (Session.getSalarioString()) {
            case "EMPLEADO" -> {
                nuevoSalario.setId(emp.getId());
                nuevoSalario.setNombre("Salario de " + emp.getNombreCompleto());
                salarioIndividual = true;
            }

            case "CUSTOM" -> {
                nuevoSalario.setId("custom");
                nuevoSalario.setNombre("Nuevo salario");
                salarioCustom = true;
            }
        }

        nuevoSalario.setPago(TipoPago.HORA);
        nuevoSalario.setNormal(40.0);
        nuevoSalario.setDomingo(50.0);

        listaSalarios.add(nuevoSalario);

        choiceBoxSalario.getItems().clear();
        choiceBoxSalario.getItems().addAll(listaSalarios);
    }

    @FXML
    public void borrarSalario() {
        if (salarioSeleccionado.getId().equals("BASE")) {
            Utils.showAlert("Alerta", "No se puede borrar el salario base.", "", AlertType.WARNING);
            return;
        }

        Salario nuevoSalarioSeleccionado = listaSalarios.get(listaSalarios.indexOf(salarioSeleccionado) - 1);
        listaSalarios.remove(salarioSeleccionado);

        switch (salarioSeleccionado.getId()) {
            case "custom" -> salarioCustom = false;

            default -> salarioIndividual = false;
        }

        choiceBoxSalario.getItems().clear();
        choiceBoxSalario.getItems().addAll(listaSalarios);
        choiceBoxSalario.setValue(nuevoSalarioSeleccionado);

        salarioSeleccionado = nuevoSalarioSeleccionado;
    }

    @FXML
    public void editarSalario() {
        Session.setSalarioSeleccionado(salarioSeleccionado);
        Navigation.cambiarVista("/View/SalarioView.fxml");
    }

    @FXML
    public void seleccionarSalario(ActionEvent event) {
        salarioSeleccionado = choiceBoxSalario.getValue();

        if (salarioSeleccionado.getId().equals("BASE")) {
            btnBorrar.setVisible(false);
            btnNuevo.setVisible(!salarioIndividual || !salarioCustom);
            btnEditar.setVisible(false);
        } else if (salarioSeleccionado.getId().equals(emp.getSalario())) {
            btnBorrar.setVisible(true);
            btnNuevo.setVisible(!salarioCustom && casoSalario.equals("CUSTOM"));
            btnEditar.setVisible(true);
        } else if (salarioSeleccionado.getId().equals("custom")) {
            btnBorrar.setVisible(true);
            btnNuevo.setVisible(!salarioIndividual && casoSalario.equals("EMPLEADO"));
            btnEditar.setVisible(true);
        }
    }

    @FXML
    public void aceptar() {
        Session.setSalarioSeleccionado(salarioSeleccionado);
        Navigation.goBack();
    }

    @FXML
    public void cancelar() {
        Navigation.goBack();
    }
}
