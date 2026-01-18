package com.albalatro.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.MulticolContainer;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.MulticolRenderer;

public class PDFService {
    
    public static void main(String[] args) {
        PDFService.getPdf(JSONService.testing1(2), 67.0, 13.0, "Salarios.pdf");
    }
    
    public static int getNumeroDeMeses(LocalDate beginning, LocalDate end ) {
        if (end.isBefore(beginning)) return 0;

        return (int) ChronoUnit.MONTHS.between(beginning, end);
    }
    
    
    public static boolean getPdf(Empleado empleado, Double horas, Double sueldo, String path){
        try{
            // VARIABLES DEL PDF
            
            PdfWriter writer = new PdfWriter(path);
            PageSize ps = PageSize.LETTER;
            
            PdfDocument pdf = new PdfDocument(writer);
            
            // VARIABLES DE LAS FUENTES
            PdfFont docFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            
            
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
            
            // FORMACION DE LA CADENA DE LA CABECERA.
            
            String datosEmpleado = "";    
            
            // VARIABLES DE LAS COLUMNAS
            Style fuenteColumna = new Style()
            .setTextAlignment(TextAlignment.RIGHT)
            .setFontSize(15);
            
            int columnas = 1;
            int filas = 3;
            int espacios = 4;
            int observaciones = 0;
            
            if (! (empleado.getObservaciones().isEmpty() || empleado.getObservaciones() == null)) {
                columnas = 2;
                observaciones = empleado.getObservaciones().size();
            }
            
            if (observaciones > 0) {
                int filasTexto = filas + observaciones + 1;
                
                // SI EL NUMERO DE FILAS ES PAR: AMBOS TIENEN LA MITAD DE FILAS.
                // SI EL NUMERO DE FILAS ES IMPAR: EL IZQUIERDO TIENE N//2 Y EL DERECHO TIENE N//2+1
                // EJEMPLO: 13: Izquierda = 6, Derecha = 7.
                
                // SI ES PAR.
                if(filasTexto%2 == 0) {
                    
                } else {
                    
                }
            }
            
            for (int i = 0; i < espacios; i++) {
                datosEmpleado += "\n";
            }
            
            datosEmpleado +=
            "Nombre del empleado: " + empleado.getNombreCompleto() +"\n"
            + String.format("Total de horas trabajadas: %.1f h",horas) + "\n"
            + String.format("Sueldo a pagar: $%.2f", sueldo)+ "\n";
                        
            
            try (Document doc = new Document(pdf, ps)) {
                doc.setFont(docFont);
                MulticolContainer container = new MulticolContainer();
                container.setProperty(Property.COLUMN_COUNT, columnas);
                container.setNextRenderer(new MultiColRendererAllow10RetriesRenderer(container));
                
                // ==================================
                // PARRAFO DE LOS DATOS DEL EMPLEADO.
                // ==================================
                Paragraph div1 = new Paragraph(datosEmpleado)
                .addStyle(fuenteColumna);
                container.add(div1);
                
                doc.add(container);
                
                // Image amogus = new Image(ImageDataFactory.create("src/main/resources/Images/amogus.jpg"));
                // doc.add(amogus);
                
                for (int i = 0; i < 2; i++) {
                    doc.add(new Paragraph("\n"));
                }
                
                // ==================================
                // TABLA(S) DE HORAS.
                // ==================================

                float cellWidth = 70;
                float cellHeight = 70;
                
                Table calendario = new Table(new float[] {cellWidth,cellWidth,cellWidth,cellWidth,cellWidth,cellWidth,cellWidth})
                .useAllAvailableWidth();
                
                Cell cabecera = new Cell(1, 7) // 1 fila, 5 columnas
                .add(new Paragraph("REGISTRO DE NOMINA")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(18)
                .setFontColor(ColorConstants.BLACK))
                // .setBackgroundColor(colorTitulo)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(15)
                .setBorder(new SolidBorder(ColorConstants.WHITE, 0))
                .setMarginBottom(5);
                
                calendario.addCell(cabecera);


                for (int i = 0; i < 31; i++) {
                    Cell celda = new Cell()
                    .add(new Paragraph((i+1)+"."))
                    .setMinHeight(cellHeight)
                    .setMinWidth(cellWidth);

                    calendario.addCell(celda);
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