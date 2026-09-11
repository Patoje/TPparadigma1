package com.juego.modelo.combate;

import com.juego.modelo.elementos.Elemento;

/**
 * Registro inmutable de lo sucedido en una ronda de combate.
 */
public record ResultadoRonda(
        int numeroRonda,
        Elemento elementoHumano,
        double danioHumanoAIA,
        Elemento elementoIA,
        double danioIAAHumano,
        boolean iaFueEliminada,
        boolean humanoFueEliminado,
        String resumenTexto
) {}
