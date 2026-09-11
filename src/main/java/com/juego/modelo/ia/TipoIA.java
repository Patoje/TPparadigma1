package com.juego.modelo.ia;

import java.util.Random;

/**
 * Enumeración de los tipos de Inteligencia Artificial disponibles.
 */
public enum TipoIA {
    ALEATORIA("IA Aleatoria") {
        @Override
        public EstrategiaIA crearEstrategia() {
            return new IAAleatoria();
        }
    },
    ESTRATEGICA("IA Estratégica") {
        @Override
        public EstrategiaIA crearEstrategia() {
            return new IAEstrategica();
        }
    },
    SUPER_IA("Super IA") {
        @Override
        public EstrategiaIA crearEstrategia() {
            return new SuperIA();
        }
    };

    private final String etiqueta;
    private static final Random RANDOM = new Random();

    TipoIA(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public abstract EstrategiaIA crearEstrategia();

    public static TipoIA obtenerAleatoria() {
        TipoIA[] valores = values();
        return valores[RANDOM.nextInt(valores.length)];
    }
}
