// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Map;

// import com.albalatro.model.Dia;
// import com.albalatro.model.Empleado;
// import com.albalatro.service.JSONService;

// import utils.Utils;



// /** <h1> SECCION DE PRUEBAS</h1>
// * <ul> 
// *       <li> Cadenas bonitas </li>
// *       <li> Crear folder y archivo JSON </li>
// *       <li> Leer archivo JSON </li>
// *       <li> Escribir JSON </li>
//         <li> Crear calendario </li>
//         <li> Calculo de salario </li>
//         <li> Convertidor de horas a minutos</li>
//         <li> Metodo para ver si una hora no se incumbe con otra</li>
// * </ul>
// */

// public class testing {
    
//     public static ArrayList<String> arreglo = new ArrayList<>();
//     public static ArrayList<Empleado> workers = new ArrayList<>();
//     private static final String FOLDER = "src/json";
//     private static final String JSON = "workers";
//     // private static final boolean ACTION = false;
    
    
    
//     public static void main(String[] args) {
//         // CADENAS

//         // for(int i = 0; i < 5; i++ ) {
//         //     arreglo.add(i+"");
//         // }
//         // IO.println(cadenaBonita(arreglo));
        
//         // Utils.innitJSON("src/json", "workers");
        

//         // CREAR JSON

//         Utils.innitStuff(FOLDER, JSON);
//         // IO.println(String.format("%s%s.json",FOLDER, JSON));
//         JSONService.setFILE(String.format("%s/%s.json",FOLDER, JSON));
        
//         // LEER JSON

//         workers = JSONService.readWorkers();
        
        
//         // CREAR CALENDARIO

//         // LocalDate fecha = LocalDate.now();
//         // LocalDate aux = fecha;
//         // DayOfWeek dayOfWeek = fecha.getDayOfWeek();
//         // int weekCount = 2;

//         // while(weekCount != 0) {
//         //     if(aux.getDayOfWeek()==dayOfWeek && aux.getDayOfMonth() != fecha.getDayOfMonth()) weekCount--;
//         //     IO.println(aux.getDayOfWeek());
//         //     aux = aux.minusDays(1);
//         // }

        

//         // ESCRIBIR JSON
//         LocalDate date = LocalDate.now();

        

//         Empleado emp1 = new Empleado("Jorge", "Bernal", "");
        
//         if(!workers.isEmpty()) emp1.setId(workers.getFirst().getId());

//         Map<LocalDate, Dia> logMap = new HashMap<>();

//         Dia day = new Dia(date);

//         ArrayList<String> entradas = new ArrayList<>();
//         ArrayList<String> salidas = new ArrayList<>();

//         entradas.add("08:20");
//         entradas.add("13:20");
        
//         salidas.add("12:00");
//         salidas.add("20:00");
        

//         day.setEntradas(entradas);
//         day.setSalidas(salidas);

//         logMap.put(date, day);

//         // day = new Dia(LocalDate.of(2005, 12, 31));
//         // entradas = new ArrayList<>();
//         // salidas = new ArrayList<>();

//         // entradas.add("08:05");
//         // salidas.add("19:23");

//         // day.setEntradas(entradas);
//         // day.setSalidas(salidas);

//         // logMap.put(LocalDate.of(2005, 12, 31), day);

        
//         day = new Dia(date.minusDays(1));
//         entradas = new ArrayList<>();
//         salidas = new ArrayList<>();

//         entradas.add("08:13");
//         salidas.add("20:11");

//         day.setEntradas(entradas);
//         day.setSalidas(salidas);


//         logMap.put(date.minusDays(1),day);
        
//         //emp1.setEntradasYSalidasPorDia(logMap);

//         // Map<LocalDate, Double> horasMap = NominaService.getHoursWorked(logMap);

//         // horasMap.put(date,3.2);
//         // horasMap.put(LocalDate.of(2005, 12, 31),1.5);
//         // horasMap.put(date.minusDays(1),13.0);
        
//         //emp1.setHorasRegistradasPorDia(horasMap);
        
//         ArrayList<String> observaciones = new ArrayList<>();
//         for (int i = 0; i < 3; i++) {
//             observaciones.add(i+"");
//         }
        
//         emp1.setObservaciones(observaciones);
        
//         boolean checker = false;

//         for(Empleado w : workers) {
//             if (w.getId().equalsIgnoreCase(emp1.getId())) {
//                 workers.set(workers.indexOf(w), emp1);
//                 checker = true;
//                 break;
//             }
//         }
        
//         if(! checker) workers.add(emp1);

//         if( JSONService.writeWorkers(workers)) 
//             IO.println("SI");
//         else IO.println("NO");

//         // if(Utils.isHourBetween("06:00", "22:00", "21:59")) IO.println("En el rango");
//         // else IO.println("Fuera del rango");

//         // CALCULO DE SALARIOS

//         // Empleado emp = workers.get(0);

//         // Double wage = 0.0;
//         // Map<LocalDate, Double> horasMap = emp.getHorasRegistradasPorDia();

//         // for (LocalDate date : horasMap.keySet()) {
//         //     if(date.getDayOfWeek() == Utils.getDayOfWeek("Domingo")) 
//         //         wage+=  50*horasMap.get(date);
//         //     else wage+=  40*horasMap.get(date);

//         //     IO.println(String.format("Dia: %s Horas: %s",date.toString(), horasMap.get(date) ));
//         // }

//         // IO.println("Salario = " + wage);

        
        
//     }
    
//     // public static String cadenaBonita(ArrayList<String> array) {
//     //     String cadena = "";
    
//     //     for (int i = 0; i < array.size(); i++) {
//     //         cadena += array.get(i);
//     //         if(i < array.size()-1) cadena += "\n";
//     //     }
    
//     //     return cadena;
//     // }
    
    
    
// }
