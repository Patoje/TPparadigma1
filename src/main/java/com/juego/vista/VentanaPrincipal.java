package com.juego.vista;

import com.juego.controlador.ControladorJuego;
import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.Elemento;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;

/**
 * Ventana Principal de la aplicación que administra las pantallas mediante CardLayout.
 */
public class VentanaPrincipal extends JFrame {

    public static final String PANTALLA_MENU = "MENU";
    public static final String PANTALLA_CONFIG = "CONFIG";
    public static final String PANTALLA_BATALLA = "BATALLA";

    private final CardLayout cardLayout;
    private final JPanel contenedorPrincipal;

    private MenuPrincipalPanel menuPanel;
    private ConfiguracionDanioPanel configPanel;
    private BatallaPanel batallaPanel;
    private DialogoCombatePanel dialogoPanel;
    private CartasReservaPanel cartasPanel;

    private ControladorJuego controlador;

    public VentanaPrincipal() {
        super("Batalla Elemental: Agua, Tierra y Fuego | TP Paradigmas 2026");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 750);
        setResizable(false);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contenedorPrincipal = new JPanel(cardLayout);
        add(contenedorPrincipal);
    }

    public void inicializar(ControladorJuego controlador, TablaEfectividad tabla) {
        this.controlador = controlador;

        // 1. Pantalla Menú
        this.menuPanel = new MenuPrincipalPanel(
                e -> controlador.iniciarPartida(menuPanel.getTipoIASeleccionado()),
                e -> mostrarPantalla(PANTALLA_CONFIG),
                e -> System.exit(0)
        );

        // 2. Pantalla Configuración
        this.configPanel = new ConfiguracionDanioPanel(
                tabla,
                e -> mostrarPantalla(PANTALLA_MENU)
        );

        // 3. Pantalla de Batalla (Escenario 2D + Consola de Diálogo + Banco de Cartas)
        JPanel contenedorBatalla = new JPanel(new BorderLayout());
        contenedorBatalla.setBackground(EstilosUI.FONDO_OSCURO);

        this.batallaPanel = new BatallaPanel();
        this.dialogoPanel = new DialogoCombatePanel(
                e -> controlador.ejecutarAtaqueRonda(),
                e -> mostrarPantalla(PANTALLA_MENU)
        );
        this.cartasPanel = new CartasReservaPanel(
                elemento -> controlador.seleccionarElementoHumano(elemento)
        );

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        panelInferior.add(dialogoPanel, BorderLayout.NORTH);
        panelInferior.add(cartasPanel, BorderLayout.SOUTH);

        contenedorBatalla.add(batallaPanel, BorderLayout.CENTER);
        contenedorBatalla.add(panelInferior, BorderLayout.SOUTH);

        // Agregar pantallas al CardLayout
        contenedorPrincipal.add(menuPanel, PANTALLA_MENU);
        contenedorPrincipal.add(configPanel, PANTALLA_CONFIG);
        contenedorPrincipal.add(contenedorBatalla, PANTALLA_BATALLA);

        mostrarPantalla(PANTALLA_MENU);
    }

    public void mostrarPantalla(String nombrePantalla) {
        cardLayout.show(contenedorPrincipal, nombrePantalla);
        if (PANTALLA_MENU.equals(nombrePantalla) || PANTALLA_CONFIG.equals(nombrePantalla)) {
            Sonidos.reproducirMusicaMenu();
        } else if (PANTALLA_BATALLA.equals(nombrePantalla)) {
            Sonidos.reproducirMusicaCombate();
        }
    }

    public void actualizarEscenario(Elemento humano, Elemento ia, String nombreIA) {
        batallaPanel.actualizarCombatientes(humano, ia, nombreIA);
    }

    public void actualizarMazoCartas(List<Elemento> elementos, Elemento activo) {
        cartasPanel.actualizarCartas(elementos, activo);
    }

    public void setMensajeDialogo(String mensaje) {
        dialogoPanel.mostrarMensaje(mensaje);
    }

    public void agregarMensajeDialogo(String mensaje) {
        dialogoPanel.agregarMensaje(mensaje);
    }

    public void reproducirImpacto(boolean danioHumano, boolean danioIA) {
        batallaPanel.reproducirEfectoImpacto(danioHumano, danioIA);
    }

    public void setBotonAtacarHabilitado(boolean habilitado) {
        dialogoPanel.setAtacarHabilitado(habilitado);
    }
}
