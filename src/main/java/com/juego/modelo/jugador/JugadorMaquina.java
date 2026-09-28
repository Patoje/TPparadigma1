package com.juego.modelo.jugador;

import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.Elemento;
import com.juego.modelo.ia.EstrategiaIA;
import com.juego.modelo.ia.TipoIA;
import java.util.Objects;

/**
 * Herencia: JugadorMaquina también ES un Jugador, así que tiene las mismas 5 cartas.
 * Composición: además TIENE una EstrategiaIA. No hereda de la IA.
 * Polimorfismo: estrategia puede ser aleatoria, estratégica o super, y decidirElemento
 * llama al mismo método sin preguntar cuál es.
 */
public class JugadorMaquina extends Jugador {
    private final TipoIA tipoIA;
    private final EstrategiaIA estrategia;

    public JugadorMaquina(String nombre, TipoIA tipoIA) {
        super(nombre);
        this.tipoIA = Objects.requireNonNull(tipoIA, "El tipo de IA no puede ser nulo");
        // Polimorfismo: crearEstrategia devuelve la IA que corresponda a este tipo.
        this.estrategia = tipoIA.crearEstrategia();
    }

    public JugadorMaquina(String nombre, EstrategiaIA estrategiaCustom) {
        super(nombre);
        this.tipoIA = TipoIA.ESTRATEGICA;
        this.estrategia = Objects.requireNonNull(estrategiaCustom, "La estrategia no puede ser nula");
    }

    /** Pide a la IA qué carta usar contra la carta activa del humano. */
    public Elemento decidirElemento(Elemento rival, TablaEfectividad tabla) {
        return estrategia.seleccionarElemento(getElementosVivos(), rival, tabla);
    }

    public TipoIA getTipoIA() {
        return tipoIA;
    }

    public EstrategiaIA getEstrategia() {
        return estrategia;
    }
}
