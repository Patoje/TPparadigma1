package com.juego.modelo.combate;

import com.juego.modelo.elementos.Elemento;
import com.juego.modelo.elementos.FabricaElementos;
import com.juego.modelo.jugador.JugadorHumano;
import com.juego.modelo.jugador.JugadorMaquina;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Orquestador principal de la partida entre el Jugador Humano y la IA.
 * Aplica las reglas del TP: 5 elementos aleatorios al 100%, turnos y condición de victoria.
 */
public class Partida {
    public static final int ELEMENTOS_POR_JUGADOR = 5;

    private final JugadorHumano humano;
    private final JugadorMaquina maquina;
    private final TablaEfectividad tablaEfectividad;
    private int rondaActual;
    private EstadoPartida estado;
    private final List<ResultadoRonda> historial;

    public Partida(JugadorHumano humano, JugadorMaquina maquina, TablaEfectividad tablaEfectividad) {
        this.humano = Objects.requireNonNull(humano, "El jugador humano no puede ser nulo");
        this.maquina = Objects.requireNonNull(maquina, "La máquina no puede ser nula");
        this.tablaEfectividad = Objects.requireNonNull(tablaEfectividad, "La tabla de efectividad no puede ser nula");
        this.historial = new ArrayList<>();
        this.estado = EstadoPartida.NO_INICIADA;
        this.rondaActual = 0;
    }

    /**
     * Inicia una nueva partida generando 5 elementos aleatorios para cada jugador.
     */
    public void iniciar() {
        humano.asignarElementos(FabricaElementos.generarElementosAleatorios(ELEMENTOS_POR_JUGADOR, "HUM"));
        maquina.asignarElementos(FabricaElementos.generarElementosAleatorios(ELEMENTOS_POR_JUGADOR, "CPU"));

        // La IA selecciona su primer combatiente evaluando el primer elemento del humano
        Elemento iaInicial = maquina.decidirElemento(humano.getElementoActivo(), tablaEfectividad);
        if (iaInicial != null) {
            maquina.setElementoActivo(iaInicial);
        }

        this.rondaActual = 1;
        this.historial.clear();
        this.estado = EstadoPartida.EN_CURSO;
    }

    /**
     * Permite al jugador humano seleccionar o cambiar su elemento activo.
     * Si el cambio es voluntario (el anterior sigue vivo), consume el turno y el rival
     * ataca de inmediato a la criatura que entra.
     */
    public ResultadoCambio cambiarElementoHumano(Elemento nuevoElemento) {
        if (!humano.getElementos().contains(nuevoElemento)) {
            throw new IllegalArgumentException("El elemento no pertenece al jugador humano.");
        }
        if (!nuevoElemento.estaVivo()) {
            throw new IllegalArgumentException("No se puede seleccionar un elemento debilitado.");
        }

        Elemento anterior = humano.getElementoActivo();
        boolean fueVoluntario = (anterior != null && anterior.estaVivo() && !anterior.equals(nuevoElemento));

        humano.setElementoActivo(nuevoElemento);

        double danioRecibido = 0.0;
        boolean nuevoDebilitado = false;
        String mensaje;

        if (fueVoluntario) {
            // El rival aprovecha el turno del cambio y ataca al nuevo ingresante
            Elemento elemIA = maquina.getElementoActivo();
            if (elemIA != null && elemIA.estaVivo()) {
                danioRecibido = tablaEfectividad.obtenerDanio(elemIA.getTipo(), nuevoElemento.getTipo());
                nuevoElemento.recibirDanio(danioRecibido);
                nuevoDebilitado = !nuevoElemento.estaVivo();

                if (nuevoDebilitado) {
                    if (!humano.tieneElementosVivos()) {
                        estado = EstadoPartida.VICTORIA_MAQUINA;
                        mensaje = String.format("🔄 Cambiaste a %s (%s). ¡%s (Rival) (%s) atacó al entrar causando %.0f%% de daño y lo debilitó!\n💀 ¡Te has quedado sin cartas vivas! Fin de la partida.",
                                nuevoElemento.getNombre(), nuevoElemento.getTipo().getNombre(),
                                elemIA.getNombre(), elemIA.getTipo().getNombre(), danioRecibido);
                    } else {
                        mensaje = String.format("🔄 Cambiaste a %s (%s). ¡%s (Rival) (%s) aprovechó tu turno y le causó %.0f%% de daño al entrar, dejándolo fuera de combate!\n👉 Selecciona otra carta viva.",
                                nuevoElemento.getNombre(), nuevoElemento.getTipo().getNombre(),
                                elemIA.getNombre(), elemIA.getTipo().getNombre(), danioRecibido);
                    }
                } else {
                    mensaje = String.format("🔄 Cambiaste a %s (%s).\n⚡ ¡%s (Rival) (%s) aprovechó tu cambio y le propinó %.0f%% de daño al entrar!",
                            nuevoElemento.getNombre(), nuevoElemento.getTipo().getNombre(),
                            elemIA.getNombre(), elemIA.getTipo().getNombre(), danioRecibido);
                }
            } else {
                mensaje = String.format("🔄 Cambiaste a %s (%s).", nuevoElemento.getNombre(), nuevoElemento.getTipo().getNombre());
            }
            rondaActual++;
        } else {
            mensaje = String.format("🔄 ¡Enviaste a %s (%s) al combate!", nuevoElemento.getNombre(), nuevoElemento.getTipo().getNombre());
        }

        return new ResultadoCambio(fueVoluntario, anterior, nuevoElemento, danioRecibido, nuevoDebilitado, mensaje);
    }

    public void seleccionarElementoHumano(Elemento nuevoElemento) {
        cambiarElementoHumano(nuevoElemento);
    }

    /**
     * Ejecuta una ronda de combate según la mecánica del documento:
     * 1. El elemento del humano ataca al elemento de la IA.
     * 2. Si el de la IA cae (0%), la IA saca un reemplazo inmediato de su reserva y contraataca.
     * 3. Si no cae, el de la IA contraataca al del humano.
     * 4. Se comprueba si el humano cayó y si hay fin de partida.
     */
    public ResultadoRonda ejecutarRonda() {
        if (estado != EstadoPartida.EN_CURSO) {
            throw new IllegalStateException("La partida no está en curso.");
        }

        Elemento elemHumano = humano.getElementoActivo();
        if (elemHumano == null || !elemHumano.estaVivo()) {
            throw new IllegalStateException("Debes seleccionar un elemento activo con vida para combatir.");
        }

        Elemento elemIA = maquina.getElementoActivo();
        if (elemIA == null || !elemIA.estaVivo()) {
            // Si la IA no tenía uno activo vivo, lo selecciona ahora
            elemIA = maquina.decidirElemento(elemHumano, tablaEfectividad);
            maquina.setElementoActivo(elemIA);
        }

        // 1. Ataque del Humano
        double danioHumano = tablaEfectividad.obtenerDanio(elemHumano.getTipo(), elemIA.getTipo());
        elemIA.recibirDanio(danioHumano);
        boolean iaEliminada = !elemIA.estaVivo();

        StringBuilder log = new StringBuilder();
        log.append(String.format("⚔️ Ronda %d: ¡%s (%s) atacó a %s (Rival) (%s) causando %.0f%% de daño!\n",
                rondaActual, elemHumano.getNombre(), elemHumano.getTipo().getNombre(),
                elemIA.getNombre(), elemIA.getTipo().getNombre(), danioHumano));

        double danioIA = 0.0;
        boolean humanoEliminado = false;
        Elemento iaQueContraataca = elemIA;

        if (iaEliminada) {
            log.append(String.format("💀 ¡%s (Rival) ha quedado fuera de combate!\n", elemIA.getNombre()));

            if (maquina.tieneElementosVivos()) {
                // El reemplazo de la IA entra al campo, listo para el siguiente turno (sin atacar de inmediato)
                iaQueContraataca = maquina.decidirElemento(elemHumano, tablaEfectividad);
                maquina.setElementoActivo(iaQueContraataca);
                log.append(String.format("🔄 La IA envía a %s (Rival) (%s) al frente de batalla.\n",
                        iaQueContraataca.getNombre(), iaQueContraataca.getTipo().getNombre()));
                log.append("👉 ¡Ambos combatientes están listos para la siguiente ronda!");
            } else {
                // IA no tiene más cartas
                estado = EstadoPartida.VICTORIA_HUMANO;
                log.append("🏆 ¡La IA se ha quedado sin elementos! ¡Has ganado la partida!");
            }
        } else {
            // 2. Contraataque normal de la IA si sobrevivió al ataque del humano
            danioIA = tablaEfectividad.obtenerDanio(elemIA.getTipo(), elemHumano.getTipo());
            elemHumano.recibirDanio(danioIA);
            humanoEliminado = !elemHumano.estaVivo();

            log.append(String.format("🛡️ ¡%s (Rival) (%s) respondió al ataque causando %.0f%% de daño!\n",
                    elemIA.getNombre(), elemIA.getTipo().getNombre(), danioIA));
        }

        if (humanoEliminado) {
            log.append(String.format("💀 ¡Tu %s ha quedado fuera de combate!\n", elemHumano.getNombre()));
            if (!humano.tieneElementosVivos()) {
                estado = EstadoPartida.VICTORIA_MAQUINA;
                log.append("❌ ¡Te has quedado sin elementos vivos! La máquina gana la partida.");
            } else {
                log.append("👉 Elige tu siguiente elemento de la reserva para continuar.");
            }
        }

        ResultadoRonda resultado = new ResultadoRonda(
                rondaActual,
                elemHumano,
                danioHumano,
                iaQueContraataca,
                danioIA,
                iaEliminada,
                humanoEliminado,
                log.toString()
        );

        historial.add(resultado);
        rondaActual++;

        return resultado;
    }

    public JugadorHumano getHumano() {
        return humano;
    }

    public JugadorMaquina getMaquina() {
        return maquina;
    }

    public TablaEfectividad getTablaEfectividad() {
        return tablaEfectividad;
    }

    public int getRondaActual() {
        return rondaActual;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public void setEstado(EstadoPartida nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void incrementarRonda() {
        this.rondaActual++;
    }

    public List<ResultadoRonda> getHistorial() {
        return Collections.unmodifiableList(historial);
    }
}
