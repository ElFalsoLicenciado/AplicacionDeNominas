package com.albalatro.model;

import java.util.Map;
import java.time.LocalDate;

/**
 * <h1> Clase {@code Log}</h1>
 * <p> POJO del Log, contiene los siguientes atributos:</p>
 * * <ul>
 *      <li> Map{@code <LocalDate, DailyLog>} {@link #logs}: Un {@code Map} que guarda los registros diarios de cada día</li>
 *  </ul>
 */

//Un atributo de tipo log en cada empleado, el log teniendo todos los dailylogs de ese empleado.
public class Log {
    private Map<LocalDate, DailyLog> logs;

    public Log() {}

    public Log(Map<LocalDate, DailyLog> logs) {
        this.logs = logs;
    }

    public Map<LocalDate, DailyLog> getLogs() {
        return logs;
    }

    public void setLogs(Map<LocalDate, DailyLog> logs) {
        this.logs = logs;
    }

    public void addDailyLog(LocalDate date, DailyLog dailyLog) {
        this.logs.put(date, dailyLog);
    }

    public void removeDailyLog(LocalDate date) {
        this.logs.remove(date);
    }
}
