package com.albalatro.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.albalatro.model.DailyLog;
import com.albalatro.model.Empleado;
import com.albalatro.model.Log;
import com.albalatro.model.Periodo;
import com.albalatro.model.Salario;
import com.albalatro.service.JSONService;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DetalleController {

    @FXML private Label lblFechaActual;
    @FXML private VBox containerPeriodos;
    @FXML private Label lblTotalDinero;

    private LocalDate fechaActual;
    private Empleado empleadoActual;
    private Salario salario;
    private Runnable onDatosGuardados; // El "Callback" para avisar al calendario

    // Formateadores de hora
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd MMM", new Locale("es", "ES"));
    // --- MÉTODOS DE INICIALIZACIÓN ---

    public void setOnDatosGuardados(Runnable onDatosGuardados) {
        this.onDatosGuardados = onDatosGuardados;
    }

    /**
     * Este es el método principal que llama el Calendario para reciclar la ventana
     */
    public void cargarDatosDia(LocalDate fecha, Empleado emp) {
        this.fechaActual = fecha;
        this.empleadoActual = emp;
        
        // 1. Actualizar Título
        lblFechaActual.setText(fecha.format(dateFormatter).toUpperCase());

        // 2. Limpiar filas anteriores
        containerPeriodos.getChildren().clear();

        // 3. Buscar datos existentes
        if (emp.getLog() != null && emp.getLog().getLogs() != null) {
            DailyLog logDia = emp.getLog().getLogs().get(fecha);
            
            if (logDia != null && logDia.getPeriodos() != null) {
                salario = logDia.getSalario();

                // Actualizar total visual
                lblTotalDinero.setText(String.format("$%.2f", logDia.getTotalPagoDia()));

                // Crear filas visuales para cada periodo existente
                for (Periodo p : logDia.getPeriodos()) {
                    agregarFilaVisual(p.getEntrada(), p.getSalida());
                }
            } else {
                lblTotalDinero.setText("$0.00");
                
            }
        }
        salario = JSONService.getSalario(empleadoActual.getSalario());
    }

    // --- MÉTODOS DE INTERACCIÓN ---

    @FXML
    public void diaAnterior() {
        cargarDatosDia(fechaActual.minusDays(1), empleadoActual);
    }

    @FXML
    public void diaSiguiente() {
        cargarDatosDia(fechaActual.plusDays(1), empleadoActual);
    }

    @FXML
    public void agregarNuevoPeriodo() {
        // Agrega una fila vacía para que el usuario escriba
        agregarFilaVisual(null, null);
    }

    @FXML
    public void guardarCambios() {
        // 1. Recolectar datos de los TextField
        ArrayList<Periodo> nuevosPeriodos = new ArrayList<>();
        
        for (var node : containerPeriodos.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                TextField txtEntrada = (TextField) row.getChildren().get(0);
                TextField txtSalida = (TextField) row.getChildren().get(2); // Indice 1 es el separador "a"

                String strEntrada = txtEntrada.getText().trim();
                String strSalida = txtSalida.getText().trim();

                if (!strEntrada.isEmpty() && !strSalida.isEmpty()) {
                    try {
                        LocalTime in = LocalTime.parse(strEntrada, timeFormatter);
                        LocalTime out = LocalTime.parse(strSalida, timeFormatter);
                        
                        // Crear objeto Periodo (Ajusta según tu constructor)
                        Periodo p = new Periodo(); 
                        p.setEntrada(in);
                        p.setSalida(out);
                        p.getMinutosTrabajados(); // Importante si tu clase no lo hace auto
                        
                        nuevosPeriodos.add(p);
                    } catch (DateTimeParseException e) {
                        System.out.println("Formato de hora inválido en fila: " + strEntrada);
                        // Aquí podrías mostrar una alerta al usuario
                    }
                }
            }
        }

        // 2. Actualizar el Modelo (DailyLog)
        if (empleadoActual.getLog() == null) {
            empleadoActual.setLog(new Log(new HashMap<>()));
        }
        if (empleadoActual.getLog().getLogs() == null) {
            empleadoActual.getLog().setLogs(new HashMap<>());
        }

        // Creamos o actualizamos el DailyLog
        DailyLog logDia = empleadoActual.getLog().getLogs().get(fechaActual);
        if (logDia == null) {
            logDia = new DailyLog(salario,  fechaActual, nuevosPeriodos);
            empleadoActual.getLog().getLogs().put(fechaActual, logDia);
        } else {
            logDia.setPeriodos(nuevosPeriodos);
        }

        // 3. Persistencia (Guardar JSON)
        // Necesitas actualizar la lista general de empleados y guardar
        // Como aquí solo tenemos 1 empleado, lo ideal es recargar la lista, buscar este empleado y actualizarlo,
        // o pasar la lista completa. Por simplicidad, asumimos que manejas la persistencia:
        ArrayList<Empleado> listaActualizada = JSONService.readWorkers();
        // Lógica para reemplazar el empleado actual en la lista y guardar...
        for(int i=0; i<listaActualizada.size(); i++) {
            if(listaActualizada.get(i).getId().equals(empleadoActual.getId())) {
                listaActualizada.set(i, empleadoActual);
                break;
            }
        }
        JSONService.writeWorkers(listaActualizada);

        // 4. Refrescar vista actual (Totales)
        cargarDatosDia(fechaActual, empleadoActual);

        // 5. Avisar al Calendario Principal
        if (onDatosGuardados != null) {
            onDatosGuardados.run();
        }
        
        // Opcional: Cerrar ventana
        // ((Stage) lblFechaActual.getScene().getWindow()).close();
    }

    // --- HELPER VISUAL ---
    
    private void agregarFilaVisual(LocalTime entrada, LocalTime salida) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5; -fx-background-radius: 5;");

        TextField txtIn = new TextField(entrada != null ? entrada.format(timeFormatter) : "");
        txtIn.setPromptText("09:00");
        txtIn.setPrefWidth(70);

        Label lblSep = new Label("a");

        TextField txtOut = new TextField(salida != null ? salida.format(timeFormatter) : "");
        txtOut.setPromptText("17:00");
        txtOut.setPrefWidth(70);

        Button btnEliminar = new Button("✕");
        btnEliminar.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-cursor: hand;");
        btnEliminar.setOnAction(e -> containerPeriodos.getChildren().remove(row));

        // Espaciador para empujar el botón X a la derecha
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(txtIn, lblSep, txtOut, spacer, btnEliminar);
        containerPeriodos.getChildren().add(row);
    }
}