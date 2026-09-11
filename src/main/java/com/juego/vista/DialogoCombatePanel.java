package com.juego.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

/**
 * Cuadro de diálogo y bitácora de combate estilo Game Boy / RPG retro.
 */
public class DialogoCombatePanel extends JPanel {

    private final JTextArea areaTexto;
    private final JButton btnAtacar;
    private final JButton btnMenu;

    public DialogoCombatePanel(ActionListener onAtacar, ActionListener onMenu) {
        setLayout(new BorderLayout(10, 5));
        setPreferredSize(new Dimension(800, 115));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 15, 6, 15));

        // Cuadro de texto estilo consola retro
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setFont(EstilosUI.FUENTE_RETRO_LOG);
        areaTexto.setForeground(new Color(245, 245, 255));
        areaTexto.setBackground(new Color(15, 20, 30));
        areaTexto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 80, 110), 2, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        // Panel lateral derecho con botones de acción
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        panelAcciones.setOpaque(false);
        panelAcciones.setPreferredSize(new Dimension(170, 100));

        btnAtacar = EstilosUI.crearBoton("⚔️ ¡ATACAR!", new Color(231, 76, 60), Color.WHITE);
        btnAtacar.setFont(EstilosUI.FUENTE_SUBTITULO);
        btnAtacar.setPreferredSize(new Dimension(160, 44));
        btnAtacar.addActionListener(onAtacar);

        btnMenu = EstilosUI.crearBoton("⬅ Menú", new Color(108, 122, 137), Color.WHITE);
        btnMenu.setPreferredSize(new Dimension(160, 36));
        btnMenu.addActionListener(onMenu);

        panelAcciones.add(btnAtacar);
        panelAcciones.add(btnMenu);

        add(scroll, BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.EAST);
    }

    public void mostrarMensaje(String mensaje) {
        areaTexto.setText(mensaje);
        areaTexto.setCaretPosition(0);
    }

    public void agregarMensaje(String mensaje) {
        areaTexto.append("\n" + mensaje);
        areaTexto.setCaretPosition(areaTexto.getDocument().getLength());
    }

    public void setAtacarHabilitado(boolean habilitado) {
        btnAtacar.setEnabled(habilitado);
        btnAtacar.setToolTipText(habilitado ? null : "Selecciona una carta viva para continuar");
    }
}
