package com.albalatro.controller;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.filechooser.FileSystemView;

import com.albalatro.model.DailyLog;
import com.albalatro.model.Empleado;
import com.albalatro.model.Periodo;
import com.albalatro.service.JSONService;
import com.albalatro.service.PDFService;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;
import com.albalatro.utils.Utils;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import javafx.stage.Stage;

public class CalendarioController {
    
    @FXML private Label lblNombre;
    @FXML private Label lblApellidoP;
    @FXML private Label lblApellidoM;
    @FXML private Label lblTotalHoras;
    @FXML private Label lblTotalSueldo;
    @FXML private Label lblMesAno;
    @FXML private GridPane gridCalendario;
    @FXML private Button btnGestionar, btnObservaciones, btnPDF, toggleBtn, btnSeleccionarFecha, btnToggleResumen;
    @FXML private DatePicker datePickerCorte;
    
    private YearMonth mesActual;
    private Empleado empleado;
    private Double totalHoras = 0.0;
    private Double totalSueldo = 0.0;
    
    private Stage stageDetalle = null;
    private DetalleController controllerDetalle = null;

    private boolean generandoFechaDeCorte = false, verResumenMensual = true;
    private String fechaDeCorteValue = "Generar fecha de corte";
    private String opcionesAvanzadasValue = "Opciones avanzadas";
    
    @FXML
    public void initialize() {
        datePickerCorte.setValue(LocalDate.now());
        datePickerCorte.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Si la fecha es mayor que la fecha actual (mañana en adelante)...
                if (date.isAfter(LocalDate.now())) {
                    setDisable(true); // ... no se puede hacer clic.
                    setStyle("-fx-background-color: #ffc0cb;"); // Opcional: píntalo rojo suave para indicar error.
                }
            }
        });

        datePickerCorte.setVisible(generandoFechaDeCorte);
        datePickerCorte.setManaged(generandoFechaDeCorte);

        btnSeleccionarFecha.setVisible(generandoFechaDeCorte);
        btnSeleccionarFecha.setManaged(generandoFechaDeCorte);

        // Obtener el empleado de la sesión
        lblTotalSueldo.setText("$0.00");
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
    
    @FXML
    public void gestionarPressed() {
        System.out.println("Gestionando empleado " + empleado.getNombre());
        Session.setSalarioSeleccionado(JSONService.getSalario(empleado.getSalario()));
        Navigation.cambiarVista("/View/CrearEmpleadoView.fxml");
    }
    
    @FXML
    public void observacionesPressed() {
        Navigation.cambiarVista("/View/ListaObservacionesView.fxml");
    }
    
    private void actualizarVista() {
        // --- 1. Configuración de Cabecera y Grid ---
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"));
        lblMesAno.setText(mesActual.format(formatter).toUpperCase());
        
        // Limpiar grid y restricciones anteriores
        gridCalendario.getChildren().clear();
        gridCalendario.getRowConstraints().clear();
        
        // --- 2. Variables para el cálculo del MES VISIBLE ---
        double sueldoMesVisible = 0;
        double horasMesVisible = 0;
        
        LocalDate primerDiaDelMes = mesActual.atDay(1);
        int diasEnElMes = mesActual.lengthOfMonth();
        
        // Ajuste de inicio de semana (Domingo = 0)
        int diaSemanaInicio = primerDiaDelMes.getDayOfWeek().getValue(); 
        if (diaSemanaInicio == 7) diaSemanaInicio = 0; 
        
        // Calcular filas necesarias dinámicamente
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
        
        // --- 3. Bucle Principal: Dibujar Días y Sumar Mes ---
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
                
                // Acumulamos SIEMPRE para tener el dato del mes visible
                sueldoMesVisible += (pago != null ? pago : 0);
                horasMesVisible += (minutos != null ? minutos : 0);
                
                if (minutos != null && minutos > 0) {
                    double horas = minutos / 60.0;
                    textoHoras = String.format("%.1f h", horas);
                    colorFondo = "#E8F5E9"; // Fondo verde claro para días trabajados
                }
            }
            
            // Crear celda visual
            VBox celda = crearCeldaDia(i, fechaDia, textoHoras, colorFondo);
            gridCalendario.add(celda, columna, fila);
            
            // Control de columnas/filas
            columna++;
            if (columna > 6) { 
                columna = 0; 
                fila++; 
            }
        }
        
        // --- 4. Lógica de Visualización del Resumen (Toggle) ---
        if (verResumenMensual) {
            // MODO A: Ver Resumen del Mes Actual
            btnToggleResumen.setText("VISTA: MES ACTUAL ↻");
            lblTotalHoras.setText(String.format("%.1f h", horasMesVisible / 60.0));
            lblTotalSueldo.setText(String.format("$%.2f", sueldoMesVisible));
            
            // Aplicar ESTILO VERDE (Clásico)
            lblTotalSueldo.getStyleClass().removeAll("label-money-period");
            if (!lblTotalSueldo.getStyleClass().contains("label-money")) {
                lblTotalSueldo.getStyleClass().add("label-money");
            }
            // Limpiar estilos inline para que predomine el CSS
            lblTotalSueldo.setStyle("-fx-font-size: 18px;"); 
            
        } else {
            // MODO B: Ver Todo lo Pendiente
            calcularYMostrarPendientes();
        }
    }

    // --- Método Auxiliar Necesario ---
    private void calcularYMostrarPendientes() {
        double totalPendienteDinero = 0;
        long totalPendienteMinutos = 0;
        
        LocalDate ultimaFechaPagada = empleado.getUltimaFechaPagada();
        LocalDate hoy = LocalDate.now(); // Límite estricto: HOY
        
        if (empleado.getLog() != null && empleado.getLog().getLogs() != null) {
            for (java.util.Map.Entry<LocalDate, DailyLog> entry : empleado.getLog().getLogs().entrySet()) {
                LocalDate fechaLog = entry.getKey();
                DailyLog log = entry.getValue();
                
                // CONDICIONES:
                // 1. Debe ser POSTERIOR a la última fecha pagada (deuda nueva)
                // 2. Debe ser ANTERIOR O IGUAL a hoy (nada de pagar días futuros)
                boolean esPosteriorAlCorte = (ultimaFechaPagada == null) || fechaLog.isAfter(ultimaFechaPagada);
                boolean esAnteriorOIgualHoy = !fechaLog.isAfter(hoy);
                
                if (esPosteriorAlCorte && esAnteriorOIgualHoy) {
                    totalPendienteDinero += log.getTotalPagoDia();
                    totalPendienteMinutos += log.getTotalMinutosTrabajados();
                }
            }
        }
        
        // Actualizar UI para Modo Pendiente
        btnToggleResumen.setText("VISTA: PENDIENTE TOTAL ↻");
        lblTotalHoras.setText(String.format("%.1f h", totalPendienteMinutos / 60.0));
        lblTotalSueldo.setText(String.format("$%.2f", totalPendienteDinero));
        
        // Aplicar ESTILO AZUL VERDOSO (Nuevo)
        lblTotalSueldo.getStyleClass().removeAll("label-money");
        if (!lblTotalSueldo.getStyleClass().contains("label-money-period")) {
            lblTotalSueldo.getStyleClass().add("label-money-period");
        }
        lblTotalSueldo.setStyle(""); 
    }
    
    private VBox crearCeldaDia(int numeroDia, LocalDate fechaExacta, String textoHoras, String colorHex) {
        VBox celda = new VBox(2); 
        celda.getStyleClass().add("calendar-cell");
        
        // --- 1. OBTENER INFORMACIÓN PREVIA ---
        DailyLog logDelDia = null;
        if (empleado.getLog() != null && empleado.getLog().getLogs() != null) {
            logDelDia = empleado.getLog().getLogs().get(fechaExacta); 
        }

        boolean hayLog = (logDelDia != null);
        LocalDate fechaCorte = empleado.getUltimaFechaPagada();

        // --- 2. DETERMINAR ESTADO Y ESTILO ---
        boolean esFechaCorte = (fechaCorte != null) && fechaExacta.isEqual(fechaCorte);
        boolean esAnteriorYTrabajado = (fechaCorte != null) && fechaExacta.isBefore(fechaCorte) && hayLog;

        if (esFechaCorte) {
            celda.getStyleClass().add("calendar-cell-pago"); // Estilo destacado (Corte)
        } else if (esAnteriorYTrabajado) {
            celda.getStyleClass().add("calendar-cell-pagado"); // Estilo tenue (Ya pagado)
        }
        
        // Configuración base de la celda
        celda.setAlignment(Pos.TOP_LEFT);
        celda.setPadding(new javafx.geometry.Insets(3));
        celda.setMaxHeight(Double.MAX_VALUE); 
        
        // --- 3. CONSTRUCCIÓN DEL CONTENIDO ---
        Label lblDia = new Label(String.valueOf(numeroDia));
        lblDia.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblHoras = new Label(textoHoras);
        if (!textoHoras.isEmpty()) {
            lblHoras.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-font-size: 15px;");
        } else {
            lblHoras.setStyle("-fx-font-size: 1px;"); 
        }
        
        ArrayList<javafx.scene.Node> nodos = new ArrayList<>();
        nodos.add(lblDia);

        // Indicador visual de CORTE
        if (esFechaCorte) {
            Label lblIndicador = new Label("CORTE PAGO");
            lblIndicador.getStyleClass().add("label-corte");
            nodos.add(lblIndicador);
        }

        // Datos del Log (Periodos y Pago)
        if (hayLog) {
            ArrayList<String> array = new ArrayList<>(); 
            for(Periodo p : logDelDia.getPeriodos()) {
                array.add(p.toString());
            }
            
            Label lblPeriodos = new Label(Utils.stringArrayToStringSpace(array));
            lblPeriodos.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            lblPeriodos.setWrapText(true);
            
            double montoPago = logDelDia.getTotalPagoDia();
            Label lblPago = new Label(String.format("$%.2f", montoPago));
            lblPago.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 15px; -fx-font-weight: bold;");
            
            nodos.add(lblPeriodos);
            nodos.add(lblHoras);
            nodos.add(lblPago);
        } else {
            nodos.add(lblHoras);
        }

        celda.getChildren().addAll(nodos);
        
        // --- 4. EVENTOS DE MOUSE ---
        final String finalColor = colorHex; 
        
        celda.setOnMouseEntered(e -> {
            celda.setStyle("-fx-border-color: #aaa; -fx-background-color: #f0f0f0;");
        });
        
        celda.setOnMouseExited(e -> {
            // Restaurar estilos según el tipo de celda
            if (esFechaCorte) {
                celda.setStyle(""); 
            } else if (esAnteriorYTrabajado) {
                celda.setStyle(""); // Deja que actúe la clase .calendar-cell-pagado
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
            // Crear ventana si no existe
            if (stageDetalle == null || !stageDetalle.isShowing()) {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/View/DetalleView.fxml")
                );
                Parent root = loader.load();
                
                controllerDetalle = loader.getController();
                
                controllerDetalle.setOnDatosGuardados(() -> {
                    System.out.println("Datos actualizados desde detalle. Refrescando calendario...");
                    this.actualizarVista(); 
                });
                
                stageDetalle = new javafx.stage.Stage();
                stageDetalle.setScene(new javafx.scene.Scene(root));
                stageDetalle.setTitle("Gestión de Día");
                stageDetalle.setResizable(false);
                
                // Hacer que aparezca siempre encima
                stageDetalle.setAlwaysOnTop(false); 
                
                stageDetalle.show();
            } else {
                // Traer al frente si ya existe
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
        Stage stage = (Stage) btnPDF.getScene().getWindow();
        
        String userDesktop = System.getProperty("user.desktop"); 
        File escritorio = FileSystemView.getFileSystemView().getHomeDirectory();
        
        FileChooser fc = new FileChooser();
        fc.setTitle("Selecciona un directorio para guardar");
        fc.setInitialFileName("Registros de "+empleado.getNombre()+".pdf");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf")  
        );

        if (escritorio.exists() && escritorio.isDirectory()) {
            fc.setInitialDirectory(escritorio);
        } else {
            fc.setInitialDirectory(new File(userDesktop));
        }

        File file =fc.showSaveDialog(stage);

        if (file == null) return;
        
        if (PDFService.getPdf(empleado, totalHoras, totalSueldo ,file.toPath().toString()))
            Utils.showAlert("PDF creado exitosamente.", "Ya puedes ver tu PDF", "", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void toggleButtons() {
        generandoFechaDeCorte = !generandoFechaDeCorte;
        btnGestionar.setManaged(!generandoFechaDeCorte);
        btnGestionar.setVisible(!generandoFechaDeCorte);

        btnObservaciones.setManaged(!generandoFechaDeCorte);
        btnObservaciones.setVisible(!generandoFechaDeCorte);

        btnPDF.setManaged(!generandoFechaDeCorte);
        btnPDF.setVisible(!generandoFechaDeCorte);

        //-------------------------------------------------

        datePickerCorte.setManaged(generandoFechaDeCorte);
        datePickerCorte.setVisible(generandoFechaDeCorte);

        btnSeleccionarFecha.setManaged(generandoFechaDeCorte);
        btnSeleccionarFecha.setVisible(generandoFechaDeCorte);

        toggleBtn.setText(!generandoFechaDeCorte ? fechaDeCorteValue : opcionesAvanzadasValue);
    }

    @FXML
    private void generarFechaDeCorte() {
        LocalDate nuevaFechaCorte = datePickerCorte.getValue();
        
        if (nuevaFechaCorte != null) {
            // Calcular el pago total entre la última fecha de corte y la nueva fecha de corte
            LocalDate fechaCorteAnterior = empleado.getUltimaFechaPagada(); 
            
            double totalPagar = 0.0;
            
            if (empleado.getLog() != null && empleado.getLog().getLogs() != null) {
                for (java.util.Map.Entry<LocalDate, DailyLog> entry : empleado.getLog().getLogs().entrySet()) {
                    LocalDate fechaLog = entry.getKey();
                    DailyLog log = entry.getValue();
                    
                    // Condición: Fecha > CorteAnterior (si existe) Y Fecha <= NuevoCorte
                    boolean esPosteriorAlUltimoCorte = (fechaCorteAnterior == null) || fechaLog.isAfter(fechaCorteAnterior);
                    boolean esAnteriorOIgualAlNuevoCorte = !fechaLog.isAfter(nuevaFechaCorte);
                    
                    if (esPosteriorAlUltimoCorte && esAnteriorOIgualAlNuevoCorte) {
                        totalPagar += log.getTotalPagoDia();
                    }
                }
            }
            
            empleado.setUltimaFechaPagada(nuevaFechaCorte);
            
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
                
                // Feedback al usuario
                Utils.showAlert("Corte Generado Exitosamente", 
                    "Fecha de corte actualizada al: " + nuevaFechaCorte.toString(), 
                    "Monto total a liquidar en este corte: $" + String.format("%.2f", totalPagar), 
                    Alert.AlertType.INFORMATION);
                
                actualizarVista();
            } else {
                Utils.showAlert("Error de Guardado", "No se pudo actualizar la base de datos.", "", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void toggleResumenMode() {
        verResumenMensual = !verResumenMensual; // Invertir estado
        actualizarVista(); // Refrescar la pantalla con la nueva lógica
    }
}