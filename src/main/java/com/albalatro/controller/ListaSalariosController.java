package com.albalatro.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

import com.albalatro.model.Salario;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ListaSalariosController {

    @FXML private VBox vboxListaSalarios;
    @FXML private ScrollPane scrollContainer;
    @FXML private Button btnExportar;
    @FXML private Button btnImportar;
    
    // Referencia al nuevo botón
    @FXML private Button btnCambiarStatus;

    private ArrayList<Salario> salarios;
    private Status statusActual; // Variable para controlar el filtro

    @FXML
    public void initialize() {
        // Por defecto mostramos los activos (ALTA)
        statusActual = Status.ALTA;
        mostrarSalarios();
    }

    private void mostrarSalarios() {
        // 1. Limpiar la vista
        vboxListaSalarios.getChildren().clear();
        
        // 2. Leer datos frescos
        ArrayList<Salario> listado = JSONService.readWagesEdit();
        
        // 3. Filtrar y agregar botones
        for (Salario sal : listado) {
            
            // Protección contra Nulos (Legacy Support)
            Status s = sal.getStatus();
            if (s == null) s = Status.ALTA; 

            // Mostrar solo si coincide con el filtro actual
            if (s == statusActual) {
                Button btn = crearBotonSalario(sal);
                vboxListaSalarios.getChildren().add(btn);
            }
        }
    }
    
    @FXML
    public void cambiarStatus() {
        // Alternar entre ALTA y BAJA
        statusActual = (statusActual == Status.ALTA) ? Status.BAJA : Status.ALTA;
        
        // Actualizar texto del botón
        if (statusActual == Status.ALTA) {
            btnCambiarStatus.setText("Ver Salarios Inactivos");
        } else {
            btnCambiarStatus.setText("Ver Salarios Activos");
        }
        
        System.out.println("Cambiando vista de salarios a: " + statusActual);
        mostrarSalarios();
    }

    private Button crearBotonSalario(Salario sal) {
        Button btn = new Button(sal.getNombre());
        
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(60.0);
        btn.setAlignment(Pos.CENTER_LEFT);
        
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0 0 0 20;"
        );
        
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #e3f2fd;" + 
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
            irEditarSalario(sal);
        });
        
        return btn;
    }

    @FXML
    private void irCrearSalario() {
        Session.setSalarioSeleccionado(null);
        Navigation.cambiarVista("/View/SalarioView.fxml");
    }

    private void irEditarSalario(Salario salarioSeleccionado) {
        Session.setSalarioSeleccionado(salarioSeleccionado);
        Navigation.cambiarVista("/View/SalarioView.fxml");
    }

    @FXML
    private void regresar() {
        Navigation.cambiarVista("/View/HomeView.fxml");
    }

    // ==========================================
    // LÓGICA DE IMPORTACIÓN Y EXPORTACIÓN
    // ==========================================
    @FXML
    private void importarJSON() {
        Stage stage = (Stage) btnImportar.getScene().getWindow(); 
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona un archivo JSON de Salarios");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Archivo JSON (*.json)", "*.json"));
        
        File file = fileChooser.showOpenDialog(stage);
        
        if(file == null) return;
        
        ArrayList<Salario> current = JSONService.readWagesEdit();
        ArrayList<Salario> additions = JSONService.readWages(file.getPath());
        
        int nuevos = 0;
        int actualizados = 0;
        
        for(Salario s : additions) {
            boolean found = false;
            for(int i = 0; i < current.size(); i++) {
                if(current.get(i).getId().equals(s.getId())) {
                    current.set(i, s);
                    found = true;
                    actualizados++;
                    break;
                }
            }
            if(! found) {
                current.add(s);
                nuevos++;
            }
        }

        if(nuevos == 0 && actualizados == 0) {
            Utils.showAlert("Aviso", "El archivo importado no contiene datos válidos.", "", AlertType.WARNING);
            return;
        }
        
        if(Utils.showAlert("Confirmar Importación", 
                "Se procesarán " + additions.size() + " registros.", 
                "Nuevos: " + nuevos + " | Actualizados: " + actualizados + "\n¿Continuar?", 
                AlertType.CONFIRMATION)) {
            
            if(JSONService.writeWagesEdit(current)) {
                Utils.showAlert("Éxito", "Salarios importados correctamente.", "", AlertType.INFORMATION);
                mostrarSalarios(); // Refrescar vista actual
            } else {
                Utils.showAlert("Error", "No se pudo guardar la base de datos.", "", AlertType.ERROR);
            }
        }
    }

    @FXML
    private void exportarJSON() {
        Stage stage = (Stage) btnExportar.getScene().getWindow();
        String userHome = System.getProperty("user.home");
        File rutaEscritorio = FileSystemView.getFileSystemView().getHomeDirectory();
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Respaldar Salarios");
        fileChooser.setInitialFileName("respaldo_salarios.json");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Archivos JSON (*.json)", "*.json"));
        
        if (rutaEscritorio.exists() && rutaEscritorio.isDirectory()) {
            fileChooser.setInitialDirectory(rutaEscritorio);
        } else {
            fileChooser.setInitialDirectory(new File(userHome));
        }
        
        File file = fileChooser.showSaveDialog(stage);
        
        if(file == null) return;
        
        ArrayList<Salario> current = JSONService.readWagesEdit();
        
        try (BufferedWriter bw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            Gson gson = JSONService.createGson();
            gson.toJson(current, bw);
            Utils.showAlert("Exportación exitosa", "Respaldo de salarios creado.", "", AlertType.INFORMATION);
        } catch(IOException e) {
            e.printStackTrace();
            Utils.showAlert("Error", "Hubo un error al exportar el archivo.", "", AlertType.ERROR);
        }
    }
}