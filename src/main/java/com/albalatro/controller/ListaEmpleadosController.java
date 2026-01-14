package com.albalatro.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

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
        empleados = JSONService.readWorkersEdit();
        status = Status.ALTA;
        
        if (empleados.isEmpty()) {
            lblSinEmpleados.setVisible(true);
            scrollContainer.setVisible(false);
        } else {
            //Generar botones dinámicamente
            mostrarEmpleados();
        }
    }
    
    private void mostrarEmpleados() {
        empleados = JSONService.readWorkersEdit();
        vboxLista.getChildren().clear();
        
        lblSinEmpleados.setVisible(false);
        scrollContainer.setVisible(true);
        
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
        btnCambiarStatus.setText((status == Status.ALTA) ? "Mostrar antiguos Empleados" : "Mostrar Empleados activos");
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
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Archivo JSON (*.json)", "*.json"));
        
        File file = fileChooser.showOpenDialog(stage);
        
        if(file == null) {
            return; // El usuario cerró la ventana de archivos
        }
        
        // 1. Leemos los datos
        ArrayList<Empleado> current = JSONService.readWorkersEdit();
        ArrayList<Empleado> additions = JSONService.readWorkers(file.getPath());
        
        // 2. Hacemos el Merge (Tu lógica aquí estaba perfecta)
        int nuevos = 0;
        int actualizados = 0;
        
        for(Empleado e : additions) {
            boolean found = false;
            for(int i = 0; i < current.size(); i++) {
                if(current.get(i).getId().equals(e.getId())) {
                    current.set(i, e);
                    found = true;
                    actualizados++;
                    break;
                }
            }
            if(! found) {
                current.add(e);
                nuevos++;
            }
        }

        if(nuevos == 0 && actualizados == 0) {
            Utils.showAlert("Error", "El JSON esta vacío.", "", AlertType.ERROR);
            return;
        }
        
        // "Si el usuario dice SÍ, entonces guardamos"
        if(Utils.showAlert("Confirmar Importación", 
        "Se van a procesar " + additions.size() + " registros.", 
        "Nuevos: " + nuevos + " | Actualizados: " + actualizados + "\n¿Deseas continuar?", 
        AlertType.CONFIRMATION)) {
            
            if(JSONService.writeWorkersEdit(current)) {
                Utils.showAlert("Éxito", "Importación completada correctamente.", "", AlertType.INFORMATION);
                mostrarEmpleados(); // Actualizamos la vista
            } else {
                Utils.showAlert("Error", "No se pudo escribir en el archivo de base de datos.", "", AlertType.ERROR);
            }
            
        } else {
            // Si el usuario dice que NO en la alerta
            Utils.showAlert("Cancelado", "No se realizaron cambios.", "", AlertType.WARNING);
        }
    }
    
    @FXML
    private void exportarJSON() {
        Stage stage = (Stage) btnExportar.getScene().getWindow();
        //Ruta para el escritorio
        String userHome = System.getProperty("user.home");
        File rutaEscritorio = FileSystemView.getFileSystemView().getHomeDirectory();
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona un directorio para guardar");
        fileChooser.setInitialFileName("respaldo_empleados.json");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos JSON (*.json)", "*.json")
        );
        
        // En caso de que no tenga escritorio
        if (rutaEscritorio.exists() && rutaEscritorio.isDirectory()) {
            fileChooser.setInitialDirectory(rutaEscritorio);
        } else {
            // Plan B: Usar la carpeta de Documentos o el Home
            fileChooser.setInitialDirectory(new File(userHome));
        }
        
        File file  = fileChooser.showSaveDialog(stage);
        
        if(file == null) {
            Utils.showAlert("Exportación cancelada", "Se ha cancelado el proceso.", "", AlertType.WARNING);
            return;
        }
        
        ArrayList<Empleado> current = JSONService.readWorkersEdit();
        
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
