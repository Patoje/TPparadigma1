package com.juego.modelo.combate;

import com.juego.modelo.elementos.TipoElemento;
import java.util.Objects;

/**
 * Par atacante-defensor. Sirve de clave en la tabla de daño.
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
