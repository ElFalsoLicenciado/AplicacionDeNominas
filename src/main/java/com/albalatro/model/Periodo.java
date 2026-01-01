package com.albalatro.model;

import java.time.*;

public class Periodo {
    private LocalTime inicio;
    private LocalTime fin;

    public Periodo() { }

    public Periodo(LocalTime inicio, LocalTime fin, Long minutosTrabajados) {
        this.inicio = inicio;
        this.fin = fin;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalTime inicio) {
        this.inicio = inicio;
    }

    public LocalTime getFin() {
        return fin;
    }

    public void setFin(LocalTime fin) {
        this.fin = fin;
    }

    public Long getMinutosTrabajados() {
        return Duration.between(inicio, fin).toMinutes();
    }
}
