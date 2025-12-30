package com.albalatro.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.albalatro.utils.Utils;


/** <h1> Clase {@code Empleado}</h1>
 *  <p> POJO del Empleado, contiene los siguientes atributos:</p>
 *  <ul>
 *      <li> String {@link #nombre nombre}, String {@link #apellidoP} y String {@link #apellidoM} : Atributos para guardar el nombre del empleado. </li>
 *      <li> Map{@code <LocalDate, Double>} {@link #horasRegistradasPorDia}: Un {@code Map} que guarda la cantidad de horas de cada día</li>
 *      <li> ArrayList{@code <String>} {@link #observaciones}:  Un arreglo que permita guardar observaciones realizadas al empleado, esto sin depender de la fecha.</li>
 *  </ul>
 *
 * */

public class Empleado implements java.io.Serializable{
    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private Map<LocalDate, Double> horasRegistradasPorDia;
    private ArrayList<String> observaciones;


    public Empleado() {}

    public Empleado(String nombre, String apellidoP, String apellidoM) {
        this.nombre = nombre;
        this.apellidoM = apellidoM;
        this.apellidoP = apellidoP;
        horasRegistradasPorDia  = new HashMap<>();
        observaciones = new ArrayList<>();
    }

    public Empleado(String nombre, String apellidoP, String apellidoM, Map<LocalDate, Double> horasRegistradas, ArrayList<String> observaciones) {
        this.nombre = nombre;
        this.apellidoM = apellidoM;
        this.apellidoP = apellidoP;
        this.horasRegistradasPorDia = horasRegistradas;
        this.observaciones = observaciones;
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


    /** 
     * @return Map{@code <LocalDate, Double>}
     */
    public Map<LocalDate, Double> getHorasRegistradasPorDia() {
        return horasRegistradasPorDia;
    }

    /** 
     * @param horasRegistradas
     */
    public void setHorasRegistradasPorDia(Map<LocalDate, Double> horasRegistradas) {
        this.horasRegistradasPorDia = horasRegistradas;
    }


    public ArrayList<String> getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(ArrayList<String> observaciones) {
        this.observaciones = observaciones;
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
                ", horasRegistradasPorDia= " + horasRegistradasPorDia +
                ", observaciones= '" + Utils.stringArrayToString(observaciones) + '\'' +
                '}';
    }

    
}
