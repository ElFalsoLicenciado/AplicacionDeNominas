package com.albalatro.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List; // Usar interfaz List es mejor práctica

public class DailyLog {
    
    private static final double TARIFA_DOMINGO = 50.0;
    private static final double TARIFA_NORMAL = 40.0;

    private LocalDate date;
    private List<Periodo> periodos = new ArrayList<>();
    
    // Inicializamos en 0 para evitar nulls en el JSON
    private Long totalMinutosTrabajados = 0L;
    private Double totalPagoDia = 0.0;

    public DailyLog() { }

    public DailyLog(LocalDate date, ArrayList<Periodo> periodos) {
        this.date = date;
        this.periodos = periodos;
        actualizarCalculos(); // Calculamos una vez al crear
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

        double tarifa = (date.getDayOfWeek() == DayOfWeek.SUNDAY) ? TARIFA_DOMINGO : TARIFA_NORMAL;
        
        this.totalPagoDia = (this.totalMinutosTrabajados / 60.0) * tarifa;
        this.totalPagoDia = Math.round(this.totalPagoDia * 100.0) / 100.0;
    }
}