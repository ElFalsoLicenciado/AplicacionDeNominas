package com.albalatro.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class Empleado implements java.io.Serializable{
    private String id;
    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private Log log;
    private String salario;
    private Status status;
    private LocalDate inicioCorte; // Fecha contratación o inicio absoluto
    private LocalDate finCorte;    // Última fecha pagada
    private ArrayList<Corte> historialPagos; 
    
    public Empleado() {
        salario = "BASE";
        status = Status.ALTA;
        historialPagos = new ArrayList<>();
    }
    
    public Empleado(String nombre, String apellidoP, String apellidoM) {
        this.id = UUID.randomUUID().toString().replace("-", "").substring(0,10);
        this.nombre = nombre;
        this.apellidoM = apellidoM;
        this.apellidoP = apellidoP;
        salario = "BASE";
        status = Status.ALTA;
        historialPagos = new ArrayList<>();
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidoP() { return apellidoP; }
    public void setApellidoP(String apellidoP) { this.apellidoP = apellidoP; }
    public String getApellidoM() { return apellidoM; }
    public void setApellidoM(String apellidoM) { this.apellidoM = apellidoM; }
    public Log getLog() { return log; }
    public void setLog(Log log) { this.log = log; }
    public String getSalario() { return salario; }
    public void setSalario(String salario) { this.salario = salario; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public String getNombreCompleto() {
        return nombre + " " + apellidoP + " " + apellidoM;
    }
    
    public LocalDate getFinCorte() { return finCorte; }
    public void setFinCorte(LocalDate finCorte) { this.finCorte = finCorte; }
    
    public LocalDate getInicioCorte() { return inicioCorte; }
    public void setInicioCorte(LocalDate inicioCorte) { this.inicioCorte = inicioCorte; }

    public ArrayList<Corte> getHistorialPagos() {
        if (historialPagos == null) historialPagos = new ArrayList<>();
        return historialPagos;
    }

    public void setHistorialPagos(ArrayList<Corte> historialPagos) {
        this.historialPagos = historialPagos;
    }

    public void agregarCorte(Corte corte) {
        getHistorialPagos().add(corte);
    }
}