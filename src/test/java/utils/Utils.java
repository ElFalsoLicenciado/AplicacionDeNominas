package utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Utils {
    
    public static String stringArrayToString (ArrayList<String> array) {
        String cadena = "";
        
        for (int i = 0; i < array.size(); i++) {
            cadena += array.get(i);
            if(i < array.size()-1) cadena += "\n";
        }
        
        return cadena;
    }
    
    public static void innitJSON(String folderName, String jsonName){
        Path folder = createFolder(folderName);
        createJSON(folder, jsonName);
    }


    @SuppressWarnings("CallToPrintStackTrace")
    private static Path createFolder(String name){
        
        Path folderPath = Paths.get(name); // relative path
        
        try {
            if (! Files.exists(folderPath)) Files.createDirectories(folderPath);
                // System.out.println("Folder created");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderPath;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static void createJSON(Path folder, String name){
        Path jsonFile = folder.resolve(name+".json");

        try{
            if (! Files.exists(jsonFile)) Files.writeString(jsonFile, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        

    
}
