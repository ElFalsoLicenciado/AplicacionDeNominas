package com.albalatro.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * <h1> Clase {@code DailyLog}</h1>
 * <p> POJO del DailyLog, contiene los siguientes atributos:</p>
 * <ul>
 *     <li> LocalDate {@link #date} : Atributo para guardar la fecha del registro diario. </li>
 *     <li> LocalTime {@link #entryTime} : Atributo para guardar la hora de entrada del empleado en ese día. </li>
 *     <li> LocalTime {@link #exitTime} : Atributo para guardar la hora de salida del empleado en ese día. </li>
 *     <li> Double {@link #hoursWorked} : Atributo para guardar la cantidad de horas trabajadas en ese día. </li>
 * </ul>
 */

//El dailylog ayuda a reducir el uso de tantos mapas y poder condensar la informacion diaria en un solo objeto
public class DailyLog {
    private LocalDate date;
    private LocalTime entryTime;
    private LocalTime exitTime;
    private Double hoursWorked;
    private ArrayList<String> observaciones;
    
    public DailyLog() {}

    public DailyLog(LocalDate date, LocalTime entryTime, LocalTime exitTime, Double hoursWorked, ArrayList<String> observaciones) {
        this.date = date;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.hoursWorked = hoursWorked;
        this.observaciones = observaciones;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalTime exitTime) {
        this.exitTime = exitTime;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public ArrayList<String> getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(ArrayList<String> observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "DailyLog{" +
                "date=" + date +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                ", hoursWorked=" + hoursWorked +
                ", observaciones=" + observaciones +
                '}';
    }
}
