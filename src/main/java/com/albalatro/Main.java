package com.albalatro;

import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start (Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/MainView.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/CSS/styles.css").toExternalForm());
        
        try {
            Image icon = new Image(getClass().getResourceAsStream("/Images/joker.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el ícono: " + e.getMessage());
        }
        
        primaryStage.setTitle("Albalatro - Aplicacion de Nominas");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            logout(primaryStage);
        });
    }
    
    
    
    public static void main(String[] args) {
        System.out.println("albalatro moment");
        launch(args);
    }
    
    
    public void logout(Stage stage){	
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Salir del programa...");
        alert.setHeaderText("¿Estás seguro de que quieres salir?");
        alert.setContentText("¿Has guardado todos los cambios?");
        
        if (alert.showAndWait().get() == ButtonType.OK){
            stage.close();
        } 
    }
}