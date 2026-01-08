package com.albalatro.utils;

import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Utils {
    
    public static String stringArrayToString (ArrayList<String> array) {
        String cadena = "";
        
        for (int i = 0; i < array.size(); i++) {
            cadena += array.get(i);
            if(i < array.size()-1) cadena += "\n";
        }
        
        return cadena;
    }
    
    public static boolean showAlert(String title, String header, String content, AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        return alert.showAndWait().get() == ButtonType.OK;
    }
}
