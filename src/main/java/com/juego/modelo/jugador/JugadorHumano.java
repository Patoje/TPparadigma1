package com.juego.modelo.jugador;

/**
 * Herencia: JugadorHumano ES un Jugador.
 * extends toma todo lo de Jugador. super(nombre) llama al constructor del padre
 * para guardar el nombre. El humano no elige carta solo: la elige el usuario en la pantalla.
 */
public class JugadorHumano extends Jugador {

    public JugadorHumano(String nombre) {
        super(nombre);
    }
}
