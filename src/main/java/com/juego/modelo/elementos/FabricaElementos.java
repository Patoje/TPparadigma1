package com.juego.modelo.elementos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fábrica para generar elementos aleatorios al iniciar la partida.
 */
public class FabricaElementos {
    private static final Random RANDOM = new Random();

    /**
     * Genera una lista de N elementos con tipos aleatorios y 100% de vida.
     *
     * @param cantidad Cantidad de cartas a generar (por regla general, 5).
     * @param prefijo Prefijo para identificar al poseedor (ej. "J1", "CPU").
     * @return Lista de nuevos elementos.
     */
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
