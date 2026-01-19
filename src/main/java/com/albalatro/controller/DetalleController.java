package com.albalatro.controller;

import java.io.IOException;
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
import com.albalatro.model.Status;
import com.albalatro.model.TipoPago;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class DetalleController {
    
    @FXML private Label lblFechaActual;
    @FXML private VBox containerPeriodos;
    @FXML private Label lblTotalDinero;
    @FXML private ComboBox<Salario> comboSalarioDiario;
    
    private ArrayList<Salario> salarios = new ArrayList<>();
    private LocalDate fechaActual;
    private Empleado empleadoActual;
    private Salario salario; // Salario base para este día
    private String notas;
    private Runnable onDatosGuardados; 
    
    // Formateadores
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd MMM", new Locale("es", "ES"));
    
    public void setOnDatosGuardados(Runnable onDatosGuardados) {
        this.onDatosGuardados = onDatosGuardados;
    }
    
    private void buscarSalarios() {
        ArrayList<Salario> listado = JSONService.readWagesEdit();
        
        salarios = new ArrayList<>();
        for (Salario w : listado) {
            if (w.getStatus().equals(Status.ALTA))
                salarios.add(w);
        }
    }
    
    private void agregarSalario(Salario nuevo) {
        salarios.add(nuevo);
    }
    
    // --- CONFIGURACIÓN Y CARGA ---
    
    private void configurarComboSalarios() {
        if (empleadoActual.getLog() != null && empleadoActual.getLog().getLogs() != null) {
            DailyLog logDia = empleadoActual.getLog().getLogs().get(fechaActual);
            if (logDia != null) {
                Salario salarioAsignado = logDia.getSalario();
                
                boolean found = false;
                for (Salario w : salarios) {
                    if(w.getId().equals(salarioAsignado.getId())) {
                        found =  true;
                        break;
                    }
                }
                
                if(! found) agregarSalario(salarioAsignado);
            }
        }
        
        comboSalarioDiario.setItems(FXCollections.observableArrayList(salarios));
        comboSalarioDiario.setConverter(new StringConverter<Salario>() {
            @Override
            public String toString(Salario s) {
                if (s == null) return null;
                return s.getNombre() + " - $" + s.getNormal(); 
            }
            @Override
            public Salario fromString(String string) { return null; }
        });
    }
    
    public void cargarDatosDia(LocalDate fecha, Empleado emp) {
        this.fechaActual = fecha;
        this.empleadoActual = emp;
        
        lblFechaActual.setText(fecha.format(dateFormatter).toUpperCase());
        containerPeriodos.getChildren().clear();
        this.salario = null;
        
        buscarSalarios();
        configurarComboSalarios();
        
        // 1. Recuperar datos existentes del día (si los hay)
        if (emp.getLog() != null && emp.getLog().getLogs() != null) {
            DailyLog logDia = emp.getLog().getLogs().get(fecha);
            if (logDia != null) {
                this.salario = logDia.getSalario();
                lblTotalDinero.setText(String.format("$%.2f", logDia.getTotalPagoDia()));
                
                if (logDia.getPeriodos() != null) {
                    for (Periodo p : logDia.getPeriodos()) {
                        agregarFilaVisual(p.getEntrada(), p.getSalida());
                    }
                }
                if (logDia.getNotas() != null) {
                    notas = logDia.getNotas();
                }
            } 
        }
        
        // 2. Si no hay salario específico guardado, usar el default del empleado
        if (this.salario == null) {
            this.salario = JSONService.getSalario(emp.getSalario());
            lblTotalDinero.setText("$0.00");
        }
        
        // 3. Seleccionar en el combo de manera segura
        if (this.salario != null) {
            for (Salario s : comboSalarioDiario.getItems()) {
                if (s.getId().equals(this.salario.getId())) {
                    comboSalarioDiario.getSelectionModel().select(s);
                    break;
                }
            }
        }
    }
    
    // --- MÉTODOS DE INTERACCIÓN ---
    
    @FXML
    public void diaAnterior() { cargarDatosDia(fechaActual.minusDays(1), empleadoActual); }
    
    @FXML
    public void diaSiguiente() { cargarDatosDia(fechaActual.plusDays(1), empleadoActual); }
    
    @FXML
    public void agregarNuevoPeriodo() {
        // Crea una fila nueva (con horas vacías o default)
        agregarFilaVisual(null, null);
    }
    
    @FXML
    public void guardarCambios() {
        ArrayList<Periodo> nuevosPeriodos = new ArrayList<>();
        boolean huboErrores = false;
        
        int periodos = 0;

        // --- 1. VALIDACIÓN DE HORAS ---
        for (var node : containerPeriodos.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                TextField txtEntrada = (TextField) row.getChildren().get(0);
                TextField txtSalida = (TextField) row.getChildren().get(2); 
                
                String strEntrada = txtEntrada.getText().trim();
                String strSalida = txtSalida.getText().trim();
                
                // Ignoramos filas vacías
                if (strEntrada.isEmpty() || strSalida.isEmpty()) continue; 
                
                try {
                    // Autocorrección inteligente (ej: "9:00" -> "09:00")
                    if(strEntrada.indexOf(':') == 1) strEntrada = "0" + strEntrada;
                    if(strSalida.indexOf(':') == 1) strSalida = "0" + strSalida;
                    
                    LocalTime in = LocalTime.parse(strEntrada, timeFormatter);
                    LocalTime out = LocalTime.parse(strSalida, timeFormatter);
                    
                    Periodo p = new Periodo(); 
                    p.setEntrada(in);
                    p.setSalida(out);
                    nuevosPeriodos.add(p);
                    
                    // Limpiamos estilos de error si los había
                    txtEntrada.setStyle(null);
                    txtSalida.setStyle(null);
                    periodos += 1;
                } catch (DateTimeParseException e) {
                    huboErrores = true;
                    txtEntrada.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    txtSalida.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }
            }
        }
        
        if (huboErrores) {
            mostrarAlerta("Error de formato", "Por favor usa el formato HH:mm (Ejemplo: 09:00).");
            return; // Detenemos el guardado
        }
        
        // --- 2. ACTUALIZACIÓN DEL MODELO ---
        // Aseguramos estructura de mapas

         boolean tieneContenido = (periodos > 0 || (notas != null && !notas.isEmpty()));

        if (empleadoActual.getLog() == null) empleadoActual.setLog(new Log(new HashMap<>()));
        if (empleadoActual.getLog().getLogs() == null) empleadoActual.getLog().setLogs(new HashMap<>());
        
        // Obtenemos el salario del combo (o el fallback)
        Salario salarioSeleccionado = comboSalarioDiario.getValue();
        if (salarioSeleccionado == null) salarioSeleccionado = this.salario;
        
        DailyLog logDia = empleadoActual.getLog().getLogs().get(fechaActual);
        
        
        if (logDia == null) {
            // Nuevo Log
            logDia = new DailyLog(salarioSeleccionado, fechaActual, nuevosPeriodos);
            empleadoActual.getLog().getLogs().put(fechaActual, logDia);
        } else {
            // Actualizar existente: Gracias a tu arreglo en DailyLog, el orden ya no importa tanto,
            // pero es buena práctica asignar salario primero.
            logDia.setSalario(salarioSeleccionado);
            logDia.setPeriodos(nuevosPeriodos);
        }
        
        logDia.setNotas(notas);
        
        if (periodos == 0 && notas == null) {
            logDia = null;
        }

        // --- 3. GUARDADO EN DISCO ---
        ArrayList<Empleado> listaActualizada = JSONService.readWorkersEdit();
        boolean encontrado = false;
        
        for(int i=0; i<listaActualizada.size(); i++) {
            if(listaActualizada.get(i).getId().equals(empleadoActual.getId())) {
                listaActualizada.set(i, empleadoActual);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) listaActualizada.add(empleadoActual);
        
        if (JSONService.writeWorkersEdit(listaActualizada)) {
            // Éxito: Notificar y Cerrar
            if (onDatosGuardados != null) onDatosGuardados.run();
            cerrarVentana();
            Session.setChanges(true);
        } else {
            mostrarAlerta("Error", "No se pudo guardar en el archivo de base de datos.");
        }
    }
    
    // --- MÉTODOS AUXILIARES ---
    
    private void agregarFilaVisual(LocalTime entrada, LocalTime salida) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5; -fx-background-radius: 5;");
        
        // Valores por defecto "prompt" para guiar al usuario
        String valEntrada = (entrada != null) ? entrada.format(timeFormatter) : "09:00";
        String valSalida = (salida != null) ? salida.format(timeFormatter) : "17:00";
        
        TextField txtIn = new TextField(valEntrada);
        txtIn.setPrefWidth(70);
        
        Label lblSep = new Label("a");
        
        TextField txtOut = new TextField(valSalida);
        txtOut.setPrefWidth(70);
        
        Button btnEliminar = new Button("✕");
        btnEliminar.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-weight: bold;");
        btnEliminar.setOnAction(e -> containerPeriodos.getChildren().remove(row));
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        row.getChildren().addAll(txtIn, lblSep, txtOut, spacer, btnEliminar);
        containerPeriodos.getChildren().add(row);
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) lblFechaActual.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
    
    @FXML
    private void abrirCrearSalarioModal() {
        try {
            Salario custom = new Salario(
                "custom", "Salario temporal", TipoPago.HORA,
                40.0, 50.0, Status.ALTA
            );
            
            Session.setSalarioSeleccionado(custom);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/SalarioView.fxml"));
            Parent root = loader.load();
            
            SalarioController controller = loader.getController();
            controller.setEsModal(true); 
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Recargar combo
            // Salario seleccionPrevia = comboSalarioDiario.getValue();
            
            buscarSalarios();
            agregarSalario(custom);
            configurarComboSalarios(); 
            
            // if (seleccionPrevia != null) {
            //     for(Salario s : comboSalarioDiario.getItems()){
            //         if(s.getId().equals(seleccionPrevia.getId())){
            //             comboSalarioDiario.getSelectionModel().select(s);
            //             break;
            //         }
            //     }
            // }
            
            Salario s = salarios.get(salarios.size()-1);
            comboSalarioDiario.getSelectionModel().select(s);
            
            Session.setSalarioSeleccionado(null);
            
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    @FXML
    private void abrirNotas () {
        try {
            Session.setNotas(notas);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/NotasView.fxml"));
            Parent root = loader.load();
            
            NotasController controller = loader.getController();
            controller.setEsModal(true); 
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            notas = Session.getNotas();
            
            Session.setNotas(null);
            
        } catch (IOException e) {e.printStackTrace(); }
    }
}