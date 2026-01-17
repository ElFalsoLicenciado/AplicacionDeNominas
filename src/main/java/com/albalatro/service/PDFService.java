package com.albalatro.service;

import java.io.IOException;

import com.albalatro.model.Empleado;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class PDFService {
    
    

    public static boolean getPdf(Empleado empleado, String path){
        try{
            PdfWriter writer = new PdfWriter(path);
            
            PdfDocument pdf = new PdfDocument(writer);
            
            try (Document doc = new Document(pdf)) {
                doc.add(new Paragraph("Hola mundo"));
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
