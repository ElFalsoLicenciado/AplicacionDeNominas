package com.albalatro.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.albalatro.model.Empleado;
import com.albalatro.utils.LocalDateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class JSONService {
    public static String FILE = "";
    
    
    public static void setFILE(String FILE) {
        JSONService.FILE = FILE;
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    public static ArrayList<Empleado> readWorkers() {
        ArrayList<Empleado> workers = new ArrayList<>();
        
        try {
            String result;
            try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
                result = "";
                String line;
                while ((line = br.readLine()) != null) {
                    result += line;
                }
            }
            
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(result);
            Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .create();
            
            for (Object object : array) {
                Empleado worker = gson.fromJson(object.toString(), Empleado.class);
                workers.add(worker);
            }
            
        }catch (JsonSyntaxException | IOException | ParseException e) {
            e.printStackTrace();
        }
        
        return workers;
    }
    
    
    @SuppressWarnings("CallToPrintStackTrace")
    public static boolean writeWorkers(ArrayList<Empleado> workers) {
        try {
            Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .create();
            String json = gson.toJson(workers);
            
            try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FILE)
            )) {
                bw.write(json);
            }
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}