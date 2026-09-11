package com.juego.vista;

import com.juego.modelo.elementos.Elemento;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

/**
 * Panel inferior que despliega las 5 cartas del jugador con miniaturas, barras de vida
 * e interactividad para cambiar o seleccionar el combatiente activo.
 */
public class CartasReservaPanel extends JPanel {

    private final Consumer<Elemento> onSeleccionarCarta;

    public CartasReservaPanel(Consumer<Elemento> onSeleccionarCarta) {
        this.onSeleccionarCarta = onSeleccionarCarta;
        setLayout(new FlowLayout(FlowLayout.CENTER, 12, 8));
        setOpaque(false);
    }

    public void actualizarCartas(List<Elemento> elementos, Elemento elementoActivo) {
        removeAll();

        for (Elemento e : elementos) {
            boolean esActivo = (elementoActivo != null && elementoActivo.equals(e));
            JPanel tarjeta = crearTarjetaCarta(e, esActivo);
            add(tarjeta);
        }

        revalidate();
        repaint();
    }

    private JPanel crearTarjetaCarta(Elemento elemento, boolean esActivo) {
        JPanel card = new JPanel(new BorderLayout(6, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                Color fondo = elemento.estaVivo() ?
                        (esActivo ? new Color(28, 45, 68) : new Color(22, 30, 44)) :
                        new Color(18, 18, 22);

                // Fondo con bisel
                g2.setColor(fondo);
                g2.fillRoundRect(2, 2, w - 4, h - 4, 12, 12);

                if (esActivo) {
                    // Doble marco dorado brillante para el combatiente activo
                    g2.setColor(EstilosUI.TEXTO_DORADO);
                    g2.setStroke(new java.awt.BasicStroke(2.5f));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, 12, 12);
                    g2.setColor(new Color(255, 215, 0, 70));
                    g2.drawRoundRect(5, 5, w - 11, h - 11, 8, 8);
                } else if (elemento.estaVivo()) {
                    // Borde retro del color del elemento
                    g2.setColor(EstilosUI.getColorTipo(elemento.getTipo()));
                    g2.setStroke(new java.awt.BasicStroke(1.5f));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, 12, 12);
                } else {
                    // Borde gris apagado para debilitado
                    g2.setColor(new Color(60, 60, 65));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, 12, 12);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setPreferredSize(new Dimension(154, 110));
        card.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        card.setOpaque(false);

        // Cuadro retro para el sprite del Pokémon (Pantalla Arcade / Tarjeta Coleccionable)
        JPanel panelSpriteRetro = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int pw = getWidth();
                int ph = getHeight();

                // Pantalla oscura retro
                g2.setColor(new Color(8, 12, 18));
                g2.fillRoundRect(0, 0, pw, ph, 8, 8);

                // Marco pixel art exterior
                g2.setColor(new Color(35, 45, 60));
                g2.drawRoundRect(0, 0, pw - 1, ph - 1, 8, 8);

                // Marco interior con tinte del elemento
                Color cTipo = EstilosUI.getColorTipo(elemento.getTipo());
                g2.setColor(new Color(cTipo.getRed(), cTipo.getGreen(), cTipo.getBlue(), 140));
                g2.drawRoundRect(2, 2, pw - 5, ph - 5, 6, 6);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelSpriteRetro.setOpaque(false);
        panelSpriteRetro.setPreferredSize(new Dimension(52, 52));

        BufferedImage sprite = RecursosGraficos.getSpriteFrente(elemento.getTipo());
        BufferedImage miniSprite = RecursosGraficos.escalarPixelArt(sprite, 46, 46);

        JLabel lblSprite = new JLabel();
        if (miniSprite != null) {
            lblSprite.setIcon(new javax.swing.ImageIcon(miniSprite));
        }
        lblSprite.setHorizontalAlignment(SwingConstants.CENTER);
        panelSpriteRetro.add(lblSprite, BorderLayout.CENTER);

        // Nombre y tipo
        JLabel lblNombre = new JLabel(elemento.getNombre(), SwingConstants.CENTER);
        lblNombre.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 11));
        lblNombre.setForeground(elemento.estaVivo() ? Color.WHITE : Color.GRAY);

        JLabel lblTipo = new JLabel(elemento.getTipo().getIcono() + " " + elemento.getTipo().getNombre(), SwingConstants.CENTER);
        lblTipo.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 10));
        lblTipo.setForeground(EstilosUI.getColorTipo(elemento.getTipo()));

        // Barra de salud en miniatura
        JProgressBar barraHp = new JProgressBar(0, 100);
        barraHp.setValue((int) elemento.getPorcentajeVida());
        barraHp.setStringPainted(true);
        barraHp.setString(elemento.estaVivo() ? String.format("%.0f%%", elemento.getPorcentajeVida()) : "DEBILITADO");
        barraHp.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 9));
        barraHp.setForeground(elemento.estaVivo() ? EstilosUI.getColorVida(elemento.getPorcentajeVida()) : Color.DARK_GRAY);
        barraHp.setBackground(new Color(15, 18, 25));
        barraHp.setPreferredSize(new Dimension(134, 14));

        JPanel infoCentral = new JPanel(new java.awt.GridLayout(2, 1, 0, 2));
        infoCentral.setOpaque(false);
        infoCentral.add(lblNombre);
        infoCentral.add(lblTipo);

        card.add(panelSpriteRetro, BorderLayout.WEST);
        card.add(infoCentral, BorderLayout.CENTER);
        card.add(barraHp, BorderLayout.SOUTH);

        if (elemento.estaVivo()) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Sonidos.reproducirClick();
                    onSeleccionarCarta.accept(elemento);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!esActivo) {
                        card.setBackground(EstilosUI.FONDO_PANEL.brighter());
                        card.repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!esActivo) {
                        card.setBackground(EstilosUI.FONDO_PANEL);
                        card.repaint();
                    }
                }
            });
        }

        return card;
    }
}
