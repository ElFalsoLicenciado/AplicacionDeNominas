package com.albalatro.utils;

import com.albalatro.model.Empleado;

public class Session {
    private static Empleado empleadoSeleccionado;

    public static void setEmpleadoSeleccionado(Empleado empleado) {
        empleadoSeleccionado = empleado;
    }

    public static Empleado getEmpleadoSeleccionado() {
        return empleadoSeleccionado;
    }
}