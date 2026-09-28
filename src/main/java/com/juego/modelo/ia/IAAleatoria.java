package com.juego.modelo.ia;

import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.Elemento;
import java.util.List;
import java.util.Random;

/**
 * Polimorfismo: implements EstrategiaIA. Esta versión de seleccionarElemento elige al azar.
 */
public class IAAleatoria implements EstrategiaIA {
    private final Random random;

    public IAAleatoria() {
        this.random = new Random();
    }

    public IAAleatoria(Random random) {
        this.random = random;
    }

    @Override
    public Elemento seleccionarElemento(List<Elemento> elementosDisponibles, Elemento elementoRival, TablaEfectividad tabla) {
        if (elementosDisponibles == null || elementosDisponibles.isEmpty()) {
            return null;
        }
        int index = random.nextInt(elementosDisponibles.size());
        return elementosDisponibles.get(index);
    }

    @Override
    public String getNombre() {
        return "IA Aleatoria";
    }

    @Override
    public String getDescripcion() {
        return "Elige al azar entre sus cartas disponibles sin evaluar tipos.";
    }
}
