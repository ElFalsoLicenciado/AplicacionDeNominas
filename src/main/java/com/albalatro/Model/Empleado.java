package com.albalatro.Model;

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
}
