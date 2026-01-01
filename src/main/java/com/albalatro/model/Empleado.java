package com.albalatro.model;

import java.util.ArrayList;
import java.util.UUID;

import com.albalatro.utils.Utils;

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
    private ArrayList<String> observaciones;
    private Status status;
    
    public Empleado() {}
    
    public Empleado(String nombre, String apellidoP, String apellidoM) {
        this.id = UUID.randomUUID().toString().replace("-", "").substring(0,10);
        this.nombre = nombre;
        this.apellidoM = apellidoM;
        this.apellidoP = apellidoP;
        observaciones = new ArrayList<>();
        status = Status.ALTA;
    }

    public Empleado(String id, String nombre, String apellidoP, String apellidoM, Log log,
            ArrayList<String> observaciones) {
        this.id = id;
        this.nombre = nombre;
        this.apellidoP = apellidoP;
        this.apellidoM = apellidoM;
        this.log = log;
        this.observaciones = observaciones;
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

    public ArrayList<String> getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(ArrayList<String> observaciones) {
        this.observaciones = observaciones;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }
    
    /** 
    * @return String
    */
    @Override
    public String toString() {
        return "Empleado{" +
        "nombre= '" + nombre + '\'' +
        ", apellidoP= '" + apellidoP + '\'' +
        ", apellidoM= '" + apellidoM + '\'' +
        ", observaciones= '" + Utils.stringArrayToString(observaciones) + '\'' +
        '}';
    }
}
