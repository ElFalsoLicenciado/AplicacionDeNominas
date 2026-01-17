package com.albalatro.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import net.bytebuddy.asm.Advice.Local;

/** <h1> Clase {@code Empleado}</h1>
*  <p> POJO del Empleado, contiene los siguientes atributos:</p>
*  <ul>
*      <li> String {@link #nombre nombre}, String {@link #apellidoP} y String {@link #apellidoM} : Atributos para guardar el nombre del empleado. </li>
*      <li> Map{@code <LocalDate, Dia>} {@link #entradasYSalidasPorDia}: Un {@code Map} que guarda la horas de entrada y salida de cada día</li>
*      <li> Map{@code <LocalDate, Double>} {@link #horasRegistradasPorDia}: Un {@code Map} que guarda la cantidad de horas trabajadas de cada día</li>
*      <li> ArrayList{@code <String>} {@link #observaciones}:  Un arreglo que permita guardar observaciones realizadas al empleado, esto sin depender de la fecha.</li>
*  </ul>
*
* */

public class Empleado implements java.io.Serializable{
    private String id;
    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private Log log;
    private String salario;
    private ArrayList<Observacion> observaciones;
    private Status status;
    private LocalDate fechaContratacion;
    private LocalDate ultimaFechaPagada;
    
    public Empleado() {
        salario = "BASE";
        observaciones = new ArrayList<>();
        status = Status.ALTA;
    }
    
    public Empleado(String nombre, String apellidoP, String apellidoM) {
        this.id = UUID.randomUUID().toString().replace("-", "").substring(0,10);
        this.nombre = nombre;
        this.apellidoM = apellidoM;
        this.apellidoP = apellidoP;
        salario = "BASE";
        observaciones = new ArrayList<>();
        status = Status.ALTA;
    }
    
    public Empleado(String id, String nombre, String apellidoP, String apellidoM, Log log, String salario,
            ArrayList<Observacion> observaciones, Status status, LocalDate ultimaFechaPagada, LocalDate fechaContratacion) {
        this.id = id;
        this.nombre = nombre;
        this.apellidoP = apellidoP;
        this.apellidoM = apellidoM;
        this.log = log;
        this.salario = salario;
        this.observaciones = observaciones;
        this.status = status;
        this.ultimaFechaPagada = ultimaFechaPagada;
        this.fechaContratacion = fechaContratacion;

        if (ultimaFechaPagada == null) {
            ultimaFechaPagada = this.fechaContratacion;
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    /** 
    * @return String
    */
    public String getNombre() {
        return nombre;
    }
    
    /** 
    * @param nombre
    */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    /** 
    * @return String
    */
    public String getApellidoP() {
        return apellidoP;
    }
    
    /** 
    * @param apellidoP
    */
    public void setApellidoP(String apellidoP) {
        this.apellidoP = apellidoP;
    }
    
    /** 
    * @return String
    */
    public String getApellidoM() {
        return apellidoM;
    }
    
    /** 
    * @param apellidoM
    */
    public void setApellidoM(String apellidoM) {
        this.apellidoM = apellidoM;
    }
    
    public Log getLog() {
        return log;
    }
    
    public void setLog(Log log) {
        this.log = log;
    }
    
    public String getSalario() {
        return salario;
    }
    
    public void setSalario(String salario) {
        this.salario = salario;
    }
    
    public ArrayList<Observacion> getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(ArrayList<Observacion> observaciones) {
        this.observaciones = observaciones;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getNombreCompleto() {
        return nombre + " " + apellidoP + " " + apellidoM;
    }
    
    public LocalDate getUltimaFechaPagada() {
        return ultimaFechaPagada;
    }

    public void setUltimaFechaPagada(LocalDate ultimaFechaPagada) {
        this.ultimaFechaPagada = ultimaFechaPagada;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    /** 
    * @return String
    */
    @Override
    public String toString() {
        return "Empleado [id=" + id + ", nombre=" + nombre + ", apellidoP=" + apellidoP + ", apellidoM=" + apellidoM
                + ", log=" + log + ", salario=" + salario + ", observaciones=" + observaciones + ", status=" + status
                + ", fechaContratacion=" + fechaContratacion + ", ultimaFechaPagada=" + ultimaFechaPagada + "]";
    }
    
}
