package com.albalatro.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.filechooser.FileSystemView;

import com.albalatro.model.Corte;
import com.albalatro.model.DailyLog;
import com.albalatro.model.Empleado;
import com.albalatro.model.Periodo;
import com.albalatro.service.JSONService;
import com.albalatro.service.PDFService;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;
import com.albalatro.utils.Utils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CalendarioController {
    
    @FXML private Label lblNombre;
    @FXML private Label lblApellidoP;
    @FXML private Label lblApellidoM;
    @FXML private Label lblTotalHoras;
    @FXML private Label lblTotalSueldo;
    @FXML private Label lblMesAno;
    @FXML private GridPane gridCalendario;
    
    // Botones de acción
    @FXML private Button btnGestionar, btnPDF, toggleBtn, btnSeleccionarFecha;
    @FXML private Button btnToggleResumen; // Botón para alternar resumen (Mes/Periodo)
    
    // Controles de fecha (Rango)
    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFin;
    @FXML private Label lblGuion; // Guion separador visual
    
    private YearMonth mesActual;
    private Empleado empleado;
    
    private Stage stageDetalle = null;
    private DetalleController controllerDetalle = null;
    
    private boolean generandoFechaDeCorte = false;
    private boolean verResumenMensual = true; // Estado del toggle de resumen
    
    private String fechaDeCorteValue = "Generar fecha de corte";
    private String opcionesAvanzadasValue = "Cancelar Pago"; // Cambiado texto para claridad
    
    @FXML
    public void initialize() {
        // --- 1. CARGA DE EMPLEADO ---
        lblTotalSueldo.setText("$0.00");
        empleado = Session.getEmpleadoSeleccionado();
        
        if (empleado != null) {
            lblNombre.setText(empleado.getNombre());
            lblApellidoP.setText(empleado.getApellidoP());
            lblApellidoM.setText(empleado.getApellidoM());
            
            cargarFechasEnDatePicker();
        }
        
        // --- 3. RESTRICCIONES DE DATEPICKERS ---
        // Inicio: No futuro
        datePickerInicio.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
        
        // Fin: No futuro y No antes que inicio
        datePickerFin.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate inicio = datePickerInicio.getValue();
                boolean esFuturo = date.isAfter(LocalDate.now());
                boolean esAnteriorAlInicio = (inicio != null) && date.isBefore(inicio);
                
                if (esFuturo || esAnteriorAlInicio) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
        
        // Ocultar controles de pago al inicio
        toggleControlesRango(false);
        
        // Inicializar calendario en mes actual
        mesActual = YearMonth.now();
        actualizarVista();
    }

    // Nuevo método para refrescar los datepickers (útil al regresar del historial)
    private void cargarFechasEnDatePicker() {
        LocalDate ultimoPago = empleado.getFinCorte();
        LocalDate fechaInicioDefault = (ultimoPago != null) ? ultimoPago.plusDays(1) : empleado.getInicioCorte();
        if (fechaInicioDefault == null) fechaInicioDefault = LocalDate.now();
        
        datePickerInicio.setValue(fechaInicioDefault);
        datePickerFin.setValue(LocalDate.now());
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
    
    @FXML
    public void gestionarPressed() {
        Session.setSalarioSeleccionado(JSONService.getSalario(empleado.getSalario()));
        Navigation.cambiarVista("/View/CrearEmpleadoView.fxml");
    }

    // --- NUEVO: ABRIR HISTORIAL ---
    @FXML
    private void abrirHistorial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/HistorialPagosView.fxml"));
            Parent root = loader.load();
            
            HistorialPagosController controller = loader.getController();
            
            // Cuando se cierre el historial (y quizás se eliminó un corte), refrescamos:
            controller.setOnCambioRealizado(() -> {
                cargarFechasEnDatePicker();
                actualizarVista();
            });
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Historial de Cortes - " + empleado.getNombre());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            Utils.showAlert("Error", "No se pudo abrir el historial.", "", Alert.AlertType.ERROR);
        }
    }
    
    // --- LÓGICA DE VISUALIZACIÓN ---
    private void actualizarVista() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"));
        lblMesAno.setText(mesActual.format(formatter).toUpperCase());
        
        gridCalendario.getChildren().clear();
        gridCalendario.getRowConstraints().clear();
        
        double sueldoMesVisible = 0;
        double horasMesVisible = 0;
        
        LocalDate primerDiaDelMes = mesActual.atDay(1);
        int diasEnElMes = mesActual.lengthOfMonth();
        
        int diaSemanaInicio = primerDiaDelMes.getDayOfWeek().getValue(); 
        if (diaSemanaInicio == 7) diaSemanaInicio = 0; 
        
        // Calcular filas necesarias
        int totalCeldas = diaSemanaInicio + diasEnElMes;
        int numeroSemanas = (int) Math.ceil(totalCeldas / 7.0);
        
        for (int k = 0; k < numeroSemanas; k++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setVgrow(Priority.ALWAYS);
            rowConst.setFillHeight(true);
            gridCalendario.getRowConstraints().add(rowConst);
        }
        
        int fila = 0;
        int columna = diaSemanaInicio;
        
        for (int i = 1; i <= diasEnElMes; i++) {
            LocalDate fechaDia = mesActual.atDay(i);
            
            DailyLog logDia = null;
            if (empleado.getLog() != null && empleado.getLog().getLogs() != null) {
                logDia = empleado.getLog().getLogs().get(fechaDia);
            }
            
            String textoHoras = "";
            String colorFondo = "#ffffff"; 
            
            if (logDia != null) {
                Double pago = logDia.getTotalPagoDia();
                Long minutos = logDia.getTotalMinutosTrabajados();
                
                sueldoMesVisible += (pago != null ? pago : 0);
                horasMesVisible += (minutos != null ? minutos : 0);
                
                if (minutos != null && minutos > 0) {
                    double horas = minutos / 60.0;
                    textoHoras = String.format("%.1f h", horas);
                    colorFondo = "#E8F5E9"; 
                }
            }
            
            VBox celda = crearCeldaDia(i, fechaDia, textoHoras, colorFondo);
            gridCalendario.add(celda, columna, fila);
            
            columna++;
            if (columna > 6) { 
                columna = 0; 
                fila++; 
            }
        }
        
        // ACTUALIZAR RESUMEN (Toggle)
        if (verResumenMensual) {
            btnToggleResumen.setText("📅 VISTA: MES ACTUAL");
            lblTotalHoras.setText(String.format("%.1f h", horasMesVisible / 60.0));
            lblTotalSueldo.setText(String.format("$%.2f", sueldoMesVisible));
            
            // Estilo Verde
            lblTotalSueldo.getStyleClass().removeAll("label-money-period");
            if (!lblTotalSueldo.getStyleClass().contains("label-money")) {
                lblTotalSueldo.getStyleClass().add("label-money");
            }
            lblTotalSueldo.setStyle("-fx-font-size: 18px;"); 
            
        } else {
            calcularYMostrarPendientes();
        }
    }
    
    // --- LÓGICA DE PENDIENTES ---
    private void calcularYMostrarPendientes() {
        double totalPendienteDinero = 0;
        long totalPendienteMinutos = 0;
        
        LocalDate ultimaFechaPagada = empleado.getFinCorte();
        LocalDate hoy = LocalDate.now();
        
        if (empleado.getLog() != null && empleado.getLog().getLogs() != null) {
            for (java.util.Map.Entry<LocalDate, DailyLog> entry : empleado.getLog().getLogs().entrySet()) {
                LocalDate fechaLog = entry.getKey();
                DailyLog log = entry.getValue();
                
                boolean esPosteriorAlCorte = (ultimaFechaPagada == null) || fechaLog.isAfter(ultimaFechaPagada);
                boolean esAnteriorOIgualHoy = !fechaLog.isAfter(hoy);
                
                if (esPosteriorAlCorte && esAnteriorOIgualHoy) {
                    totalPendienteDinero += log.getTotalPagoDia();
                    totalPendienteMinutos += log.getTotalMinutosTrabajados();
                }
            }
        }
        
        btnToggleResumen.setText("⏳ VISTA: PENDIENTE TOTAL");
        lblTotalHoras.setText(String.format("%.1f h", totalPendienteMinutos / 60.0));
        lblTotalSueldo.setText(String.format("$%.2f", totalPendienteDinero));
        
        // Estilo Azul/Verdoso
        lblTotalSueldo.getStyleClass().removeAll("label-money");
        if (!lblTotalSueldo.getStyleClass().contains("label-money-period")) {
            lblTotalSueldo.getStyleClass().add("label-money-period");
        }
        lblTotalSueldo.setStyle(""); 
    }
    
    @FXML
    private void toggleResumenMode() {
        verResumenMensual = !verResumenMensual;
        actualizarVista();
    }
    
    // --- CREACIÓN DE CELDAS (COMPACTO) ---
    private VBox crearCeldaDia(int numeroDia, LocalDate fechaExacta, String textoHoras, String colorHex) {
        VBox celda = new VBox(0); 
        celda.getStyleClass().add("calendar-cell");
        
        // Verificar estados
        DailyLog logDelDia = null;
        if (empleado.getLog() != null && empleado.getLog().getLogs() != null) {
            logDelDia = empleado.getLog().getLogs().get(fechaExacta); 
        }
        boolean hayLog = (logDelDia != null);
        
        LocalDate inicioCorte = empleado.getInicioCorte();
        LocalDate finCorte = empleado.getFinCorte();
        
        boolean esFechaInicioCorte = (inicioCorte != null) && fechaExacta.isEqual(inicioCorte);
        boolean esFechaFinCorte = (finCorte != null) && fechaExacta.isEqual(finCorte);
        boolean esAnteriorYTrabajado = (finCorte != null) && fechaExacta.isBefore(finCorte) && hayLog;
        
        // Asignar clases CSS especiales
        if (esFechaFinCorte || esFechaInicioCorte) {
            celda.getStyleClass().add("calendar-cell-pago"); 
        } else if (esAnteriorYTrabajado) {
            celda.getStyleClass().add("calendar-cell-pagado"); 
        }
        
        celda.setAlignment(Pos.TOP_LEFT);
        celda.setPadding(new Insets(2));
        celda.setMaxHeight(Double.MAX_VALUE); 
        
        // Construir contenido visual
        Label lblDia = new Label(String.valueOf(numeroDia));
        lblDia.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblHoras = new Label(textoHoras);
        if (!textoHoras.isEmpty()) {
            lblHoras.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-font-size: 14px;");
        } else {
            lblHoras.setStyle("-fx-font-size: 1px;"); 
        }
        
        ArrayList<javafx.scene.Node> nodos = new ArrayList<>();
        nodos.add(lblDia);
        
        if (esFechaFinCorte || esFechaInicioCorte) {
            String ind = "";
            if (esFechaInicioCorte) ind = "INICIO CORTE";
            if (esFechaFinCorte) ind = "FIN CORTE";

            Label lblIndicador = new Label(ind); 
            lblIndicador.getStyleClass().add("label-corte");
            nodos.add(lblIndicador);
        }
        
        if (hayLog) {
            ArrayList<String> array = new ArrayList<>(); 
            for(Periodo p : logDelDia.getPeriodos()) {
                array.add(p.toString());
            }
            Label lblPeriodos = new Label(Utils.stringArrayToStringSpace(array));
            lblPeriodos.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            lblPeriodos.setWrapText(true);
            
            double montoPago = logDelDia.getTotalPagoDia();
            Label lblPago = new Label(String.format("$%.2f", montoPago));
            lblPago.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 14px; -fx-font-weight: bold;"); 
            
            if(! lblPeriodos.getText().isEmpty()) nodos.add(lblPeriodos);
            if(! lblPeriodos.getText().isEmpty()) nodos.add(lblHoras);
            if(montoPago > 0) nodos.add(lblPago);
            
            if (logDelDia.getNotas() != null) {
                Label lblNotas = new Label(logDelDia.getNotas());
                lblNotas.setStyle("-fx-text-fill: #020101; -fx-font-weight: bold; -fx-font-size: 12px;");
                lblNotas.setWrapText(true);
                nodos.add(lblNotas);
            }
        } else {
            nodos.add(lblHoras);
        }
        
        celda.getChildren().addAll(nodos);
        
        // --- CORRECCIÓN: APLICAR ESTILO INICIAL ---
        // Aplicamos el color de fondo inmediatamente al crear la celda
        if (esFechaFinCorte || esFechaInicioCorte) {
            celda.setStyle(""); // Deja que CSS controle el color (Amarillo)
        } else if (esAnteriorYTrabajado) {
            celda.setStyle(""); // Deja que CSS controle el color (Verde pálido)
        } else {
            // Celdas normales: Aplicamos blanco o verde (si hubo trabajo) manualmente
            celda.setStyle("-fx-border-color: #eee; -fx-background-color: " + colorHex + ";");
        }
        
        // Eventos de Mouse
        final String finalColor = colorHex; 
        
        celda.setOnMouseEntered(e -> {
            celda.setStyle("-fx-border-color: #aaa; -fx-background-color: #f0f0f0;");
        });
        
        celda.setOnMouseExited(e -> {
            if (esFechaFinCorte || esFechaInicioCorte) {
                celda.setStyle(""); 
            } else if (esAnteriorYTrabajado) {
                celda.setStyle(""); 
            } else {
                celda.setStyle("-fx-border-color: #eee; -fx-background-color: " + finalColor + ";");
            }
        });
        
        celda.setOnMouseClicked(event -> abrirVentanaDetalle(fechaExacta));
        
        return celda;
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    private void abrirVentanaDetalle(LocalDate fecha) {
        try {
            if (stageDetalle == null || !stageDetalle.isShowing()) {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/View/DetalleView.fxml")
                );
                Parent root = loader.load();
                
                controllerDetalle = loader.getController();
                controllerDetalle.setOnDatosGuardados(() -> {
                    this.actualizarVista(); 
                });
                
                stageDetalle = new javafx.stage.Stage();
                stageDetalle.setScene(new javafx.scene.Scene(root));
                stageDetalle.setTitle("Gestión de Día");
                stageDetalle.setResizable(false);
                stageDetalle.show();
            } else {
                stageDetalle.toFront();
            }
            
            if (controllerDetalle != null) {
                controllerDetalle.cargarDatosDia(fecha, this.empleado);
            }
            
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void generarPDF() {
        // 1. Validar que exista un corte seleccionado
        if (empleado.getInicioCorte() == null || empleado.getFinCorte() == null) {
            Utils.showAlert("Error a exportar PDF", "Selecciona un rango de dias para el corte.", "", Alert.AlertType.ERROR);
            return;
        }

        // 2. Configurar el FileChooser
        Stage stage = (Stage) btnPDF.getScene().getWindow();
        String userDesktop = System.getProperty("user.desktop"); 
        File escritorio = FileSystemView.getFileSystemView().getHomeDirectory();
        
        FileChooser fc = new FileChooser();
        fc.setTitle("Selecciona un directorio para guardar");
        fc.setInitialFileName("Registros de " + empleado.getNombreCompleto() + ".pdf");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        
        if (escritorio.exists() && escritorio.isDirectory()) {
            fc.setInitialDirectory(escritorio);
        } else {
            fc.setInitialDirectory(new File(userDesktop));
        }
        
        File file = fc.showSaveDialog(stage);
        if (file == null) return;
        
        // 3. --- CALCULAR TOTALES DEL PERIODO ---
        double totalSueldo = 0.0;
        double totalHoras = 0.0;
        
        LocalDate inicio = empleado.getInicioCorte();
        LocalDate fin = empleado.getFinCorte();

        if (empleado.getLog() != null && empleado.getLog().getLogs() != null) {
            for (java.util.Map.Entry<LocalDate, DailyLog> entry : empleado.getLog().getLogs().entrySet()) {
                LocalDate fechaLog = entry.getKey();
                DailyLog log = entry.getValue();
                
                // Verificar si la fecha está dentro del rango del corte (Inclusivo)
                boolean enRango = !fechaLog.isBefore(inicio) && !fechaLog.isAfter(fin);
                
                if (enRango) {
                    totalSueldo += log.getTotalPagoDia();
                    if (log.getTotalMinutosTrabajados() != null) {
                        totalHoras += log.getTotalMinutosTrabajados() / 60.0;
                    }
                }
            }
        }
        
        // 4. Llamar al servicio con los 4 argumentos requeridos
        if (PDFService.getPdf(empleado, totalHoras, totalSueldo, file.toPath().toString())) {
            Utils.showAlert("PDF creado exitosamente.", "Ya puedes ver tu PDF", "", Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void toggleButtons() {
        generandoFechaDeCorte = !generandoFechaDeCorte;
        
        // Botones normales
        btnGestionar.setManaged(!generandoFechaDeCorte);
        btnGestionar.setVisible(!generandoFechaDeCorte);
        btnPDF.setManaged(!generandoFechaDeCorte);
        btnPDF.setVisible(!generandoFechaDeCorte);
        
        // Controles de Rango
        toggleControlesRango(generandoFechaDeCorte);
        
        toggleBtn.setText(!generandoFechaDeCorte ? fechaDeCorteValue : opcionesAvanzadasValue);
    }
    
    private void toggleControlesRango(boolean visible) {
        datePickerInicio.setManaged(visible);
        datePickerInicio.setVisible(visible);
        
        if (lblGuion != null) {
            lblGuion.setManaged(visible);
            lblGuion.setVisible(visible);
        }
        
        datePickerFin.setManaged(visible);
        datePickerFin.setVisible(visible);
        
        btnSeleccionarFecha.setManaged(visible);
        btnSeleccionarFecha.setVisible(visible);
    }
    
    @FXML
    private void generarFechaDeCorte() {
        LocalDate fechaInicio = datePickerInicio.getValue();
        LocalDate fechaFin = datePickerFin.getValue();
        
        // Validaciones
        if (fechaInicio == null || fechaFin == null) {
            Utils.showAlert("Fechas Inválidas", "Selecciona inicio y fin.", "", Alert.AlertType.WARNING);
            return;
        }
        if (fechaFin.isBefore(fechaInicio)) {
            Utils.showAlert("Rango Inválido", "La fecha fin no puede ser anterior al inicio.", "", Alert.AlertType.WARNING);
            return;
        }
        if (fechaFin.isAfter(LocalDate.now())) {
            Utils.showAlert("Fecha Futura", "No puedes pagar días futuros.", "", Alert.AlertType.WARNING);
            return;
        }
        
        // Calcular pago en rango
        double totalPagar = 0.0;
        
        if (empleado.getLog() != null && empleado.getLog().getLogs() != null) {
            for (java.util.Map.Entry<LocalDate, DailyLog> entry : empleado.getLog().getLogs().entrySet()) {
                LocalDate fechaLog = entry.getKey();
                DailyLog log = entry.getValue();
                
                // Rango Inclusivo [Inicio, Fin]
                boolean dentroDelRango = !fechaLog.isBefore(fechaInicio) && !fechaLog.isAfter(fechaFin);
                
                if (dentroDelRango) {
                    totalPagar += log.getTotalPagoDia();
                }
            }
        }
        
        // --- GUARDAR CORTE Y ACTUALIZAR HISTORIAL ---
        empleado.setInicioCorte(fechaInicio);
        empleado.setFinCorte(fechaFin);
        
        // Añadir al historial
        Corte nuevoCorte = new Corte(fechaInicio, fechaFin, totalPagar);
        empleado.agregarCorte(nuevoCorte);
        
        // Persistencia
        ArrayList<Empleado> listaEmpleados = JSONService.readWorkersEdit();
        boolean guardado = false;
        
        for (int i = 0; i < listaEmpleados.size(); i++) {
            if (listaEmpleados.get(i).getId().equals(empleado.getId())) {
                listaEmpleados.set(i, empleado); 
                guardado = true;
                break;
            }
        }
        
        if (guardado) {
            JSONService.writeWorkersEdit(listaEmpleados);
            Utils.showAlert("Pago Registrado", 
            "Periodo: " + fechaInicio + " al " + fechaFin, 
            "Monto liquidado: $" + String.format("%.2f", totalPagar), 
            Alert.AlertType.INFORMATION);
            
            Session.setChanges(true);
            
            toggleButtons(); 
            // Refrescar DatePickers para el próximo ciclo
            cargarFechasEnDatePicker();
            actualizarVista();
            
        } else {
            Utils.showAlert("Error de Guardado", "No se pudo actualizar la base de datos.", "", Alert.AlertType.ERROR);
        }
    }
}