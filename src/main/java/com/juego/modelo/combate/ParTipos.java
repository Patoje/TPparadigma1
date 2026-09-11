package com.juego.modelo.combate;

import com.juego.modelo.elementos.TipoElemento;
import java.util.Objects;

/**
 * Representa una combinación inmutable de tipo atacante y tipo defensor.
 * Utilizada como clave en la matriz de efectividad, evitando estructuras condicionales (if/switch).
 */
public record ParTipos(TipoElemento atacante, TipoElemento defensor) {
    public ParTipos {
        Objects.requireNonNull(atacante, "El atacante no puede ser nulo");
        Objects.requireNonNull(defensor, "El defensor no puede ser nulo");
    }

    @Override
    public String toString() {
        return atacante.getNombre() + " -> " + defensor.getNombre();
    }
}
