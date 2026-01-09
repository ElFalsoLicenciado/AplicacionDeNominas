package com.albalatro.utils;

import com.albalatro.model.Empleado;

public class Session {
    private static Empleado empleadoSeleccionado;
    private static boolean changes = false;

    public static void setEmpleadoSeleccionado(Empleado empleado) {
        empleadoSeleccionado = empleado;
    }

    public static Empleado getEmpleadoSeleccionado() {
        return empleadoSeleccionado;
    }

    public static void setChanges(boolean bool) {
        changes = bool;
    }

    public static boolean getChanges() {
        return changes;
    }
    // 01/05/25 : Skill issue (ALBERTO), no sabe pensar.
}