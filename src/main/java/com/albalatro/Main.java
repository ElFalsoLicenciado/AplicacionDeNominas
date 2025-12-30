package com.albalatro;

import com.albalatro.service.JSONService;
import com.albalatro.utils.Utils;

public class Main {

    private static final String FOLDER = "src/json";
    private static final String JSON = "workers";
    public static void main(String[] args) {
        System.out.println("albalatro moment");
        // Empleado alberto = new com.albalatro.model.Empleado();
        Utils.innitJSON(FOLDER, JSON);
        JSONService.setFILE(String.format("%s/%s.json",FOLDER, JSON));
    }
}