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
import com.albalatro.utils.LocalDateTypeAdapter;
import com.albalatro.utils.LocalTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JSONService {
    
    // Ruta final que usará la aplicación
    public static String FILE = "empleados.json"; 

    // Configuración de rutas
    public static final String APP_FOLDER = "AlbalatroApp";
    public static final String DATA_FOLDER = "data";
    public static final String FILE_NAME = "empleados.json"; 
    
    static {
        System.out.println("--- INICIANDO SERVICIO DE DATOS ---");
        try {
            String userHome = System.getProperty("user.home");
            
            // Ruta destino (C:/Users/Usuario/AlbalatroApp/data/empleados.json)
            File folderPath = new File(userHome, APP_FOLDER + File.separator + DATA_FOLDER);
            File destinationFile = new File(folderPath, FILE_NAME);

            // Ruta Origen para migración
            File localFolder = new File(System.getProperty("user.dir"), "data");
            File localFile = new File(localFolder, FILE_NAME); 

            System.out.println("1. Buscando archivo origen en: " + localFile.getAbsolutePath());
            System.out.println("2. Ruta destino deseada: " + destinationFile.getAbsolutePath());

            if (!folderPath.exists()) {
                folderPath.mkdirs();
            }

            // Lógica de Migración
            if (!destinationFile.exists()) {
                System.out.println("   -> Destino vacío. Buscando respaldo local...");
                
                if (localFile.exists()) {
                    System.out.println("   -> ¡Respaldo encontrado! Copiando...");
                    Files.copy(localFile.toPath(), destinationFile.toPath());
                } else {
                    System.out.println("   -> No se encontró respaldo local. Creando DB nueva.");
                    destinationFile.createNewFile();
                    Files.writeString(destinationFile.toPath(), "[]", StandardCharsets.UTF_8);
                }
            } else {
                System.out.println("   -> Archivo de datos cargado correctamente.");
            }

            setFILE(destinationFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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
    // LECTURA
    // ==========================================
    public static ArrayList<Empleado> readWorkers() {
        return readWorkers(FILE);
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
    
    // ==========================================
    // ESCRITURA
    // ==========================================
    public static boolean writeWorkers(ArrayList<Empleado> workers) {
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