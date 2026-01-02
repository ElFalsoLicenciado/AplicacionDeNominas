package com.albalatro.model;

public class Salario {
    private Double normal;
    private Double domingo;

    public Salario() {

    }

    public Salario (Double normal, Double domingo) {
        this.normal = normal;
        this.domingo = domingo;
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
}
