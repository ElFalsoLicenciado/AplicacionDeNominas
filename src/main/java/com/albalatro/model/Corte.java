package com.albalatro.model;

import java.time.LocalDate;

public class Corte {
    private LocalDate inicio;
    private LocalDate fin;
    private Double monto;
    
    public Corte() {}

    public Corte(LocalDate inicio, LocalDate fin, Double monto) {
        this.inicio = inicio;
        this.fin = fin;
        this.monto = monto;
    }

    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }

    public LocalDate getFin() { return fin; }
    public void setFin(LocalDate fin) { this.fin = fin; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    @Override
    public String toString() {
        return inicio + " al " + fin + " ($" + monto + ")";
    }
}