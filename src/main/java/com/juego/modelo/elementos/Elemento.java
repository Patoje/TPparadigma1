package com.juego.modelo.elementos;

import java.util.Objects;

/**
 * Encapsulación: id, nombre, tipo y vida son private.
 * Nadie puede poner la vida en un número cualquiera. Solo baja con recibirDanio, y nunca pasa de 0.
 */
public class Elemento {
    private final String id;
    private final String nombre;
    private final TipoElemento tipo;
    private final double vidaMaxima;
    private double vidaActual;

    public Elemento(String id, String nombre, TipoElemento tipo) {
        this(id, nombre, tipo, 100.0);
    }

    public Elemento(String id, String nombre, TipoElemento tipo, double vidaMaxima) {
        if (vidaMaxima <= 0) {
            throw new IllegalArgumentException("La vida máxima debe ser mayor a 0.");
        }
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.tipo = Objects.requireNonNull(tipo, "El tipo no puede ser nulo");
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
    }

    /** Resta vida. Si el golpe es más grande que la vida que queda, queda en 0. */
    public void recibirDanio(double cantidad) {
        if (cantidad <= 0) {
            return;
        }
        this.vidaActual = Math.max(0.0, this.vidaActual - cantidad);
    }

    /** Sigue en juego si todavía tiene vida. */
    public boolean estaVivo() {
        return this.vidaActual > 0.0001;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoElemento getTipo() {
        return tipo;
    }

    public double getVidaMaxima() {
        return vidaMaxima;
    }

    public double getVidaActual() {
        return vidaActual;
    }

    public double getPorcentajeVida() {
        return (vidaActual / vidaMaxima) * 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %.0f%% PS", nombre, tipo.getNombre(), vidaActual);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Elemento elemento = (Elemento) o;
        return Objects.equals(id, elemento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
