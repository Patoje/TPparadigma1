package com.juego.modelo.ia;

import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.Elemento;
import java.util.List;

/**
 * Abstracción: la interfaz dice QUÉ tiene que poder hacer una IA (elegir carta),
 * pero no dice CÓMO. IAAleatoria, IAEstrategica y SuperIA la implementan cada una a su manera.
 * Eso es polimorfismo: el jugador máquina llama seleccionarElemento y corre la versión que tenga guardada.
 */
public interface EstrategiaIA {
    /** Elige una carta viva para enfrentar a la del humano. */
    Elemento seleccionarElemento(List<Elemento> elementosDisponibles, Elemento elementoRival, TablaEfectividad tabla);

    String getNombre();

    String getDescripcion();
}
