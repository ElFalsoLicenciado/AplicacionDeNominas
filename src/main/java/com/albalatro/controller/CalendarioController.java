package com.albalatro.controller;

import com.albalatro.model.DailyLog;
import com.albalatro.model.Empleado;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CalendarioController {

    @FXML private Label lblNombre;
    @FXML private Label lblApellidoP;
    @FXML private Label lblApellidoM;
    @FXML private Label lblTotalHoras;
    @FXML private Label lblTotalSueldo;
    @FXML private Label lblMesAno;
    @FXML private GridPane gridCalendario;

    private YearMonth mesActual;
    private Empleado empleado;

    @FXML
    public void initialize() {
        // Obtener el empleado de la sesión
        empleado = Session.getEmpleadoSeleccionado();

        // Llenar los datos de cabecera
        if (empleado != null) {
            lblNombre.setText(empleado.getNombre());
            lblApellidoP.setText(empleado.getApellidoP());
            lblApellidoM.setText(empleado.getApellidoM());
        }

        // Empezar en el mes actual
        mesActual = YearMonth.now();
        
        // Dibujar el calendario
        actualizarVista();
    }

    @FXML
    public void mesAnterior() {
        mesActual = mesActual.minusMonths(1);
        actualizarVista();
    }

    @FXML
    public void mesSiguiente() {
        mesActual = mesActual.plusMonths(1);
        actualizarVista();
    }

    @FXML
    public void regresar() {
        Navigation.goBack();
    }

    private void actualizarVista() {
        // Actualizar título del mes
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"));
        lblMesAno.setText(mesActual.format(formatter).toUpperCase());

        // Limpiar la cuadrícula Y LAS FILAS ANTERIORES
        gridCalendario.getChildren().clear();
        gridCalendario.getRowConstraints().clear(); // <--- CLAVE PARA EL RESIZE VERTICAL

        // Variables de cálculo
        double totalSueldoMes = 0;
        double totalHorasMes = 0;

        // Lógica de Fechas
        LocalDate primerDiaDelMes = mesActual.atDay(1);
        int diasEnElMes = mesActual.lengthOfMonth();
        
        // Ajustar día de inicio (Domingo=0 ... Sábado=6)
        int diaSemanaInicio = primerDiaDelMes.getDayOfWeek().getValue(); 
        if (diaSemanaInicio == 7) diaSemanaInicio = 0; 

        // Calculamos cuántas filas (semanas) necesitamos para este mes específico
        int totalCeldas = diaSemanaInicio + diasEnElMes;
        int numeroSemanas = (int) Math.ceil(totalCeldas / 7.0);

        // Creamos una restricción para cada semana para que crezca (Priority.ALWAYS)
        for (int k = 0; k < numeroSemanas; k++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setVgrow(Priority.ALWAYS); // Estirarse verticalmente
            rowConst.setFillHeight(true);       // Llenar el alto disponible
            gridCalendario.getRowConstraints().add(rowConst);
        }

        int fila = 0;
        int columna = diaSemanaInicio;

        // Bucle por cada día del mes
        for (int i = 1; i <= diasEnElMes; i++) {
            LocalDate fechaDia = mesActual.atDay(i);
            
            // --- Obtener datos del Log ---
            DailyLog logDia = null;
            if (empleado.getLog() != null && empleado.getLog().getLogs() != null) {
                logDia = empleado.getLog().getLogs().get(fechaDia);
            }

            // --- Calcular totales ---
            String textoHoras = "";
            String colorFondo = "#ffffff"; 

            if (logDia != null) {
                Double pago = logDia.getTotalPagoDia();
                Long minutos = logDia.getTotalMinutosTrabajados();
                
                totalSueldoMes += (pago != null ? pago : 0);
                totalHorasMes += (minutos != null ? minutos : 0);

                if (minutos != null && minutos > 0) {
                    double horas = minutos / 60.0;
                    textoHoras = String.format("%.1f h", horas);
                    colorFondo = "#E8F5E9"; // Verde claro
                }
            }

            // --- Crear la celda visual ---
            VBox celda = crearCeldaDia(i, textoHoras, colorFondo);
            
            // Añadir al grid
            gridCalendario.add(celda, columna, fila);

            columna++;
            if (columna > 6) { 
                columna = 0;
                fila++;
            }
        }

        // Actualizar etiquetas de resumen
        lblTotalHoras.setText(String.format("%.1f h", totalHorasMes / 60.0));
        lblTotalSueldo.setText(String.format("$%.2f", totalSueldoMes));
    }

    private VBox crearCeldaDia(int numeroDia, String textoHoras, String colorHex) {
        VBox celda = new VBox(5);
        celda.setAlignment(Pos.TOP_LEFT);
        celda.setPadding(new javafx.geometry.Insets(5));
        
        celda.setMaxHeight(Double.MAX_VALUE); 
        
        celda.setStyle("-fx-border-color: #eee; -fx-background-color: " + colorHex + ";");

        // Número del día
        Label lblDia = new Label(String.valueOf(numeroDia));
        lblDia.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Horas trabajadas
        Label lblHoras = new Label(textoHoras);
        lblHoras.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 12px;"); 

        celda.getChildren().addAll(lblDia, lblHoras);
        
        // Efecto hover
        final String finalColor = colorHex; // Variable final para usar en lambda
        celda.setOnMouseEntered(e -> celda.setStyle("-fx-border-color: #aaa; -fx-background-color: #f0f0f0;"));
        celda.setOnMouseExited(e -> celda.setStyle("-fx-border-color: #eee; -fx-background-color: " + finalColor + ";"));

        return celda;
    }
}