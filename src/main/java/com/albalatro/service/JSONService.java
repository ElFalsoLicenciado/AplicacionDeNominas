package com.albalatro.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import com.albalatro.model.Empleado;
import com.albalatro.model.Salario;
import com.albalatro.model.Status;
import com.albalatro.model.TipoPago;
import com.albalatro.utils.LocalDateTypeAdapter;
import com.albalatro.utils.LocalTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JSONService {
    
    // Ruta final que usará la aplicación
    public static String workers_file = "empleados.json";
    public static String workers_edit = "empleadosEdit.json"; 
    public static String wages_file = "salarios.json";
    public static String wages_edit = "salariosEdit.json";
    
    // Configuración de rutas
    public static final String APP_FOLDER = "AlbalatroApp";
    public static final String DATA_FOLDER = "data";
    public static final String FILE_NAME[] = {"empleados.json", "empleadosEdit.json", "salarios.json", "salariosEdit.json"}; 

    //User Home
    private static String userHome = System.getProperty("user.home");
    
    static {
        System.out.println("--- INICIANDO SERVICIO DE DATOS ---");
        try {
            // Ruta destino (C:/Users/Usuario/AlbalatroApp/data/empleados.json)
            File folderPath = new File(userHome, APP_FOLDER + File.separator + DATA_FOLDER);
            
            for (int i = 0; i < FILE_NAME.length; i++) {
                File destinationFile = new File(folderPath, FILE_NAME[i]);
                
                IO.println("Ruta destino deseada: " + destinationFile.getAbsolutePath());
                if (!folderPath.exists()) {
                    folderPath.mkdirs();
                }
                
                // Lógica de Migración
                if (! destinationFile.exists()) {
                    IO.println("   -> Destino vacío. Buscando respaldo local...");

                    destinationFile.createNewFile();
                    Files.writeString(destinationFile.toPath(), "[]", StandardCharsets.UTF_8);
                } else {
                    IO.println("   -> Archivo de datos cargado correctamente.");
                }
                
                setFILE(i,destinationFile.getAbsolutePath());
            }            
        } catch (IOException e) {
            e.printStackTrace();
        }
        innitEdits();
        innitWages();
    }
    
    private static void setFILE(int choice, String FILE) {
        switch (choice) {
            case 0 -> JSONService.workers_file = FILE;
            case 1 -> JSONService.workers_edit = FILE;
            case 2 -> JSONService.wages_file = FILE; 
            case 3 -> JSONService.wages_edit = FILE;
        }
    }

    public static void innitEdits(){
        writeWorkersEdit(readWorkers());
        writeWagesEdit(readWages());
    }

    private static void innitWages(){
        ArrayList<Salario> wages = readWages();

        Salario base =  createBaseWage();
        boolean found = false;

        for(Salario w : wages) {
            if(w.getId().equals(base.getId())) {
                found = true;
                break;
            }
        }

        if (! found) {
            wages.add(base);
        }

        writeWages(wages);
        writeWagesEdit(wages);
    }

    private static Salario createBaseWage() {
        Salario base = new Salario();
        base.setId("BASE");
        base.setNombre("Salario base");
        base.setPago(TipoPago.HORA);
        base.setNormal(40.0);
        base.setDomingo(50.0);
        base.setStatus(Status.ALTA);

        return base;
    }
    
    public static Gson createGson() {
        return new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
        .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter()) 
        .create();
        
    }

    public static Empleado testing1(int index) {
        return readWorkers().get(index);
    }
    
    // ==========================================
    // LECTURA
    // ==========================================
    public static ArrayList<Empleado> readWorkers() {
        return readWorkers(workers_file);
    }

    public static ArrayList<Empleado> readWorkersEdit() {
        return readWorkers(workers_edit);
    }
    
    public static ArrayList<Empleado> readWorkers(String path) {
        File archivo = new File(path);
        
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            Gson gson = createGson();
            Type listType = new TypeToken<ArrayList<Empleado>>(){}.getType();
            ArrayList<Empleado> workers = gson.fromJson(reader, listType);
            return workers != null ? workers : new ArrayList<>();
        } catch (Exception e) { 
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private static ArrayList<Salario> readWages() {
        return readWages(wages_file);
    }
    
    public static ArrayList<Salario> readWagesEdit() {
        return readWages(wages_edit);
    }

    public static ArrayList<Salario> readWages(String path) {
        File archivo = new File(path);
        
        if (! archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            Gson gson = createGson();
            Type listType = new TypeToken<ArrayList<Salario>>(){}.getType();
            ArrayList<Salario> wages = gson.fromJson(reader, listType);
            return wages != null ? wages : new ArrayList<>();
        } catch (Exception e) { 
            e.printStackTrace();
            return new ArrayList<>();
        }
        
    }
    
    // ==========================================
    // ESCRITURA
    // ==========================================
    private static boolean writeWorkers(ArrayList<Empleado> workers) {
        return writeWorkers(workers, workers_file);
    }
    
    public static boolean writeWorkersEdit(ArrayList<Empleado> wages) {
        return writeWorkers(wages, workers_edit);
    }
    
    public static boolean writeWorkers(ArrayList<Empleado> workers, String path) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
            Gson gson = createGson();
            gson.toJson(workers, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean writeWages(ArrayList<Salario> wages) {
        return writeWages(wages, wages_file);
    }
    
    public static boolean writeWagesEdit(ArrayList<Salario> wages) {
        return writeWages(wages, wages_edit);
    }
    
    public static boolean writeWages(ArrayList<Salario> wages, String path) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
            Gson gson = createGson();
            gson.toJson(wages, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Salario getSalario(String id) {
        for (Salario w: readWagesEdit()) {
            if(w.getId().equals(id)) {
                return w;
            }
        }
        return null;
    }

    public static void removeEdits(){
        try {
            File workers_json = new File(workers_edit);
            workers_json.delete();

            File wages_json = new File(wages_edit);
            wages_json.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveChanges(){
        writeWorkers(readWorkersEdit());
        writeWages(readWagesEdit());
    }
}