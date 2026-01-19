package com.albalatro.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import com.albalatro.model.Empleado;
import com.albalatro.model.Salario;
import com.albalatro.model.Status;
import com.albalatro.service.JSONService;
import com.albalatro.utils.Navigation;
import com.albalatro.utils.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button; // Importado
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class CrearEmpleadoController {
    
    @FXML private TextField txtNombre, txtApellidoP, txtApellidoM;
    @FXML private Label lblError;
    @FXML private Button btnGuardar, btnAlta, btnBaja; // btnSalario eliminado
    
    @FXML private ComboBox<Salario> comboSalarios; 
    
    private Empleado empleado;
    
    @FXML
    public void initialize() {
        empleado = Session.getEmpleadoSeleccionado();
        boolean isEdit = (empleado != null);
        
        // NUEVO MÉTODO
        cargarSalariosEnCombo();
        
        if (isEdit) {
            
            Salario salario = JSONService.getSalario(empleado.getSalario());
            
            if (salario != null) {
                for (Salario s : comboSalarios.getItems()) {
                    if (s.getId().equals(salario.getId())) {
                        comboSalarios.getSelectionModel().select(s);
                        break;
                    }
                }
            }
            txtNombre.setText(empleado.getNombre());
            txtApellidoP.setText(empleado.getApellidoP());
            txtApellidoM.setText(empleado.getApellidoM());
            // Aquí deberás añadir lógica para seleccionar el salario actual del empleado en el combo
        }
        
        btnAlta.setVisible(isEdit && empleado.getStatus() == Status.BAJA);
        btnAlta.setManaged(isEdit && empleado.getStatus() == Status.BAJA);
        btnBaja.setManaged(isEdit && empleado.getStatus() == Status.ALTA);
        btnBaja.setVisible(isEdit && empleado.getStatus() == Status.ALTA);
        
        // Listeners de teclado...
        txtNombre.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                txtApellidoP.requestFocus();
        });
        txtApellidoP.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                txtApellidoM.requestFocus();
        });
        txtApellidoM.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                btnGuardar.fire();
        });
    }
    
    private void cargarSalariosEnCombo() {
        ArrayList<Salario> listado = JSONService.readWagesEdit();
        ArrayList<Salario> salarios = new ArrayList<>();
        
        for (Salario w : listado) {
            if (w.getStatus().equals(Status.ALTA))
                salarios.add(w);
        }
        comboSalarios.setItems(FXCollections.observableArrayList(salarios));
        comboSalarios.setConverter(new StringConverter<Salario>() {
            @Override
            public String toString(Salario s) {
                if (s == null) return null;
                // Ejemplo de visualización: "Cajero - $200.00"
                return s.getNombre() + " - $" + s.getNormal(); 
            }
            
            @Override
            public Salario fromString(String string) {
                // Este método no se suele usar en ComboBoxes de solo lectura/selección
                return comboSalarios.getItems().stream()
                .filter(s -> s.getNombre().equals(string))
                .findFirst().orElse(null);
            }
        });
    }
    
    // NUEVO MÉTODO VACÍO (Reemplaza a gestionarSalario)
    @FXML
    private void abrirCrearSalarioModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/SalarioView.fxml"));
            Parent root = loader.load();
            
            // Obtenemos el controlador
            SalarioController controller = loader.getController();
            controller.setEsModal(true); 
            
            // Configuración de datos si fuera necesario
            // controller.setDatosSalario(null); // Es nuevo
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás
            stage.showAndWait();
            
            // Al cerrarse (showAndWait termina), recargamos el combo
            cargarSalariosEnCombo();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void guardar() {
        String n = txtNombre.getText(), ap = txtApellidoP.getText(), am = txtApellidoM.getText();
        if (n.isEmpty() || ap.isEmpty() || am.isEmpty() || comboSalarios.getValue() == null) {
            mostrarError("Por favor llena todos los campos.");
            return;
        }
        
        ArrayList<Empleado> lista = JSONService.readWorkersEdit();
        Empleado target = (empleado == null) ? new Empleado() : empleado;
        
        setData(target, n, ap, am);
        target.setSalario(comboSalarios.getValue().getId());
        
        if (empleado == null) {
            target.setId(UUID.randomUUID().toString());
            lista.add(target);
        } else {
            // target.setSalario(...); 
            actualizarEnLista(lista, target);
        }
        
        if (JSONService.writeWorkersEdit(lista)) {
            Navigation.empleadoGuardadoCustomHistory(target);
            Session.setChanges(true);
        }
        else
            mostrarError("Error al escribir en el archivo JSON.");
    }
    
    @FXML
    public void cancelar() {
        Navigation.goBack();
    }
    
    @FXML
    public void darDeBaja() {
        if (cambiarStatus(Status.BAJA)) {
            Navigation.irAListaEmpleados();
        }
    }
    
    @FXML
    public void darDeAlta() {
        if (cambiarStatus(Status.ALTA)) {
            Navigation.irAListaEmpleados();
        }
    }
    
    private boolean cambiarStatus(Status s) {
        empleado.setStatus(s);
        ArrayList<Empleado> lista = JSONService.readWorkersEdit();
        actualizarEnLista(lista, empleado);
        
        boolean exito = JSONService.writeWorkersEdit(lista);
        if (exito)
            System.out.println("Status actualizado a " + s);
        return exito;
    }
    
    private void actualizarEnLista(ArrayList<Empleado> lista, Empleado target) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId().equals(target.getId())) {
                lista.set(i, target);
                break;
            }
        }
    }
    
    private void setData(Empleado emp, String n, String ap, String am) {
        emp.setNombre(n);
        emp.setApellidoP(ap);
        emp.setApellidoM(am);
    }
    
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}