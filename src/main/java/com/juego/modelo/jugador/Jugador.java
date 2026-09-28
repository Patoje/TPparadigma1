package com.juego.modelo.jugador;

import com.juego.modelo.elementos.Elemento;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Herencia: esta es la clase padre. No se puede crear un Jugador suelto (es abstracta).
 * JugadorHumano y JugadorMaquina heredan de acá el nombre, las cartas y la carta activa.
 * Encapsulación: nombre, cartas y carta activa son private. Desde afuera solo se usan los métodos.
 */
public abstract class Jugador {
    private final String nombre;
    private final List<Elemento> elementos;
    private Elemento elementoActivo;

    public Jugador(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre del jugador no puede ser nulo");
        this.elementos = new ArrayList<>();
    }

    public void asignarElementos(List<Elemento> nuevosElementos) {
        this.elementos.clear();
        if (nuevosElementos != null) {
            this.elementos.addAll(nuevosElementos);
            if (!this.elementos.isEmpty()) {
                this.elementoActivo = this.elementos.get(0);
            }
        }
    }

    public boolean tieneElementosVivos() {
        for (Elemento e : elementos) {
            if (e.estaVivo()) {
                return true;
            }
        }
        return false;
    }

    public List<Elemento> getElementosVivos() {
        List<Elemento> vivos = new ArrayList<>();
        for (Elemento e : elementos) {
            if (e.estaVivo()) {
                vivos.add(e);
            }
        }
        return vivos;
    }

    public int contarElementosVivos() {
        return getElementosVivos().size();
    }

    public boolean necesitaReemplazo() {
        return elementoActivo == null || !elementoActivo.estaVivo();
    }

    public void setElementoActivo(Elemento nuevoActivo) {
        if (nuevoActivo != null && !nuevoActivo.estaVivo()) {
            throw new IllegalArgumentException("No se puede enviar a la batalla un elemento fuera de combate.");
        }
        this.elementoActivo = nuevoActivo;
    }

    public Elemento getElementoActivo() {
        return elementoActivo;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Elemento> getElementos() {
        return Collections.unmodifiableList(elementos);
    }
}
