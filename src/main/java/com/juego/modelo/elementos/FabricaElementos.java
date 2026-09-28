package com.juego.modelo.elementos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * No es una función suelta: es una clase. generarElementosAleatorios arma las cartas al azar
 * cuando empieza la partida. Partida la usa, pero la regla de crear cartas no está dentro de Partida.
 */
public class FabricaElementos {
    private static final Random RANDOM = new Random();

    /** Crea N cartas con tipo al azar y 100% de vida. El prefijo distingue las del humano y las de la IA. */
    public static List<Elemento> generarElementosAleatorios(int cantidad, String prefijo) {
        TipoElemento[] tipos = TipoElemento.values();
        List<Elemento> lista = new ArrayList<>();

        for (int i = 1; i <= cantidad; i++) {
            TipoElemento tipo = tipos[RANDOM.nextInt(tipos.length)];
            String id = prefijo + "_" + tipo.name().toLowerCase() + "_" + i;
            String nombre = tipo.getNombreCriatura() + " #" + i;
            lista.add(new Elemento(id, nombre, tipo));
        }

        return lista;
    }
}
