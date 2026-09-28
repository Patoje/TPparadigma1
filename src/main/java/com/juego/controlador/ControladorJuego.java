package com.juego.controlador;

import com.juego.modelo.combate.EstadoPartida;
import com.juego.modelo.combate.Partida;
import com.juego.modelo.combate.ResultadoRonda;
import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.Elemento;
import com.juego.modelo.ia.TipoIA;
import com.juego.modelo.jugador.JugadorHumano;
import com.juego.modelo.jugador.JugadorMaquina;
import com.juego.vista.ModalFinPartida;
import com.juego.vista.Sonidos;
import com.juego.vista.VentanaPrincipal;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * Separa la pantalla del juego: la ventana no calcula el daño, se lo pide a esta clase,
 * y esta clase usa Partida, Elemento y TablaEfectividad. Así la lógica no queda toda en un solo lado.
 */
public class ControladorJuego {

    private final TablaEfectividad tablaEfectividad;
    private VentanaPrincipal vista;
    private Partida partida;
    private TipoIA ultimoTipoIA = TipoIA.ALEATORIA;
    private boolean turnoEnProceso = false;

    public ControladorJuego() {
        this.tablaEfectividad = new TablaEfectividad();
    }

    public void setVista(VentanaPrincipal vista) {
        this.vista = vista;
        this.vista.inicializar(this, this.tablaEfectividad);
    }

    /** Arranca una partida nueva contra la IA elegida en el menú. */
    public void iniciarPartida(TipoIA tipoIA) {
        this.ultimoTipoIA = tipoIA;
        this.turnoEnProceso = false;
        JugadorHumano humano = new JugadorHumano("Entrenador");
        JugadorMaquina cpu = new JugadorMaquina("Rival", tipoIA);

        this.partida = new Partida(humano, cpu, tablaEfectividad);
        this.partida.iniciar();

        vista.mostrarPantalla(VentanaPrincipal.PANTALLA_BATALLA);
        actualizarTodo();

        String mensajeBienvenida = String.format(
                "¡Comienza el combate elemental!\nTu rival es controlado por %s.\n" +
                "Presiona '⚔️ ¡ATACAR!' para la Ronda 1 o haz clic en una de tus cartas para cambiar.",
                cpu.getTipoIA().getEtiqueta()
        );
        vista.setMensajeDialogo(mensajeBienvenida);
        vista.setBotonAtacarHabilitado(true);
    }

    /** El humano elige otra carta. Si la anterior seguía viva, el rival le pega al que entra. */
    public void seleccionarElementoHumano(Elemento elemento) {
        if (partida == null || partida.getEstado() != EstadoPartida.EN_CURSO || turnoEnProceso) {
            return;
        }

        if (!elemento.estaVivo()) {
            vista.agregarMensajeDialogo("❌ Ese elemento está fuera de combate y no puede luchar.");
            return;
        }

        if (elemento.equals(partida.getHumano().getElementoActivo())) {
            return;
        }

        Elemento anterior = partida.getHumano().getElementoActivo();
        boolean eraVoluntario = (anterior != null && anterior.estaVivo());

        Sonidos.reproducirCambio();
        partida.getHumano().setElementoActivo(elemento);
        actualizarTodo();

        if (eraVoluntario) {
            // El cambio voluntario consume el turno: la IA espera 1.5s y ataca al recién llegado
            turnoEnProceso = true;
            vista.setBotonAtacarHabilitado(false);
            vista.setMensajeDialogo(String.format("🔄 Cambiaste a %s (%s).\n⏳ El rival aprovecha tu turno para preparar su ataque...",
                    elemento.getNombre(), elemento.getTipo().getNombre()));

            Timer timerRival = new Timer(1500, e -> {
                Elemento elemIA = partida.getMaquina().getElementoActivo();
                double danioRecibido = tablaEfectividad.obtenerDanio(elemIA.getTipo(), elemento.getTipo());
                elemento.recibirDanio(danioRecibido);

                vista.reproducirImpacto(true, false);
                Sonidos.reproducirGolpe();
                actualizarTodo();
                turnoEnProceso = false;

                if (!elemento.estaVivo()) {
                    if (!partida.getHumano().tieneElementosVivos()) {
                        partida.setEstado(EstadoPartida.VICTORIA_MAQUINA);
                        mostrarModalFinPartida(false, "Tu última criatura cayó al entrar.");
                    } else {
                        vista.setMensajeDialogo(String.format("🔄 Cambiaste a %s (%s). ¡%s (Rival) (%s) lo atacó al entrar causando %.0f%% de daño y lo debilitó!\n👉 Selecciona otra carta viva.",
                                elemento.getNombre(), elemento.getTipo().getNombre(),
                                elemIA.getNombre(), elemIA.getTipo().getNombre(), danioRecibido));
                        vista.setBotonAtacarHabilitado(false);
                    }
                } else {
                    vista.setMensajeDialogo(String.format("🔄 Cambiaste a %s (%s).\n⚡ ¡%s (Rival) (%s) de la IA aprovechó tu cambio y le propinó %.0f%% de daño al entrar!\n👉 ¡Presiona '⚔️ ¡ATACAR!' para la siguiente ronda!",
                            elemento.getNombre(), elemento.getTipo().getNombre(),
                            elemIA.getNombre(), elemIA.getTipo().getNombre(), danioRecibido));
                    partida.incrementarRonda();
                    vista.setBotonAtacarHabilitado(true);
                }
            });
            timerRival.setRepeats(false);
            timerRival.start();
        } else {
            // Reemplazo limpio tras debilitación previa
            vista.setMensajeDialogo(String.format("🔄 ¡Enviaste a %s (%s) al combate!\n👉 ¡Presiona '⚔️ ¡ATACAR!' para continuar!",
                    elemento.getNombre(), elemento.getTipo().getNombre()));
            vista.setBotonAtacarHabilitado(true);
        }
    }

    /** Ataque del turno: primero pega el humano y, un segundo y medio después, responde la IA. */
    public void ejecutarAtaqueRonda() {
        if (partida == null || partida.getEstado() != EstadoPartida.EN_CURSO || turnoEnProceso) {
            return;
        }

        if (partida.getHumano().necesitaReemplazo()) {
            vista.setMensajeDialogo("⚠️ Tu elemento actual ha caído. ¡Haz clic en una de tus cartas vivas abajo para continuar!");
            vista.setBotonAtacarHabilitado(false);
            return;
        }

        Elemento elemHumano = partida.getHumano().getElementoActivo();
        Elemento elemIA = partida.getMaquina().getElementoActivo();

        if (elemHumano == null || elemIA == null) return;

        // 1. Fase Jugador: ataca de inmediato con impacto y sonido
        turnoEnProceso = true;
        vista.setBotonAtacarHabilitado(false);

        double danioHumano = tablaEfectividad.obtenerDanio(elemHumano.getTipo(), elemIA.getTipo());
        elemIA.recibirDanio(danioHumano);

        vista.reproducirImpacto(false, true);
        Sonidos.reproducirGolpe();
        actualizarTodo();

        vista.setMensajeDialogo(String.format("⚔️ Ronda %d: ¡%s (%s) atacó a %s (Rival) (%s) causando %.0f%% de daño!\n⏳ El rival está preparando su acción...",
                partida.getRondaActual(),
                elemHumano.getNombre(), elemHumano.getTipo().getNombre(),
                elemIA.getNombre(), elemIA.getTipo().getNombre(),
                danioHumano));

        // 2. Fase Rival: espera 1.5 segundos antes de reaccionar
        Timer timerIA = new Timer(1500, e -> {
            boolean iaEliminada = !elemIA.estaVivo();

            if (iaEliminada) {
                vista.agregarMensajeDialogo(String.format("💀 ¡%s (Rival) ha quedado fuera de combate!", elemIA.getNombre()));

                if (partida.getMaquina().tieneElementosVivos()) {
                    // La IA espera 1 segundo adicional para enviar su relevo
                    Timer timerRelevo = new Timer(1000, ev -> {
                        Elemento nuevoIA = partida.getMaquina().decidirElemento(elemHumano, tablaEfectividad);
                        partida.getMaquina().setElementoActivo(nuevoIA);

                        Sonidos.reproducirCambio();
                        actualizarTodo();

                        vista.agregarMensajeDialogo(String.format("🔄 La IA envía a %s (Rival) (%s) al frente de batalla.\n👉 ¡Turno listo para la siguiente ronda!",
                                nuevoIA.getNombre(), nuevoIA.getTipo().getNombre()));

                        partida.incrementarRonda();
                        turnoEnProceso = false;
                        vista.setBotonAtacarHabilitado(true);
                    });
                    timerRelevo.setRepeats(false);
                    timerRelevo.start();
                } else {
                    partida.setEstado(EstadoPartida.VICTORIA_HUMANO);
                    turnoEnProceso = false;
                    mostrarModalFinPartida(true, "Dificultad superada: " + partida.getMaquina().getTipoIA().getEtiqueta());
                }
            } else {
                // 3. Contraataque de la IA
                double danioIA = tablaEfectividad.obtenerDanio(elemIA.getTipo(), elemHumano.getTipo());
                elemHumano.recibirDanio(danioIA);

                vista.reproducirImpacto(true, false);
                Sonidos.reproducirGolpe();
                actualizarTodo();

                vista.agregarMensajeDialogo(String.format("🛡️ ¡%s (Rival) (%s) respondió al ataque causando %.0f%% de daño!",
                        elemIA.getNombre(), elemIA.getTipo().getNombre(), danioIA));

                partida.incrementarRonda();
                turnoEnProceso = false;

                if (!elemHumano.estaVivo()) {
                    vista.agregarMensajeDialogo(String.format("💀 ¡Tu %s ha quedado fuera de combate!", elemHumano.getNombre()));
                    if (!partida.getHumano().tieneElementosVivos()) {
                        partida.setEstado(EstadoPartida.VICTORIA_MAQUINA);
                        mostrarModalFinPartida(false, "Dificultad enfrentada: " + partida.getMaquina().getTipoIA().getEtiqueta());
                    } else {
                        vista.agregarMensajeDialogo("👉 Haz clic en una de tus cartas vivas abajo para continuar.");
                        vista.setBotonAtacarHabilitado(false);
                    }
                } else {
                    vista.setBotonAtacarHabilitado(true);
                }
            }
        });
        timerIA.setRepeats(false);
        timerIA.start();
    }

    private void mostrarModalFinPartida(boolean esVictoria, String detalle) {
        vista.setBotonAtacarHabilitado(false);
        if (esVictoria) {
            Sonidos.reproducirMusicaVictoria();
        } else {
            Sonidos.reproducirMusicaDerrota();
        }
        ModalFinPartida modal = new ModalFinPartida(
                vista,
                esVictoria,
                detalle,
                () -> iniciarPartida(ultimoTipoIA),
                () -> vista.mostrarPantalla(VentanaPrincipal.PANTALLA_MENU)
        );
        modal.setVisible(true);
    }

    private void actualizarTodo() {
        if (partida == null) return;

        Elemento hActivo = partida.getHumano().getElementoActivo();
        Elemento iaActivo = partida.getMaquina().getElementoActivo();
        String infoIA = partida.getMaquina().getNombre() + " (" + partida.getMaquina().getTipoIA().getEtiqueta() + ")";

        vista.actualizarEscenario(hActivo, iaActivo, infoIA);
        vista.actualizarMazoCartas(partida.getHumano().getElementos(), hActivo);
    }

    public TablaEfectividad getTablaEfectividad() {
        return tablaEfectividad;
    }
}
