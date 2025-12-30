import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.albalatro.model.Empleado;
import com.albalatro.service.JSONService;

import utils.Utils;

public class testing {
    
    public static ArrayList<String> arreglo = new ArrayList<>();
    public static ArrayList<Empleado> workers = new ArrayList<>();
    private static final String FOLDER = "src/json";
    private static final String JSON = "workers";
    private static final boolean ACTION = false;
    
    public static void main(String[] args) {
        // for(int i = 0; i < 5; i++ ) {
        //     arreglo.add(i+"");
        // }
        // IO.println(cadenaBonita(arreglo));
        
        // Utils.innitJSON("src/json", "workers");
        
        Utils.innitJSON(FOLDER, JSON);
        // IO.println(String.format("%s%s.json",FOLDER, JSON));
        JSONService.setFILE(String.format("%s/%s.json",FOLDER, JSON));
        
        workers = JSONService.readWorkers();
        
        
        Empleado emp1 = new Empleado("Jorge", "Bernal", "");
        
        Map<LocalDate, Double> horasMap = new HashMap<>();
        
        LocalDate date = LocalDate.now();
        horasMap.put(date,3.2);
        horasMap.put(LocalDate.of(2005, 12, 31),1.5);
        horasMap.put(date.minusDays(1),13.0);
        
        emp1.setHorasRegistradasPorDia(horasMap);
        
        ArrayList<String> observaciones = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            observaciones.add(i+"");
        }
        
        emp1.setObservaciones(observaciones);
        
        workers.add(emp1);
        
        if( JSONService.writeWorkers(workers)) 
            IO.println("SI");
        else IO.println("NO");
        
        
        
    }
    
    // public static String cadenaBonita(ArrayList<String> array) {
    //     String cadena = "";
    
    //     for (int i = 0; i < array.size(); i++) {
    //         cadena += array.get(i);
    //         if(i < array.size()-1) cadena += "\n";
    //     }
    
    //     return cadena;
    // }
    
    
    
}
