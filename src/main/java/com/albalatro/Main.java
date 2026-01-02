package com.albalatro;

import com.albalatro.service.JSONService;
import com.albalatro.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start (Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/MainView.fxml")));
        Scene scene = new Scene(root);

        try {
            Image icon = new Image(getClass().getResourceAsStream("/Images/icon.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el ícono: " + e.getMessage());
        }

        primaryStage.setTitle("Albalatro - Aplicacion de Nominas");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private static final String FOLDER = "data/";
    private static final String JSON = "workers";
    public static void main(String[] args) {
        System.out.println("albalatro moment");
        // Empleado alberto = new com.albalatro.model.Empleado();
        Utils.innitStuff(FOLDER, JSON);
        JSONService.setFILE(String.format("%s/%s.json",FOLDER, JSON));

        //TODO: Cargar el objeto salario desde un JSON y a partir de ahi modificar el salario. Posible singleton

        launch(args);
    }
}