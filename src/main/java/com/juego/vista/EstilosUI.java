package com.juego.vista;

import com.juego.modelo.elementos.TipoElemento;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Constantes estéticas y componentes retro con estilo píxel y 3D (estilo GBA/Arcade).
 */
public class EstilosUI {
    public static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 17);
    public static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FUENTE_NEGRITA = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FUENTE_PEQUENA = new Font("SansSerif", Font.BOLD, 11);
    public static final Font FUENTE_RETRO_LOG = new Font("Monospaced", Font.BOLD, 13);

    public static final Color FONDO_OSCURO = new Color(18, 24, 34);
    public static final Color FONDO_PANEL = new Color(28, 36, 50);
    public static final Color BORDE_PANEL = new Color(55, 68, 92);
    public static final Color TEXTO_CLARO = new Color(245, 248, 255);
    public static final Color TEXTO_DORADO = new Color(255, 215, 0);

    // Colores para botones retro 3D (inspirados en sprites arcade)
    public static final Color RETRO_VERDE = new Color(38, 178, 122);
    public static final Color RETRO_AZUL = new Color(42, 148, 222);
    public static final Color RETRO_NARANJA = new Color(236, 120, 48);
    public static final Color RETRO_GRIS = new Color(125, 140, 155);

    public static Color getColorTipo(TipoElemento tipo) {
        if (tipo == null) return Color.GRAY;
        return switch (tipo) {
            case AGUA -> new Color(41, 128, 185);
            case TIERRA -> new Color(39, 174, 96);
            case FUEGO -> new Color(230, 81, 0);
        };
    }

    public static Color getColorVida(double porcentaje) {
        if (porcentaje > 50.0) {
            return new Color(46, 204, 113); // Verde
        } else if (porcentaje > 20.0) {
            return new Color(241, 196, 15); // Amarillo / Naranja
        } else {
            return new Color(231, 76, 60);   // Rojo crítico
        }
    }

    /**
     * Crea un botón retro estilo píxel 3D con bisel de sombra, brillo superior y efecto de presión
     * exactamente como los botones de la referencia gráfica retro.
     */
    public static JButton crearBoton(String texto, Color colorBase, Color colorTexto) {
        return crearBotonRetro(texto, colorBase, colorTexto, 16);
    }

    public static JButton crearBotonRetro(String texto, Color colorBase, Color colorTexto, int radio) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                boolean presionado = getModel().isPressed();
                boolean hover = getModel().isRollover();

                int offsetY = presionado ? 3 : 0;
                int sombraH = presionado ? 2 : 5;

                Color cBase = hover ? colorBase.brighter() : colorBase;
                Color cSombra = colorBase.darker().darker();
                Color cBrillo = colorBase.brighter().brighter();
                Color cBorde = new Color(15, 20, 25);

                // 1. Sombra inferior 3D
                g2.setColor(cSombra);
                g2.fillRoundRect(2, 4, w - 4, h - 5, radio, radio);

                // 2. Cuerpo del botón
                g2.setColor(cBase);
                g2.fillRoundRect(2, 2 + offsetY, w - 4, h - sombraH - 2, radio, radio);

                // 3. Brillo de relieve superior
                if (!presionado) {
                    g2.setColor(new Color(cBrillo.getRed(), cBrillo.getGreen(), cBrillo.getBlue(), 140));
                    g2.fillRoundRect(4, 3, w - 8, (h - sombraH) / 3, radio - 2, radio - 2);
                }

                // 4. Borde oscuro exterior
                g2.setColor(cBorde);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(2, 2 + offsetY, w - 5, h - sombraH - 2, radio, radio);

                // 5. Texto con sombra sutil de relieve
                g2.setFont(getFont());
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int textX = (w - fm.stringWidth(getText())) / 2;
                int textY = ((h - sombraH) + fm.getAscent() - fm.getDescent()) / 2 + offsetY + 2;

                // Sombra de texto
                g2.setColor(new Color(0, 0, 0, 120));
                g2.drawString(getText(), textX + 1, textY + 1);

                // Texto frontal
                g2.setColor(colorTexto);
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };

        btn.setFont(FUENTE_NEGRITA);
        btn.setForeground(colorTexto);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.addActionListener(e -> Sonidos.reproducirClick());
        return btn;
    }
}
