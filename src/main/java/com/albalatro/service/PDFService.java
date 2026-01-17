package com.albalatro.service;

import java.io.IOException;

import com.albalatro.model.Empleado;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.MulticolContainer;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.MulticolRenderer;

public class PDFService {
    
    public static void main(String[] args) {
        PDFService.getPdf(JSONService.testing1(), "C:/Users/User/Desktop/Salarios.pdf");
    }
    
    
    
    public static boolean getPdf(Empleado empleado, String path){
        try{
            // VARIABLES DEL PDF
            
            PdfWriter writer = new PdfWriter(path);
            PageSize ps = PageSize.LETTER;
            
            PdfDocument pdf = new PdfDocument(writer);
            
            // VARIABLES DE LAS FUENTES
            PdfFont docFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            
            // VARIABLES DE LAS COLUMNAS
            // float offSet = 36;
            // float columnWidth = (ps.getWidth() - offSet * 2 + 10 ) / 3;
            // float columnHeight = ps.getHeight() - offSet * 2;
            
            // DEFINIR LAS COLUMNAS
            // Rectangle[] columns = {
            //     new Rectangle(offSet - 5, offSet, columnWidth, columnHeight),
            //     new Rectangle(offSet + columnWidth, offSet, columnWidth, columnHeight),
            //     new Rectangle(offSet + columnWidth * 2 + 5, offSet, columnWidth, columnHeight)
            // };
            
            
            try (Document doc = new Document(pdf, ps)) {
                doc.setFont(docFont);
                MulticolContainer container = new MulticolContainer();
                container.setProperty(Property.COLUMN_COUNT, 2);
                container.setNextRenderer(new MultiColRendererAllow10RetriesRenderer(container));
                
                Paragraph div = new Paragraph(
                    "Nombre del empleado:" + empleado.getNombreCompleto()
                );
                container.add(div);
                doc.add(container);

                doc.add(new Paragraph("asdjasdkasdbuasbdabsidbuasdiuasdaisdiahsoidhaoishdoashodahsdhoisahdoisahdosaiiiiiiiiña7bpbasdjasdkasdbuasbdabsidbuasdiuasdaisdiahsoidhaoishdoashodahsdhoisahdoisahdosaiiiiiiiiña7bpbasdjasdkasdbuasbdabsidbuasdiuasdaisdiahsoidhaoishdoashodahsdhoisahdoisahdosaiiiiiiiiña7bpbasdjasdkasdbuasbdabsidbuasdiuasdaisdiahsoidhaoishdoashodahsdhoisahdoisahdosaiiiiiiiiña7bpbasdjasdkasdbuasbdabsidbuasdiuasdaisdiahsoidhaoishdoashodahsdhoisahdoisahdosaiiiiiiiiña7bpbasdjasdkasdbuasbdabsidbuasdiuasdaisdiahsoidhaoishdoashodahsdhoisahdoisahdosaiiiiiiiiña7bpbasdjasdkasdbuasbdabsidbuasdiuasdaisdiahsoidhaoishdoashodahsdhoisahdoisahdosaiiiiiiiiña7bpbasdjasdkasdbuasbdabsidbuasdiuasdaisdiahsoidhaoishdoashodahsdhoisahdoisahdosaiiiiiiiiña7bpb"));
                
                // Image amogus = new Image(ImageDataFactory.create("src/main/resources/Images/amogus.jpg"));

                // doc.add(amogus);
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static class MultiColRendererAllow10RetriesRenderer extends MulticolRenderer {
        
        /**
        * Creates a DivRenderer from its corresponding layout object.
        *
        * @param modelElement the {@link MulticolContainer} which this object should manage
        */
        public MultiColRendererAllow10RetriesRenderer(MulticolContainer modelElement) {
            super(modelElement);
            setHeightCalculator(new LayoutInInfiniteHeightCalculator());
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public IRenderer getNextRenderer() {
            return new MultiColRendererAllow10RetriesRenderer((MulticolContainer) modelElement);
        }
    }
    
    public static class LayoutInInfiniteHeightCalculator extends MulticolRenderer.LayoutInInfiniteHeightCalculator {
        
        public LayoutInInfiniteHeightCalculator() {
            super();
            maxRelayoutCount = 10;
        }
    }
}
