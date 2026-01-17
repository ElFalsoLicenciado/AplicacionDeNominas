package com.albalatro.service;

import java.io.IOException;

import com.albalatro.model.Empleado;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.MulticolContainer;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.MulticolRenderer;

public class PDFService {
    
    public static void main(String[] args) {
        PDFService.getPdf(JSONService.testing1(2), 67.0, 13.0, "Salarios.pdf");
    }
    
    
    
    public static boolean getPdf(Empleado empleado, Double horas, Double sueldo, String path){
        try{
            // VARIABLES DEL PDF
            
            PdfWriter writer = new PdfWriter(path);
            PageSize ps = PageSize.LETTER;
            
            PdfDocument pdf = new PdfDocument(writer);
            
            // VARIABLES DE LAS FUENTES
            PdfFont docFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            
            // VARIABLES DE LAS COLUMNAS
            int columnas = 1;
            int filas = 4;
            int espacios = 0;
            int observaciones = 0;
            
            if (! (empleado.getObservaciones().isEmpty() || empleado.getObservaciones() == null)) {
                columnas = 2;
                observaciones = empleado.getObservaciones().size();
            }
            // DEFINIR LAS COLUMNAS
            // Rectangle[] columns = {
            //     new Rectangle(offSet - 5, offSet, columnWidth, columnHeight),
            //     new Rectangle(offSet + columnWidth, offSet, columnWidth, columnHeight),
            //     new Rectangle(offSet + columnWidth * 2 + 5, offSet, columnWidth, columnHeight)
            // };
            
            // VARIABLES PARA LAS FUENTES A UTILIZAR EN LAS CELDAS.
            
            Style fuenteCelda = new Style()
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(11)
            .setFontColor(ColorConstants.GREEN);
            
            
            Style fuenteHoras = new Style()
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(11)
            .setFontColor(ColorConstants.RED);
            
            Style fuenteSueldo = new Style()
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(11)
            .setFontColor(ColorConstants.GREEN);
            
            try (Document doc = new Document(pdf, ps)) {
                doc.setFont(docFont);
                MulticolContainer container = new MulticolContainer();
                container.setProperty(Property.COLUMN_COUNT, columnas);
                container.setNextRenderer(new MultiColRendererAllow10RetriesRenderer(container));
                
                String status = "";
                
                switch (empleado.getStatus()) {
                    case ALTA -> status = "Activo";
                    case BAJA -> status = "Antiguo";
                }
                
                String datosEmpleado =    
                "Nombre del empleado: " + empleado.getNombreCompleto() +"\n"
                + String.format("Total de horas trabajadas: %.1f h",horas) + "\n"
                + String.format("Sueldo a pagar: $%.2f", sueldo)+ "\n"
                + "Status del empleado:" + status +"\n";

                // SI EL NUMERO DE FILAS ES PAR: AMBOS TIENEN LA MITAD DE FILAS.
                // SI EL NUMERO DE FILAS ES IMPAR: EL IZQUIERDO TIENE N//2 Y EL DERECHO TIENE N//2+1
                // EJEMPLO: 13: Izquierda = 6, Derecha = 7.

                for (int i = 0; i < 3; i++) {
                    datosEmpleado += (i+1)+".\n";
                }
                
                //EL contenedor divide la cantidad de lineas en 2, si hay 6, 3 en cada lado.
                Paragraph div1 = new Paragraph(datosEmpleado);
                container.add(div1);
                
                // Paragraph div2 = new Paragraph(
                //     "Nombre del empleado:" + empleado.getNombreCompleto()
                // );
                // container.add(div2);
                
                doc.add(container);
                
                
                // Image amogus = new Image(ImageDataFactory.create("src/main/resources/Images/amogus.jpg"));
                
                // doc.add(amogus);
                
                float cellWidth = 70;
                float cellHeight = 70;
                
                Table calendario = new Table(new float[] {cellWidth,cellWidth,cellWidth,cellWidth,cellWidth,cellWidth,cellWidth});
                calendario.setHeight(cellHeight);
                for (int i = 0; i < 7; i++) {
                    calendario.addCell((i+1)+".");
                }
                
                doc.add(calendario);
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