package com.albalatro.model;

import java.time.Duration;
import java.time.LocalTime;

public class Periodo {
    private LocalTime entrada;
    private LocalTime salida;

    public Periodo() { }

    public Periodo(LocalTime inicio, LocalTime fin, Long minutosTrabajados) {
        this.entrada = inicio;
        this.salida = fin;
    }

    public LocalTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalTime inicio) {
        this.entrada = inicio;
    }

    public LocalTime getSalida() {
        return salida;
    }

    public void setSalida(LocalTime fin) {
        this.salida = fin;
    }

    public Long getMinutosTrabajados() {
        return Duration.between(entrada, salida).toMinutes();
    }

    @Override
    public String toString() {
        return String.format("%s-%s", entrada, salida);
    }
}
