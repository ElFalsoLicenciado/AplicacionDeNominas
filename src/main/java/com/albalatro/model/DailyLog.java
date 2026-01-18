package com.albalatro.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyLog {
    
    private LocalDate date;
    private List<Periodo> periodos = new ArrayList<>();
    private Salario salario;
    
    private Long totalMinutosTrabajados = 0L;
    private Double totalPagoDia = 0.0;
    private String notas;
    
    public DailyLog() { }
    
    public DailyLog(Salario salario, LocalDate date, ArrayList<Periodo> periodos) {
        this.salario = salario;
        this.date = date;
        this.periodos = periodos;
        actualizarCalculos(); 
    }
    
    public Salario getSalario() {
        return salario;
    }

    public void setSalario(Salario salario) {
        this.salario = salario;
        actualizarCalculos(); 
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
        actualizarCalculos(); 
    }
    
    public void setPeriodos(ArrayList<Periodo> periodos) {
        this.periodos = periodos;
        actualizarCalculos();
    }
    
    public void addPeriodo(Periodo periodo) {
        this.periodos.add(periodo);
        actualizarCalculos();
    }
    
    public void removePeriodo(Periodo periodo) {
        if (this.periodos.remove(periodo)) {
            actualizarCalculos();
        }
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    private void actualizarCalculos() {
        // 1. Calcular Minutos
        long totalMinutos = 0L;
        if (periodos != null) {
            for (Periodo p : periodos) {
                if (p != null) {
                    totalMinutos += p.getMinutosTrabajados();
                }
            }
        }
        this.totalMinutosTrabajados = totalMinutos;
        
        // 2. Calcular Pago 
        // --- PROTECCIÓN CONTRA NULOS AÑADIDA ---
        if (date == null || salario == null) {
            this.totalPagoDia = 0.0;
            return;
        }
        
        switch (salario.getPago()) {
            case TipoPago.FIJO -> this.totalPagoDia = salario.getNormal();
            case TipoPago.HORA -> {
                double tarifa = (date.getDayOfWeek() == DayOfWeek.SUNDAY) ? salario.getDomingo() : salario.getNormal();
                this.totalPagoDia = (this.totalMinutosTrabajados / 60.0) * tarifa;
                this.totalPagoDia = Math.round(this.totalPagoDia * 100.0) / 100.0;
            }            
        }
    }

    @Override
    public String toString() {
        return "DailyLog [date=" + date + ", periodos=" + periodos + ", salario=" + salario
                + ", totalMinutosTrabajados=" + totalMinutosTrabajados + ", totalPagoDia=" + totalPagoDia
                + ", notas=" + notas + "]";
    }
}