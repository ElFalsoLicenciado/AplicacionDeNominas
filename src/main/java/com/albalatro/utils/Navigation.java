package com.albalatro.utils;

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Stack;

import com.albalatro.model.Empleado;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;

public class Navigation {

    private static BorderPane mainLayout; // El contenedor principal
    private static Button backButton;     // Referencia al botón de atrás visual
    
    // Historial de vistas
    private static final Stack<Parent> history = new Stack<>();

    public static void setMainComponents(BorderPane layout, Button btnAtras) {
        mainLayout = layout;
        backButton = btnAtras;
        actualizarBotonAtras(); // Ocultarlo al inicio
    }

    /**
     * Navegar a una nueva vista (FXML)
     */
    public static void cambiarVista(String fxmlPath) {
        try {
            // 1. Si ya hay algo, guardarlo en historial
            if (mainLayout.getCenter() != null) {
                history.push((Parent) mainLayout.getCenter());
            }

            java.net.URL url = Navigation.class.getResource(fxmlPath);

            FXMLLoader loader = new FXMLLoader(url); // Usamos la URL verificada
            Parent nuevaVista = loader.load();

            mainLayout.setCenter(nuevaVista);
            actualizarBotonAtras();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Regresar a la vista anterior
     */
    public static void goBack() {
        if (!history.isEmpty()) {
            // Sacamos la última vista de la pila
            Parent vistaAnterior = history.pop();
            
            // La ponemos en el centro (sin guardarla en historial de nuevo)
            mainLayout.setCenter(vistaAnterior);
            actualizarBotonAtras();
        }
    }

    /**
     * Lógica visual del botón
     */
    private static void actualizarBotonAtras() {
        if (backButton != null) {
            // Si el historial está vacío, deshabilitar/ocultar el botón
            backButton.setVisible(!history.isEmpty());
        }
    }
    
    /**
     * Método para limpiar historial (útil si vas al Home y quieres borrar el rastro)
     */
    public static void clearHistory() {
        history.clear();
        actualizarBotonAtras();
    }

    public static void empleadoGuardadoCustomHistory(Empleado empleado) {
        try {
            Session.setEmpleadoSeleccionado(empleado);
            history.clear();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    Navigation.class.getResource("/View/HomeView.fxml")
            );
            javafx.scene.Parent homeView = loader.load();
            history.push(homeView);
    
            cambiarVista("/View/CalendarioView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}