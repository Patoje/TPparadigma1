package com.juego.modelo.jugador;

import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.Elemento;
import com.juego.modelo.ia.EstrategiaIA;
import com.juego.modelo.ia.TipoIA;
import java.util.Objects;

/**
 * Representa al jugador controlado por la Inteligencia Artificial.
 * Aplica el patrón Strategy para decidir sus acciones.
 */
public class JugadorMaquina extends Jugador {
    private final TipoIA tipoIA;
    private final EstrategiaIA estrategia;

    public JugadorMaquina(String nombre, TipoIA tipoIA) {
        super(nombre);
        this.tipoIA = Objects.requireNonNull(tipoIA, "El tipo de IA no puede ser nulo");
        this.estrategia = tipoIA.crearEstrategia();
    }

    public JugadorMaquina(String nombre, EstrategiaIA estrategiaCustom) {
        super(nombre);
        this.tipoIA = TipoIA.ESTRATEGICA;
        this.estrategia = Objects.requireNonNull(estrategiaCustom, "La estrategia no puede ser nula");
    }

    /**
     * Selecciona el elemento óptimo para combatir contra el rival según su estrategia.
     */
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
