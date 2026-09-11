package com.juego.modelo.ia;

import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.Elemento;
import java.util.ArrayList;
import java.util.List;

/**
 * Super IA (Estrategia + Eficiencia):
 * Combina la búsqueda de ventaja neta con la optimización de recursos (eficiencia).
 * Si el rival tiene poca vida (ej. 20%), busca entre los elementos capaces de liquidarlo
 * en el turno (daño >= vida rival) aquel que reciba menor daño o sea más eficiente,
 * reservando cartas clave para rondas futuras.
 */
public class SuperIA implements EstrategiaIA {

    @Override
    public Elemento seleccionarElemento(List<Elemento> elementosDisponibles, Elemento elementoRival, TablaEfectividad tabla) {
        if (elementosDisponibles == null || elementosDisponibles.isEmpty()) {
            return null;
        }

        if (elementoRival == null) {
            return elementosDisponibles.get(0);
        }

        double vidaRival = elementoRival.getVidaActual();

        // 1. Identificar candidatos letales (capaces de rematar al rival en este turno)
        List<Elemento> candidatosLetales = new ArrayList<>();
        for (Elemento e : elementosDisponibles) {
            double danio = tabla.obtenerDanio(e.getTipo(), elementoRival.getTipo());
            if (danio >= vidaRival) {
                candidatosLetales.add(e);
            }
        }

        // 2. Si hay elementos capaces de rematar al rival, elige con criterio de eficiencia:
        // Aquel que reciba menor daño de contraataque o el que tenga menor vida (para no arriesgar los más sanos)
        if (!candidatosLetales.isEmpty()) {
            Elemento mejorLetal = null;
            double menorDanioRecibido = Double.POSITIVE_INFINITY;

            for (Elemento letal : candidatosLetales) {
                double danioRecibido = tabla.obtenerDanio(elementoRival.getTipo(), letal.getTipo());
                if (danioRecibido < menorDanioRecibido) {
                    menorDanioRecibido = danioRecibido;
                    mejorLetal = letal;
                }
            }
            if (mejorLetal != null) {
                return mejorLetal;
            }
        }

        // 3. Si ninguno puede rematarlo en 1 golpe, aplica estrategia de máxima ventaja neta
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
                if (candidato.getVidaActual() > mejorElemento.getVidaActual()) {
                    mejorElemento = candidato;
                }
            }
        }

        return mejorElemento != null ? mejorElemento : elementosDisponibles.get(0);
    }

    @Override
    public String getNombre() {
        return "Super IA";
    }

    @Override
    public String getDescripcion() {
        return "Estrategia avanzada y remate eficiente según la vida del rival.";
    }
}
