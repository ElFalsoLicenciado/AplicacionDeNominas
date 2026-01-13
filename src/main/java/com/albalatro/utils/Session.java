package com.albalatro.utils;

import com.albalatro.model.Empleado;
import com.albalatro.model.Salario;

public class Session {
    private static Empleado empleadoSeleccionado;
    private static Salario salarioSeleccionado;
    private static boolean changes = false;
    private static String salarioString;

    public static void setEmpleadoSeleccionado(Empleado empleado) {
        empleadoSeleccionado = empleado;
    }

    public static Empleado getEmpleadoSeleccionado() {
        return empleadoSeleccionado;
    }
    
    public static void setSalarioSeleccionado(Salario salarioSeleccionado) {
        Session.salarioSeleccionado = salarioSeleccionado;
    }

    public static Salario getSalarioSeleccionado() {
        return salarioSeleccionado;
    }

    public static void setChanges(boolean bool) {
        changes = bool;
    }

    public static boolean getChanges() {
        return changes;
    }

    public static void setEditandoSalarioIndividual() {
        salarioString = "EMPLEADO";
    }

    public static void setEditandoSalarioCustom() {
        salarioString = "CUSTOM";
    }

    public static void setEditandoNinguno() {
        salarioString = "";
    } 

    public static String getSalarioString() {
        return salarioString;
    }
    // 01/05/25 : Skill issue (ALBERTO), no sabe pensar.
}