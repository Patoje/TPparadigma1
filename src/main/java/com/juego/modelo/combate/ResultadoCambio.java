package com.juego.modelo.combate;

import com.juego.modelo.elementos.Elemento;

/**
 * Registro del resultado de una acción de cambio de criatura.
 * Indica si fue un cambio voluntario (con costo de turno y ataque del rival) o forzado (tras caer debilitado).
 */
public record ResultadoCambio(
        boolean fueVoluntario,
        Elemento elementoAnterior,
        Elemento elementoNuevo,
        double danioRecibido,
        boolean nuevoFueDebilitado,
        String mensaje
) {}
