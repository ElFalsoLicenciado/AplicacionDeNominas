package com.albalatro.service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;

import com.albalatro.model.DailyLog;
import com.albalatro.model.Empleado;
import com.albalatro.model.Periodo;
import com.albalatro.utils.Utils;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
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
    
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", new Locale("es", "ES"));
    
    public static void main(String[] args) {
        PDFService.getPdf(JSONService.testing1(2), "Salarios.pdf");
    }
    
    public static int getNumeroDeMeses (LocalDate beginning, LocalDate end ) {
        if (end.isBefore(beginning)) return 0;
        
        return (int) ChronoUnit.MONTHS.between(beginning, end);
    }
    
    public static int getNumeroDeSemanas (LocalDate beginning, LocalDate end ) {
        if (end.isBefore(beginning)) return 0;
        
        WeekFields weekFields = WeekFields.of(Locale.US); // O tu locale
        
        // Encontrar el lunes de la semana de inicio
        LocalDate lunesInicio = beginning.with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));
        
        // Encontrar el domingo de la semana de fin
        LocalDate domingoFin = end.with(TemporalAdjusters.nextOrSame(weekFields.getFirstDayOfWeek().plus(6)));
        
        // Calcular diferencia en días y convertir a semanas
        long diasDiferencia = java.time.temporal.ChronoUnit.DAYS.between(lunesInicio, domingoFin);
        return (int) (diasDiferencia / 7) + 1;
    }
    
    public static boolean getInBetween (LocalDate beginning, LocalDate end, LocalDate date) {
        if (date.isAfter(beginning) && date.isBefore(end)) 
            IO.println(String.format("%s in between", date));
        else IO.println(String.format("%s not between", date));
        
        if (date.isEqual(beginning)) return true;
        if (date.isEqual(beginning)) return true;
        
        return date.isAfter(beginning) && date.isBefore(end);
    }
    
    public static LocalDate getDomingoDeLaSemana(LocalDate fecha) {
        return fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }
    
    
    public static boolean getPdf(Empleado empleado, String path){
        try{
            // VARIABLES DEL PDF
            
            PdfWriter writer = new PdfWriter(path);
            PageSize ps = PageSize.LETTER;
            
            PdfDocument pdf = new PdfDocument(writer);
            
            // VARIABLES DE LAS FUENTES
            PdfFont docFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            
            // VARIABLES PARA LAS FUENTES A UTILIZAR EN LAS CELDAS.
            float cellFontSize = 9;
            
            Style fuenteFecha = new Style()
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(cellFontSize)
            .setFontColor(ColorConstants.BLACK);
            
            Style fuentePeriodos = new Style()
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(cellFontSize)
            .setFontColor(ColorConstants.GRAY);
            
            Style fuenteHoras = new Style()
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(cellFontSize)
            .setFontColor(ColorConstants.RED);
            
            Style fuenteSueldo = new Style()
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(cellFontSize)
            .setFontColor(ColorConstants.GREEN);
            
            // FORMACION DE LA CADENA DE LA CABECERA.
            
            
            
            
            // VARIABLES DE LAS COLUMNAS
            Style fuenteColumna = new Style()
            .setTextAlignment(TextAlignment.RIGHT)
            .setFontSize(15);
            
            
            try (Document doc = new Document(pdf, ps)) {
                double horas = 0.0, sueldo = 0.0;
                
                doc.setFont(docFont);
                
                MulticolContainer container = new MulticolContainer();
                container.setProperty(Property.COLUMN_COUNT, 1);
                container.setNextRenderer(new MultiColRendererAllow10RetriesRenderer(container));
                
                
                
                // ==================================
                // TABLA(S) DE HORAS.
                // ==================================
                
                float cellWidth = 70;
                float cellHeight = 90;
                
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
                
                LocalDate inicioCorte = empleado.getInicioCorte();
                LocalDate finCorte = empleado.getFinCorte();
                LocalDate primerDia = getDomingoDeLaSemana(inicioCorte);
                
                
                int semanas = getNumeroDeSemanas(inicioCorte, finCorte );
                int celdas = semanas * 7;
                
                LocalDate indice = primerDia;
                
                
                for (int i = 0; i < celdas; i++) {
                    
                    Cell celda = new Cell()
                    .setMinHeight(cellHeight)
                    .setMinWidth(cellWidth);
                    
                    String aux = indice.format(formatter);
                    
                    if (indice.equals(inicioCorte) || indice.equals(finCorte)) {
                        celda.setBorder(new SolidBorder(new DeviceRgb(210, 180, 140), 3));
                        if (indice.equals(inicioCorte)) aux += "\nINICIO CORTE";
                        else aux += "\nFIN CORTE";
                    }
                    Paragraph fechaParrafo = new Paragraph(
                        // String.format("%s-%s-%s", 
                        // indice.getDayOfMonth(), 
                        // indice.getMonthValue(), 
                        // indice.getYear()
                        aux
                    )
                    .addStyle(fuenteFecha);
                    
                    celda.add(fechaParrafo);
                    
                    if (empleado.getLog().getLogs().get(indice) != null && getInBetween(inicioCorte, finCorte, indice)) {
                        DailyLog logDia = empleado.getLog().getLogs().get(indice);                        
                        if ( logDia.getTotalMinutosTrabajados() > 0) {
                            ArrayList<String> array = new ArrayList<>(); 
                            for(Periodo p : logDia.getPeriodos()) {
                                array.add(p.toString());
                            }
                            
                            Paragraph periodosParrafo = new Paragraph(Utils.stringArrayToStringSpace(array))
                            .addStyle(fuentePeriodos);
                            
                            celda.add(periodosParrafo);
                            
                            horas += logDia.getTotalMinutosTrabajados()/60.0;
                            
                            Paragraph horasParrafo = new Paragraph(
                                String.format("%,.1f h", logDia.getTotalMinutosTrabajados()/60.0)
                            )
                            .addStyle(fuenteHoras);
                            
                            celda.add(horasParrafo);
                            
                            sueldo += logDia.getTotalPagoDia();
                            
                            Paragraph sueldoParrafo = new Paragraph(
                                String.format("$%.2f", logDia.getTotalPagoDia())
                            )
                            .addStyle(fuenteSueldo);
                            
                            celda.add(sueldoParrafo);   
                        }
                        
                        if (logDia.getNotas() != null) {
                            Paragraph notasParrafo = new Paragraph(
                                logDia.getNotas()
                            )
                            .addStyle(fuenteFecha);
                            
                            celda.add(notasParrafo);
                        }
                    }
                    calendario.addCell(celda);
                    
                    indice = indice.plusDays(1);
                }
                
                String datosEmpleado = "";
                
                datosEmpleado +=
                "Nombre del empleado: " + empleado.getNombreCompleto() +"\n"
                + String.format("Total de horas trabajadas: %.1f h",horas) + "\n"
                + String.format("Sueldo a pagar: $%,.2f", sueldo)+ "\n";
                
                // int espacios = 0;
                
                // for (int i = 0; i < espacios; i++) {
                //     datosEmpleado += "\n";
                // }
                
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