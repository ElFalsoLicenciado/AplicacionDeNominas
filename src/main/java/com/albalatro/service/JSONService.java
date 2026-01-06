package com.albalatro.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
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
    public static String FILE = "";

    public static void setFILE(String FILE) {
        JSONService.FILE = FILE;
    }

    // Configuración centralizada de Gson para no repetir código
    private static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter()) 
                .create();
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static ArrayList<Empleado> readWorkers() {
        //Si el archivo no existe o está vacío, retornamos lista nueva.
        File archivo = new File(FILE);
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            Gson gson = createGson();
            
            // Leer listas genericas
            Type listType = new TypeToken<ArrayList<Empleado>>(){}.getType();
            ArrayList<Empleado> workers = gson.fromJson(reader, listType);

            // Si el archivo contenía "null" o estaba corrupto, devolvemos lista vacía
            return workers != null ? workers : new ArrayList<>();

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static boolean writeWorkers(ArrayList<Empleado> workers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
            Gson gson = createGson();
            gson.toJson(workers, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}