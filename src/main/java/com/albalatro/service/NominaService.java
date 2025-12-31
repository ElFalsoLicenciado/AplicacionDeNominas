package com.albalatro.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.albalatro.model.Dia;
import com.albalatro.utils.Utils;

public class NominaService {
    
    public static Map<LocalDate, Double> getHoursWorked(Map<LocalDate, Dia> entriesLog) {
        Map<LocalDate, Double> hoursMap = new HashMap<>();
        for (LocalDate date : entriesLog.keySet()) {
            Double hours = 0.0;
            ArrayList<String> entradas = entriesLog.get(date).getEntradas();
            ArrayList<String> salidas = entriesLog.get(date).getSalidas();
            
            for(int i = 0; i < entradas.size(); i++) {
                int startMin = Utils.timeToMinutes(entradas.get(i));
                int endMin = Utils.timeToMinutes(salidas.get(i));
                
                // IO.println(String.format("Hora:%s en minutos: %s", entradas.get(i), startMin));
                // IO.println(String.format("Hora:%s en minutos: %s", salidas.get(i), endMin));
                
                hours += (endMin - startMin) / 60.0;
                
            }
            hours = BigDecimal.valueOf(hours)
            .setScale(1, RoundingMode.HALF_UP)
            .doubleValue();
            hoursMap.put(date, hours);
        }
        
        return hoursMap;
    }

    public static Map<LocalDate, Double> getWage(Map<LocalDate, Double> hoursMap) {
        
    }
}
