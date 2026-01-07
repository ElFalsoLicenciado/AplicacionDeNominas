package com.albalatro.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets; // Importante
import java.nio.file.Files;               // Importante
import java.nio.file.Paths;               // Importante
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import com.albalatro.model.Empleado;
import com.albalatro.utils.LocalDateTypeAdapter;
import com.albalatro.utils.LocalTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JSONService {
    
    // Inicializa esto con el nombre de tu archivo base
    public static String FILE = "empleados.json"; 
    
    public static void setFILE(String FILE) {
        JSONService.FILE = FILE;
    }
    
    public static Gson createGson() {
        return new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
        .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter()) 
        .create();
    }
    
    // ==========================================
    // LECTURA (Corregida a UTF-8)
    // ==========================================
    
    public static ArrayList<Empleado> readWorkers() {
        return readWorkers(FILE);
    }
    
    public static ArrayList<Empleado> readWorkers(String path) {
        File archivo = new File(path);
        
        // Validación básica
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }
        
        // CAMBIO CLAVE: Usamos Files.newBufferedReader con UTF_8 explícito
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            Gson gson = createGson();
            Type listType = new TypeToken<ArrayList<Empleado>>(){}.getType();
            ArrayList<Empleado> workers = gson.fromJson(reader, listType);
            
            return workers != null ? workers : new ArrayList<>();
            
        } catch (Exception e) { 
            // CAMBIO CLAVE: Capturamos Exception general para atrapar errores de Gson (JsonSyntaxException)
            System.err.println("Error leyendo JSON en " + path + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // ==========================================
    // ESCRITURA (Corregida a UTF-8)
    // ==========================================
    
    public static boolean writeWorkers(ArrayList<Empleado> workers) {
        // CAMBIO CLAVE: Usamos Files.newBufferedWriter con UTF_8 explícito
        // Esto asegura que lo que guardas aquí sea compatible con lo que exportas/importas
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE), StandardCharsets.UTF_8)) {
            Gson gson = createGson();
            gson.toJson(workers, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}