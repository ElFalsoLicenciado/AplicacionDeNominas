package com.albalatro.model;

import java.time.LocalDate;
import java.util.ArrayList;

/** <h1> Clase {@code Dia}</h1>
*  <p> POJO del Dia, contiene los siguientes atributos:</p>
*  <ul>
*      <li> LocalDate {@link #fecha} </li>
*      <li> ArrayList{@code <String>} {@link #entradas}:  Un arreglo que guarda   las horas de entrada del empleado en ese dia.</li>
*  </ul>
*      <li> ArrayList{@code <String>} {@link #salidas}:  n arreglo que guarda   las horas de salida del empleado en ese dia.</li>
*  </ul>
* */
public class Dia implements java.io.Serializable{

    private LocalDate fecha;
    private ArrayList<String> entradas;
    private ArrayList<String> salidas;

    public Dia() {
    }

    public Dia (LocalDate fecha) {
        this.fecha = fecha;
        entradas = new ArrayList<>();
        salidas = new ArrayList<>();
                
    }

    public Dia(LocalDate fecha, ArrayList<String> entradas, ArrayList<String> salidas ) {
        this.fecha = fecha;
        this.entradas = entradas;
        this.salidas = salidas;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public ArrayList<String> getEntradas() {
        return entradas;
    }

    public void setEntradas(ArrayList<String> hours) {
        this.entradas = hours;
    }

    public ArrayList<String> getSalidas() {
        return salidas;
    }

    public void setSalidas(ArrayList<String> salidas) {
        this.salidas = salidas;
    }
    
    
    
}
