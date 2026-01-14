package com.albalatro.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyLog {
    
    private LocalDate date;
    private List<Periodo> periodos = new ArrayList<>();
    private Salario salario;
    
    // Inicializamos en 0 para evitar nulls en el JSON
    private Long totalMinutosTrabajados = 0L;
    private Double totalPagoDia = 0.0;
    private String falta;
    
    public DailyLog() { }
    
    public DailyLog(Salario salario, LocalDate date, ArrayList<Periodo> periodos) {
        this.salario = salario;
        this.date = date;
        this.periodos = periodos;
        actualizarCalculos(); // Calculamos una vez al crear
    }
    
    public Salario getSalario() {
        return salario;
    }

    public void setSalario(Salario salario) {
        this.salario = salario;
    }

    public Long getTotalMinutosTrabajados() {
        return totalMinutosTrabajados;
    }
    
    public Double getTotalPagoDia() {
        return totalPagoDia;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public List<Periodo> getPeriodos() {
        return periodos;
    }
    
    
    public void setDate(LocalDate date) {
        this.date = date;
        // Si cambia la fecha, puede cambiar si es domingo o no, así que recalculamos
        actualizarCalculos(); 
    }
    
    public void setPeriodos(ArrayList<Periodo> periodos) {
        this.periodos = periodos;
        // Si cambiamos toda la lista, recalculamos todo
        actualizarCalculos();
    }
    
    public void addPeriodo(Periodo periodo) {
        this.periodos.add(periodo);
        // Al agregar un periodo, sumamos sus minutos y actualizamos pago
        actualizarCalculos();
    }
    
    public void removePeriodo(Periodo periodo) {
        if (this.periodos.remove(periodo)) {
            // Solo recalculamos si realmente se borró algo
            actualizarCalculos();
        }
    }

    public String getFalta() {
        return falta;
    }

    public void setFalta(String falta) {
        this.falta = falta;
    }
    
    /**
    * Este método privado es el único responsable de hacer las matemáticas.
    * Se llama automáticamente cuando los datos cambian.
    */
    private void actualizarCalculos() {
        // 1. Calcular Minutos
        long totalMinutos = 0L;
        if (periodos != null) {
            for (Periodo p : periodos) {
                // Asumiendo que Periodo tiene un método getMinutosTrabajados que devuelve Long
                if (p != null) {
                    totalMinutos += p.getMinutosTrabajados();
                }
            }
        }
        this.totalMinutosTrabajados = totalMinutos;
        
        // 2. Calcular Pago (Depende de los minutos recién calculados)
        if (date == null) {
            this.totalPagoDia = 0.0;
            return;
        }
        
        // double tarifa = (date.getDayOfWeek() == DayOfWeek.SUNDAY) ? tarifa : tarifa_normal;
        switch (salario.getPago()) {
            case TipoPago.FIJO -> this.totalPagoDia = salario.getNormal();
            case TipoPago.HORA -> {
                double tarifa = (date.getDayOfWeek() == DayOfWeek.SUNDAY) ? salario.getDomingo() : salario.getNormal();
                this.totalPagoDia = (this.totalMinutosTrabajados / 60.0) * tarifa;
                this.totalPagoDia = Math.round(this.totalPagoDia * 100.0) / 100.0;
            }            
            
        }
    }
}

