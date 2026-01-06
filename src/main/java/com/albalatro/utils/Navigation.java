package com.albalatro.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;

import com.albalatro.model.Empleado;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class Navigation {

    private static BorderPane mainLayout; // El contenedor principal
    private static Button backButton;     // Referencia al botón de atrás visual
    
    // Historial de vistas (Guardamos objetos Parent ya cargados)
    private static final Stack<Parent> history = new Stack<>();

    public static void setMainComponents(BorderPane layout, Button btnAtras) {
        mainLayout = layout;
        backButton = btnAtras;
        actualizarBotonAtras(); // Ocultarlo al inicio si no hay historial
    }

    /**
     * Navegar a una nueva vista (FXML) estándar
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void cambiarVista(String fxmlPath) {
        try {
            // 1. Si ya hay una vista mostrándose, la guardamos en el historial
            if (mainLayout.getCenter() != null) {
                history.push((Parent) mainLayout.getCenter());
            }

            // 2. Cargar la nueva vista
            URL url = Navigation.class.getResource(fxmlPath);
            if (url == null) {
                System.out.println("Error: No se encontró el FXML: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent nuevaVista = loader.load();

            // 3. Mostrarla y actualizar botón
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
     * Lógica visual del botón: Se oculta si no hay historial
     */
    private static void actualizarBotonAtras() {
        if (backButton != null) {
            backButton.setVisible(!history.isEmpty());
        }
    }
    
    /**
     * Limpiar todo el historial
     */
    public static void clearHistory() {
        history.clear();
        actualizarBotonAtras();
    }

    /**
     * Método Especial: Al guardar un empleado, reinicia el historial, pone Home de fondo
     * y muestra el Calendario del empleado.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void empleadoGuardadoCustomHistory(Empleado empleado) {
        try {
            Session.setEmpleadoSeleccionado(empleado);
            
            // 1. Limpiar historial previo (borrar rastro de "Crear Empleado")
            history.clear();

            // 2. Cargar Home y empujarlo manualmente a la pila
            // (Así, al dar atrás, el usuario irá al menú principal)
            FXMLLoader loaderHome = new FXMLLoader(Navigation.class.getResource("/View/HomeView.fxml"));
            Parent homeView = loaderHome.load();
            history.push(homeView);
    
            // 3. Cargar y mostrar el Calendario (sin usar cambiarVista para no ensuciar la pila)
            FXMLLoader loaderCal = new FXMLLoader(Navigation.class.getResource("/View/CalendarioView.fxml"));
            Parent calendarioView = loaderCal.load();

            mainLayout.setCenter(calendarioView);
            actualizarBotonAtras();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método Especial: Al dar de Alta, reinicia historial y lleva a la Lista.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void irAListaEmpleados() {
        try {
            // 1. Limpiar historial
            history.clear();

            // 2. Cargar Home y empujarlo a la pila
            FXMLLoader loaderHome = new FXMLLoader(Navigation.class.getResource("/View/HomeView.fxml"));
            Parent homeView = loaderHome.load();
            history.push(homeView);

            // 3. Cargar y mostrar la Lista de Empleados
            FXMLLoader loaderList = new FXMLLoader(Navigation.class.getResource("/View/ListaEmpleadosView.fxml"));
            Parent listaView = loaderList.load();
            
            mainLayout.setCenter(listaView);
            actualizarBotonAtras();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}