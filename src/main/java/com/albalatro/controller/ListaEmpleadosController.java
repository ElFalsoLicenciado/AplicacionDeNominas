package com.albalatro.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import com.albalatro.model.Empleado;
import com.albalatro.model.Status;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;
import com.albalatro.utils.Utils;
import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ListaEmpleadosController {
    
    @FXML private VBox vboxLista;
    @FXML private Label lblSinEmpleados;
    @FXML private ScrollPane scrollContainer;
    @FXML private Button btnCambiarStatus;
    @FXML private Button btnExportar;
    @FXML private Button btnImportar;
    private ArrayList<Empleado> empleados;
    private Status status;
    
    @FXML
    public void initialize() {
        empleados = JSONService.readWorkers();
        status = Status.ALTA;
        
        if (empleados.isEmpty()) {
            lblSinEmpleados.setVisible(true);
            scrollContainer.setVisible(false);
        } else {
            lblSinEmpleados.setVisible(false);
            scrollContainer.setVisible(true);
            
            //Generar botones dinámicamente
            mostrarEmpleados();
        }
    }
    
    private void mostrarEmpleados() {
        empleados = JSONService.readWorkers();
        vboxLista.getChildren().clear();
        for (Empleado emp : empleados) {
            if (emp.getStatus() == status) {
                Button btn = crearBotonEmpleado(emp);
                vboxLista.getChildren().add(btn);
            }
        }
    }
    
    @FXML
    public void cambiarStatus() {
        System.out.print("Status cambiado de " + status);
        status = (status == Status.ALTA) ? Status.BAJA : Status.ALTA;
        System.out.println(" a " + status);
        
        mostrarEmpleados();
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
            "-fx-cursor: hand;" +                "-fx-padding: 0 0 0 20;"
        ));
        
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +                "-fx-padding: 0 0 0 20;"
        ));
        
        btn.setOnAction(event -> {
            // Para pasar el empleado seleccionado a la siguiente vista
            Session.setEmpleadoSeleccionado(emp);
            
            System.out.println("Seleccionado: " + emp.getNombreCompleto());
            // System.out.println("Hola");
            Navigation.cambiarVista("/View/CalendarioView.fxml");
        });
        
        return btn;
    }
    
    @FXML
    private void importarJSON() {
        Stage stage = (Stage) btnImportar.getScene().getWindow(); 
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona un archivo JSON");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("JSON", "*.json"));
        
        File file = fileChooser.showOpenDialog(stage);
        
        if(file == null) {
            Utils.showAlert("Importación cancelada", "Se ha cancelado el proceso.", "", AlertType.WARNING);
            return;
        }
        
        ArrayList<Empleado> current = JSONService.readWorkers();
        ArrayList<Empleado> additions = JSONService.readWorkers(file.getPath());
        
        for(Empleado e : additions) {
            boolean found = false;
            for(int i = 0; i < current.size(); i++) {
                if(current.get(i).getId().equals(e.getId())) {
                    current.set(i, e);
                    found = true;
                    break;
                }
            }
            
            if(! found) current.add(e);
        }
        
        if(! Utils.showAlert("Confirmar", "¿Estás seguro?", "Se sobreescribará la información de empleados ya existentes.", AlertType.CONFIRMATION)) {
            
            if(JSONService.writeWorkers(current)) 
                Utils.showAlert("Archivo importado.", "Se ha importado correctamente los empleados.", "", AlertType.INFORMATION);
            
            else Utils.showAlert("No se pudo importar.", "Hubo un error al importar.", "", AlertType.ERROR);
            
            mostrarEmpleados();
            return;
        }
        Utils.showAlert("Importación cancelada", "Se ha cancelado el proceso.", "", AlertType.WARNING);
    }
    
    @FXML
    @SuppressWarnings("CallToPrintStackTrace")
    private void exportarJSON() {
        Stage stage = (Stage) btnExportar.getScene().getWindow();
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona un directorio para guardar");
        fileChooser.setInitialFileName("respaldo_esclavos_punto_jotason.json");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos JSON", "*json")
        );
        
        File file  = fileChooser.showSaveDialog(stage);
        
        if(file == null) {
            Utils.showAlert("Exportación cancelada", "Se ha cancelado el proceso.", "", AlertType.WARNING);
            return;
        }
               
        ArrayList<Empleado> current = JSONService.readWorkers();
        
        try (BufferedWriter bw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            bw.write("");
            Gson gson = JSONService.createGson();
            gson.toJson(current, bw);
            Utils.showAlert("Exportación exitosa", "Proceso terminado exitosamente.", "", AlertType.INFORMATION);
        }catch(IOException e) {
            e.printStackTrace();
            Utils.showAlert("No se pudo exportar.", "Hubo un error al exportar.", "", AlertType.ERROR);
        }
    }
    
    
}
