package com.albalatro.Modelo;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

public class Empleado {
    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private double pagoHora;
    private Map<LocalDate, Double> horasRegistradas = new HashMap<>();

    public Empleado() {}

    public Empleado(String nombre, String apellidoP, String apellidoM, double pagoHora, Map<LocalDate, Double> horasRegistradas) {
        this.nombre = nombre;
        this.apellidoM = apellidoM;
        this.apellidoP = apellidoP;
        this.pagoHora = pagoHora;
        this.horasRegistradas = horasRegistradas;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoP() {
        return apellidoP;
    }

    public void setApellidoP(String apellidoP) {
        this.apellidoP = apellidoP;
    }

    public String getApellidoM() {
        return apellidoM;
    }

    public void setApellidoM(String apellidoM) {
        this.apellidoM = apellidoM;
    }

    public double getPagoHora() {
        return pagoHora;
    }

    public void setPagoHora(double pagoHora) {
        this.pagoHora = pagoHora;
    }

    public Map<LocalDate, Double> getHorasRegistradas() {
        return horasRegistradas;
    }

    public void setHorasRegistradas(Map<LocalDate, Double> horasRegistradas) {
        this.horasRegistradas = horasRegistradas;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "nombre='" + nombre + '\'' +
                ", apellidoP='" + apellidoP + '\'' +
                ", apellidoM='" + apellidoM + '\'' +
                ", pagoHora=" + pagoHora +
                ", horasRegistradas=" + horasRegistradas +
                '}';
    }
    
}
