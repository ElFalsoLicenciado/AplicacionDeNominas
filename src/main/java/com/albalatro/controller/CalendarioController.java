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
    @FXML private Button btnGestionar, btnObservaciones, btnPDF, toggleBtn, btnSeleccionarFecha;
    @FXML private DatePicker datePicker;
    
    private YearMonth mesActual;
    private Empleado empleado;
    
    private Stage stageDetalle = null;
    private DetalleController controllerDetalle = null;

    private boolean generandoFechaDeCorte = false;
    private String fechaDeCorteValue = "Generar fecha de corte";
    private String opcionesAvanzadasValue = "Opciones avanzadas";
    
    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        datePicker.setVisible(generandoFechaDeCorte);
        datePicker.setManaged(generandoFechaDeCorte);

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
    
    // TODONT: Crear una nueva vista para poder acceder al panel de edicion del empleado.
    // TODONT: Tras haber creado la vista, hacer que este evento invoque a la nueva vista.
    // TODONT: Añadir sus respectivos eventos y funcoinalidad a la vista de editar empleado.
    // TODO: Editar el sueldo individual del empleado, sea pagar por día o sepa.
    // Para lograr lo de arriba se puede copiar el codigo de #abrirVentanaDetalle() 
    // TODONT: refactorizar el codigo para abrir una nueva ventana en un metodo nuevo para evitar duplicidad y redundancia
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
            
            VBox celda = crearCeldaDia(i, fechaDia, textoHoras, colorFondo);
            
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
    
    private VBox crearCeldaDia(int numeroDia, LocalDate fechaExacta, String textoHoras, String colorHex) {
        VBox celda = new VBox(2); 
        celda.getStyleClass().add("calendar-cell");
        celda.setAlignment(Pos.TOP_LEFT);
        celda.setPadding(new javafx.geometry.Insets(3));
        celda.setMaxHeight(Double.MAX_VALUE); 
        
        // Número del día 
        Label lblDia = new Label(String.valueOf(numeroDia));
        lblDia.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Horas trabajadas 
        Label lblHoras = new Label(textoHoras);
        // Si hay horas, le ponemos fondo blanco para resaltar sobre el verde, o negrita
        if (!textoHoras.isEmpty()) {
            lblHoras.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-font-size: 15px;");
        } else {
            lblHoras.setStyle("-fx-font-size: 1px;"); // Truco: si no hay horas, que no ocupe espacio
        }
        
        DailyLog logDelDia = null;
        double montoPago = 0.0;
        
        // Verificar si un empleado tiene logs
        if (empleado.getLog() != null && empleado.getLog().getLogs() != null) {
            logDelDia = empleado.getLog().getLogs().get(fechaExacta); 
        }
        
        // Si tiene logs, verificar que un log de un día exacto exista
        if (logDelDia != null) {
            ArrayList<String> array = new ArrayList<>(); 
            for(Periodo p : logDelDia.getPeriodos()) {
                array.add(p.toString());
            }
            
            Label lblPeriodos = new Label (Utils.stringArrayToStringSpace(array));
            lblPeriodos.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            lblPeriodos.wrapTextProperty();
            
            montoPago = logDelDia.getTotalPagoDia();
            Label lblPago = new Label(String.format("$%.2f", montoPago));
            
            lblPago.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 15px; -fx-font-weight: bold;");
            celda.getChildren().addAll(lblDia,lblPeriodos, lblHoras, lblPago);
            
        } else { // Si no tiene log de un dia específico, no se muestra información de pago ese dia.
            celda.getChildren().addAll(lblDia, lblHoras);
        }
        
        final String finalColor = colorHex; 
        celda.setOnMouseEntered(e -> celda.setStyle("-fx-border-color: #aaa; -fx-background-color: #f0f0f0;"));
        celda.setOnMouseExited(e -> celda.setStyle("-fx-border-color: #eee; -fx-background-color: " + finalColor + ";"));
        
        celda.setOnMouseClicked(event -> {
            abrirVentanaDetalle(fechaExacta);
        });
        
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
        
        if (PDFService.getPdf(empleado, file.toPath().toString()))
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

        datePicker.setManaged(generandoFechaDeCorte);
        datePicker.setVisible(generandoFechaDeCorte);

        btnSeleccionarFecha.setManaged(generandoFechaDeCorte);
        btnSeleccionarFecha.setVisible(generandoFechaDeCorte);

        toggleBtn.setText(!generandoFechaDeCorte ? fechaDeCorteValue : opcionesAvanzadasValue);
    }

    @FXML
    private void generarFechaDeCorte() {
        LocalDate fechaSeleccionada = datePicker.getValue();
        if (fechaSeleccionada != null) {
            mesActual = YearMonth.from(fechaSeleccionada);
            actualizarVista();
        }
    }
}