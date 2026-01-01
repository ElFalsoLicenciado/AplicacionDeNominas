package com.albalatro.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

//El dailylog ayuda a reducir el uso de tantos mapas y poder condensar la informacion diaria en un solo objeto
public class DailyLog {
    private LocalDate date;
    private ArrayList<Periodo> periodos;
    private Long totalMinutosTrabajados;
    private Double totalPagoDia;

    public DailyLog() { }

    public DailyLog(LocalDate date, ArrayList<Periodo> periodos) {
        this.date = date;
        this.periodos = periodos;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ArrayList<Periodo> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(ArrayList<Periodo> periodos) {
        this.periodos = periodos;
    }

    public void addPeriodo(Periodo periodo) {
        this.periodos.add(periodo);
    }

    public void removePeriodo(Periodo periodo) {
        this.periodos.remove(periodo);
    }

    public void calculateTotalMinutosTrabajados() {
        Long total = 0L;
        for (Periodo periodo : periodos) {
            total += periodo.getMinutosTrabajados();
        }
        totalMinutosTrabajados = total;
    }

    public Long getTotalMinutosTrabajados() {
        calculateTotalMinutosTrabajados();
        return totalMinutosTrabajados;
    }

    public void calculateTotalPagoDia() {
        if (periodos.isEmpty()) { // no hay periodos
            totalPagoDia = 0.0;
        }

        if (date == null) { // la fecha no está establecida
            totalPagoDia = 0.0;
        }

        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) { // Domingo
            totalPagoDia = (getTotalMinutosTrabajados() / 60.0) * 50.0;
        } else { // Entre semana
            totalPagoDia = (getTotalMinutosTrabajados() / 60.0) * 40.0;
        }
    }

    public Double getTotalPagoDia () {
        calculateTotalPagoDia();
        return totalPagoDia;
    }
}
