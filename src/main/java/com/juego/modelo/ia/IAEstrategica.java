package com.juego.modelo.ia;

import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.Elemento;
import java.util.List;

/**
 * IA Estratégica:
 * Selecciona el elemento que le otorgue mayor ventaja neta frente al elemento del oponente
 * (maximizar daño infligido y minimizar el daño recibido).
 */
public class IAEstrategica implements EstrategiaIA {

    @Override
    public Elemento seleccionarElemento(List<Elemento> elementosDisponibles, Elemento elementoRival, TablaEfectividad tabla) {
        if (elementosDisponibles == null || elementosDisponibles.isEmpty()) {
            return null;
        }

        // Si el rival aún no tiene un elemento activo, elige el de mayor vida
        if (elementoRival == null) {
            return elementosDisponibles.get(0);
        }

        Elemento mejorElemento = null;
        double mejorVentaja = Double.NEGATIVE_INFINITY;

        for (Elemento candidato : elementosDisponibles) {
            double danioInfligido = tabla.obtenerDanio(candidato.getTipo(), elementoRival.getTipo());
            double danioRecibido = tabla.obtenerDanio(elementoRival.getTipo(), candidato.getTipo());
            double ventajaNeta = danioInfligido - danioRecibido;

            if (ventajaNeta > mejorVentaja) {
                mejorVentaja = ventajaNeta;
                mejorElemento = candidato;
            } else if (Math.abs(ventajaNeta - mejorVentaja) < 0.0001 && mejorElemento != null) {
                // En caso de empate en ventaja, prioriza la carta con mayor vida restante
                if (candidato.getVidaActual() > mejorElemento.getVidaActual()) {
                    mejorElemento = candidato;
                }
            }
        }

        return mejorElemento != null ? mejorElemento : elementosDisponibles.get(0);
    }

    @Override
    public String getNombre() {
        return "IA Estratégica";
    }

    @Override
    public String getDescripcion() {
        return "Maximiza el daño infligido y minimiza el daño recibido contra el rival.";
    }
}
