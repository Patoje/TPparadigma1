package com.juego.modelo.ia;

import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.Elemento;
import java.util.List;

/**
 * Patrón Strategy para los distintos comportamientos de Inteligencia Artificial.
 */
public interface EstrategiaIA {
    /**
     * Selecciona la carta/elemento más conveniente entre las disponibles.
     *
     * @param elementosDisponibles Lista de cartas vivas del jugador máquina.
     * @param elementoRival Carta activa del rival (jugador humano).
     * @param tabla Matriz de efectividad configurada.
     * @return El elemento seleccionado para combatir.
     */
    Elemento seleccionarElemento(List<Elemento> elementosDisponibles, Elemento elementoRival, TablaEfectividad tabla);

    String getNombre();

    String getDescripcion();
}
