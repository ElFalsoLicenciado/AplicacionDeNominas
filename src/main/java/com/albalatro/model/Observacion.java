package com.albalatro.model;

import java.time.LocalDate;

public class Observacion {
    
    private LocalDate fecha;
    private String texto;

    public Observacion() {
    }

    public Observacion(LocalDate fecha, String texto) {
        this.fecha = fecha;
        this.texto = texto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return String.format("Fecha: %s\n%s" , fecha, texto);
    }


}
