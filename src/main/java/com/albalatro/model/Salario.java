package com.albalatro.model;

public class Salario {
    private String id;
    private String nombre;
    private Double normal;
    private Double domingo;
    
    public Salario() {
        
    }
    
    public Salario(Double domingo, String id, String nombre, Double normal) {
        this.domingo = domingo;
        this.id = id;
        this.nombre = nombre;
        this.normal = normal;
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
    
}
