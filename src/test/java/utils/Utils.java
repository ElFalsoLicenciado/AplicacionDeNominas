package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    
    private static Map<String, DayOfWeek> daysOfTheWeek;
    private static final String [] nameOfTheDays = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};

    public static void innitStuff(String folderName, String jsonName){
        innitJSON(folderName, jsonName);

        daysOfTheWeek = new HashMap<>();

        LocalDate aux = LocalDate.of(2025, 01, 06);

        for (int i = 0; i < 7; i++) {
            daysOfTheWeek.put(nameOfTheDays[i], aux.getDayOfWeek());
            aux = aux.plusDays(1);
        }
    }


    public static String stringArrayToString (ArrayList<String> array) {
        String cadena = "";
        
        for (int i = 0; i < array.size(); i++) {
            cadena += array.get(i);
            if(i < array.size()-1) cadena += "\n";
        }
        
        return cadena;
    }

    public static int timeToMinutes(String time) {

        int hour = Integer.parseInt(time.substring(0, 2));
        int minutes = Integer.parseInt(time.substring(3, 5));

        return (hour * 60) + minutes;
    }


    public static boolean isHourBetween(String start, String end, String hour) {
    int startMin = timeToMinutes(start);
    int endMin = timeToMinutes(end);
    int hourMin = timeToMinutes(hour);

    if (startMin <= endMin) {
        return hourMin >= startMin && hourMin <= endMin;
    } else {
        return hourMin >= startMin || hourMin <= endMin;
    }
}



    // public static ArrayList<String> orderMyHourList(ArrayList<String> array) {
    //     ArrayList<String> ordered = new ArrayList<>();

    //     if (array.size() == 1) return array;

    //     for (String h : array) {
            
    //     }

    //     return ordered;
    // }
    
    private static void innitJSON(String folderName, String jsonName){
        Path folder = createFolder(folderName);
        createJSON(folder, jsonName);
    }


    @SuppressWarnings("CallToPrintStackTrace")
    private static Path createFolder(String name){
        
        Path folderPath = Paths.get(name); // relative path
        
        try {
            if (! Files.exists(folderPath)) Files.createDirectories(folderPath);
                // System.out.println("Folder created");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderPath;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static void createJSON(Path folder, String name){
        Path jsonFile = folder.resolve(name+".json");

        try{
            if (! Files.exists(jsonFile)) Files.writeString(jsonFile, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DayOfWeek getDayOfWeek(String name) {
        return daysOfTheWeek.get(name);
    }
        

    
}
