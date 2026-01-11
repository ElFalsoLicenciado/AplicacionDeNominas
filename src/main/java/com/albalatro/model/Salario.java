package com.albalatro.model;

public class Salario {
    private String id;
    private String nombre;
    private TipoPago pago;
    private Double normal;
    private Double domingo;
    
    public Salario() {
        
    }
    
    public Salario(Double domingo, TipoPago pago, String id, String nombre, Double normal) {
        this.id = id;
        this.nombre = nombre;
        this.pago = pago;
        this.normal = normal;
        this.domingo = domingo;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public void setNormal(Double normal) {
        this.normal = normal;
    }

    public TipoPago getPago() {
        return pago;
    }

    public void setPago(TipoPago pago) {
        this.pago = pago;
    }
    
    public void setDomingo(Double domingo) {
        this.domingo = domingo;
    }
    
    public Double getNormal() {
        return normal;
    }
    
    public Double getDomingo() {
        return domingo;
    }
    
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
    
}

