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
import com.albalatro.model.Pago;
import com.albalatro.model.Salario;
import com.albalatro.utils.LocalDateTypeAdapter;
import com.albalatro.utils.LocalTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JSONService {
    
    // Ruta final que usará la aplicación
    public static String workers_file = "empleados.json"; 
    public static String wages_file = "salarios.json";
    
    // Configuración de rutas
    public static final String APP_FOLDER = "AlbalatroApp";
    public static final String DATA_FOLDER = "data";
    public static final String FILE_NAME[] = {"empleados.json", "salarios.json"}; 
    
    static {
        System.out.println("--- INICIANDO SERVICIO DE DATOS ---");
        try {
            String userHome = System.getProperty("user.home");
            
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
        innitWages();
    }
    
    private static void setFILE(int choice, String FILE) {
        switch (choice) {
            case 0 -> JSONService.workers_file = FILE;
            case 1 -> JSONService.wages_file = FILE; 
        }
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
    }

    private static Salario createBaseWage() {
        Salario base = new Salario();
        base.setId("BASE");
        base.setNombre("Salario base");
        base.setPago(Pago.HORA);
        base.setNormal(40.0);
        base.setDomingo(50.0);

        return base;
    }
    
    public static Gson createGson() {
        return new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
        .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter()) 
        .create();
        
    }
    
    // ==========================================
    // LECTURA
    // ==========================================
    public static ArrayList<Empleado> readWorkers() {
        return readWorkers(workers_file);
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
    
    public static ArrayList<Salario> readWages() {
        return readWages(wages_file);
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
    public static boolean writeWorkers(ArrayList<Empleado> workers) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(workers_file), StandardCharsets.UTF_8)) {
            Gson gson = createGson();
            gson.toJson(workers, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean writeWages(ArrayList<Salario> wages) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(wages_file), StandardCharsets.UTF_8)) {
            Gson gson = createGson();
            gson.toJson(wages, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Salario getSalario(String id) {
        for (Salario w: readWages()) {
            if(w.getId().equals(id)) {
                return w;
            }
        }
        return null;
    }
}