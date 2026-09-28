package com.juego.modelo.ia;

import java.util.Random;

/**
 * Cada valor del enum redefine crearEstrategia (herencia + polimorfismo).
 * ALEATORIA crea una IAAleatoria, ESTRATEGICA una IAEstrategica y SUPER_IA una SuperIA.
 * Así no hace falta un switch para decidir qué IA construir.
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
