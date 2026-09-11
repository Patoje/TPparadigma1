package com.juego.modelo.combate;

import com.juego.modelo.elementos.TipoElemento;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Matriz configurable de efectividad y daño entre tipos.
 * Cumple con la restricción de la cátedra:
 * - NO utiliza condicionales if/switch para resolver interacciones.
 * - Permite modificar las reglas de daño dinámicamente sin modificar la lógica principal.
 */
public class TablaEfectividad {
    private final Map<ParTipos, Double> matrizDanio;

    public TablaEfectividad() {
        this.matrizDanio = new HashMap<>();
        restablecerValoresPorDefecto();
    }

    /**
     * Carga los valores de daño especificados en la consigna del TP.
     * Atacante -> Defensor:
     * - Agua -> Fuego: 50%
     * - Tierra -> Fuego: 20%
     * - Fuego -> Tierra: 40%
     * - Agua -> Tierra: 30%
     * - Fuego -> Agua: 20% (pág. 2)
     * - Tierra -> Agua: 50% (pág. 3)
     * - Mismo tipo: 15% (daño neutro entre elementos del mismo tipo)
     */
    public final void restablecerValoresPorDefecto() {
        matrizDanio.clear();

        // Valores según el documento de la cátedra
        configurarDanio(TipoElemento.AGUA, TipoElemento.FUEGO, 50.0);
        configurarDanio(TipoElemento.TIERRA, TipoElemento.FUEGO, 20.0);
        configurarDanio(TipoElemento.FUEGO, TipoElemento.TIERRA, 40.0);
        configurarDanio(TipoElemento.AGUA, TipoElemento.TIERRA, 30.0);
        configurarDanio(TipoElemento.FUEGO, TipoElemento.AGUA, 20.0);
        configurarDanio(TipoElemento.TIERRA, TipoElemento.AGUA, 50.0);

        // Mismos elementos enfrentados
        configurarDanio(TipoElemento.AGUA, TipoElemento.AGUA, 15.0);
        configurarDanio(TipoElemento.TIERRA, TipoElemento.TIERRA, 15.0);
        configurarDanio(TipoElemento.FUEGO, TipoElemento.FUEGO, 15.0);
    }

    /**
     * Permite modificar el porcentaje de daño de un cruce entre tipos.
     */
    public void configurarDanio(TipoElemento atacante, TipoElemento defensor, double porcentaje) {
        if (porcentaje < 0) {
            throw new IllegalArgumentException("El porcentaje de daño no puede ser negativo.");
        }
        matrizDanio.put(new ParTipos(atacante, defensor), porcentaje);
    }

    /**
     * Obtiene el daño que inflige el tipo atacante sobre el tipo defensor.
     * Resuelto por consulta directa de tabla hash (O(1)), sin 'if' ni 'switch'.
     */
    public double obtenerDanio(TipoElemento atacante, TipoElemento defensor) {
        return matrizDanio.getOrDefault(new ParTipos(atacante, defensor), 20.0);
    }

    /**
     * Devuelve una vista inmodificable de la matriz para su visualización o edición en UI.
     */
    public Map<ParTipos, Double> getMatrizDanio() {
        return Collections.unmodifiableMap(matrizDanio);
    }
}
