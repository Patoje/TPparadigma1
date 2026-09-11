package com.juego.modelo.elementos;

/**
 * Representa los tipos elementales del juego: Agua, Tierra y Fuego.
 * Contiene metadatos de presentación (criatura asociada, ícono, ruta de sprites).
 */
public enum TipoElemento {
    AGUA("Agua", "Squirtle", "💧", "/sprites/agua_frente.png", "/sprites/agua_espalda.png"),
    TIERRA("Tierra", "Bulbasaur", "🌱", "/sprites/tierra_frente.png", "/sprites/tierra_espalda.png"),
    FUEGO("Fuego", "Charmander", "🔥", "/sprites/fuego_frente.png", "/sprites/fuego_espalda.png");

    private final String nombre;
    private final String nombreCriatura;
    private final String icono;
    private final String spriteFrente;
    private final String spriteEspalda;

    TipoElemento(String nombre, String nombreCriatura, String icono, String spriteFrente, String spriteEspalda) {
        this.nombre = nombre;
        this.nombreCriatura = nombreCriatura;
        this.icono = icono;
        this.spriteFrente = spriteFrente;
        this.spriteEspalda = spriteEspalda;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreCriatura() {
        return nombreCriatura;
    }

    public String getIcono() {
        return icono;
    }

    public String getSpriteFrente() {
        return spriteFrente;
    }

    public String getSpriteEspalda() {
        return spriteEspalda;
    }

    @Override
    public String toString() {
        return icono + " " + nombre + " (" + nombreCriatura + ")";
    }
}
