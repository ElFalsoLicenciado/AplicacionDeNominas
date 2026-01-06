package com.albalatro.utils;

import com.albalatro.model.Empleado;

public class Session {
    private static Empleado empleadoSeleccionado;
    private static Empleado empleadoEditando;

    public static void setEmpleadoSeleccionado(Empleado empleado) {
        empleadoSeleccionado = empleado;
        empleadoEditando = empleado;
    }

    public static Empleado getEmpleadoSeleccionado() {
        return empleadoSeleccionado;
    }

    public static Empleado getEmpleadoEditando(){
        return empleadoEditando;
    }

    // 01/05/25 : Skill issue (ALBERTO), no sabe pensar.
}