package com.albalatro.controller;

import com.albalatro.model.Empleado;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ListaEmpleadosController {

    @FXML private VBox vboxLista;
    @FXML private Label lblSinEmpleados;
    @FXML private ScrollPane scrollContainer;

    @FXML
    public void initialize() {
        ArrayList<Empleado> empleados = JSONService.readWorkers();

        if (empleados.isEmpty()) {
            lblSinEmpleados.setVisible(true);
            scrollContainer.setVisible(false);
        } else {
            lblSinEmpleados.setVisible(false);
            scrollContainer.setVisible(true);
            
            //Generar botones dinámicamente
            for (Empleado emp : empleados) {
                Button btn = crearBotonEmpleado(emp);
                vboxLista.getChildren().add(btn);
            }
        }
    }

    private Button crearBotonEmpleado(Empleado emp) {
        Button btn = new Button(emp.getNombreCompleto());
        
        btn.setMaxWidth(Double.MAX_VALUE); // Ocupar todo el ancho
        btn.setPrefHeight(60.0);
        btn.setAlignment(Pos.CENTER_LEFT); // Texto a la izquierda
        // CSS en línea para diseño moderno
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0 0 0 20;" // Padding izquierdo para el texto
        );

        // Efecto Hover (cambiar color al pasar el mouse)
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #e3f2fd;" + // Azul muy claro
            "-fx-border-color: #2196F3;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0 0 0 20;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0 0 0 20;"
        ));

        btn.setOnAction(event -> {
            // Para pasar el empleado seleccionado a la siguiente vista
            Session.setEmpleadoSeleccionado(emp);
            
            System.out.println("Seleccionado: " + emp.getNombreCompleto());
            System.out.println("Hola");
            Navigation.cambiarVista("/View/CalendarioView.fxml");
        });
        return btn;
    }
}