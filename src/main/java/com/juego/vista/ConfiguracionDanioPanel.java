package com.juego.vista;

import com.juego.modelo.combate.ParTipos;
import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.TipoElemento;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla para cambiar los porcentajes de daño antes de jugar.
 * Esos números se guardan en la tabla y el combate los usa tal cual.
 */
public class ConfiguracionDanioPanel extends JPanel {

    private final TablaEfectividad tablaEfectividad;
    private final Map<ParTipos, ControlDanioRetro> controlesDanio;

    public ConfiguracionDanioPanel(TablaEfectividad tabla, ActionListener onVolver) {
        this.tablaEfectividad = tabla;
        this.controlesDanio = new HashMap<>();

        setLayout(new BorderLayout());

        // Cabecera estilizada con marco retro
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));

        JPanel panelTitulo = new JPanel(new BorderLayout(5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 23, 42, 215));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(EstilosUI.TEXTO_DORADO);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelTitulo.setOpaque(false);
        panelTitulo.setMaximumSize(new Dimension(580, 75));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel lblTitulo = new JLabel("⚙️ MATRIZ DE EFECTIVIDAD Y DAÑO", SwingConstants.CENTER);
        lblTitulo.setFont(EstilosUI.FUENTE_TITULO);
        lblTitulo.setForeground(EstilosUI.TEXTO_DORADO);

        JLabel lblSub = new JLabel("Personaliza el daño (%) que cada elemento inflige a su oponente", SwingConstants.CENTER);
        lblSub.setFont(EstilosUI.FUENTE_NORMAL);
        lblSub.setForeground(new Color(175, 225, 255));

        panelTitulo.add(lblTitulo, BorderLayout.NORTH);
        panelTitulo.add(lblSub, BorderLayout.SOUTH);
        panelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(panelTitulo);

        // Panel central con las 9 interacciones en grilla 3x3
        JPanel panelGrid = new JPanel(new GridLayout(3, 3, 12, 10));
        panelGrid.setOpaque(false);
        panelGrid.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        cargarSpinners(panelGrid);

        JScrollPane scrollPane = new JScrollPane(panelGrid);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Barra inferior con los botones 3D retro
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 14));
        panelBotones.setOpaque(false);

        JButton btnGuardar = EstilosUI.crearBotonRetro("💾 Guardar Cambios", EstilosUI.RETRO_VERDE, Color.WHITE, 16);
        btnGuardar.setPreferredSize(new Dimension(210, 44));
        btnGuardar.addActionListener(e -> {
            guardarValores();
            JOptionPane.showMessageDialog(this,
                    "¡Matriz de daño actualizada exitosamente!",
                    "Configuración Guardada",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnRestablecer = EstilosUI.crearBotonRetro("🔄 Valores por Defecto", EstilosUI.RETRO_NARANJA, Color.WHITE, 16);
        btnRestablecer.setPreferredSize(new Dimension(220, 44));
        btnRestablecer.addActionListener(e -> {
            tablaEfectividad.restablecerValoresPorDefecto();
            actualizarSpinnersDesdeTabla();
            JOptionPane.showMessageDialog(this,
                    "Se restauraron los porcentajes oficiales del TP.",
                    "Restaurado",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnVolver = EstilosUI.crearBotonRetro("⬅ Volver al Menú", EstilosUI.RETRO_AZUL, Color.WHITE, 16);
        btnVolver.setPreferredSize(new Dimension(200, 44));
        btnVolver.addActionListener(onVolver);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnRestablecer);
        panelBotones.add(btnVolver);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarSpinners(JPanel contenedor) {
        TipoElemento[] tipos = TipoElemento.values();

        for (TipoElemento atacante : tipos) {
            for (TipoElemento defensor : tipos) {
                ParTipos par = new ParTipos(atacante, defensor);
                double valorActual = tablaEfectividad.obtenerDanio(atacante, defensor);

                // Tarjeta retro con doble bisel 3D
                JPanel cardPar = new JPanel(new BorderLayout(6, 2)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        // Fondo tarjeta oscura con contraste
                        g2.setColor(new Color(18, 26, 40, 235));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                        // Borde retro con color del elemento atacante
                        g2.setColor(EstilosUI.getColorTipo(atacante));
                        g2.setStroke(new BasicStroke(2));
                        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                cardPar.setOpaque(false);
                cardPar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 10));

                JLabel lblAtacante = new JLabel(atacante.getIcono() + " " + atacante.getNombre() + " ➔ " + defensor.getIcono() + " " + defensor.getNombre());
                lblAtacante.setFont(new Font("SansSerif", Font.BOLD, 12));
                lblAtacante.setForeground(Color.WHITE);

                ControlDanioRetro control = new ControlDanioRetro(valorActual);

                cardPar.add(lblAtacante, BorderLayout.CENTER);
                cardPar.add(control, BorderLayout.EAST);

                controlesDanio.put(par, control);
                contenedor.add(cardPar);
            }
        }
    }

    private void guardarValores() {
        for (Map.Entry<ParTipos, ControlDanioRetro> entry : controlesDanio.entrySet()) {
            double nuevoValor = entry.getValue().getValor();
            tablaEfectividad.configurarDanio(entry.getKey().atacante(), entry.getKey().defensor(), nuevoValor);
        }
    }

    private void actualizarSpinnersDesdeTabla() {
        for (Map.Entry<ParTipos, ControlDanioRetro> entry : controlesDanio.entrySet()) {
            double valor = tablaEfectividad.obtenerDanio(entry.getKey().atacante(), entry.getKey().defensor());
            entry.getValue().setValor(valor);
        }
    }

    /**
     * Selector numérico estilo arcade / retro con botones [-] y [+] y badge de porcentaje.
     */
    public static class ControlDanioRetro extends JPanel {
        private double valor;
        private final JLabel lblDisplay;

        public ControlDanioRetro(double valorInicial) {
            this.valor = valorInicial;
            setLayout(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            setOpaque(false);

            JButton btnMenos = crearBotonPaso("-");
            JButton btnMas = crearBotonPaso("+");

            lblDisplay = new JLabel(String.format("%.0f%%", valor), SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(10, 15, 24));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(55, 75, 105));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            lblDisplay.setFont(new Font("Monospaced", Font.BOLD, 13));
            lblDisplay.setForeground(EstilosUI.TEXTO_DORADO);
            lblDisplay.setPreferredSize(new Dimension(52, 28));
            lblDisplay.setOpaque(false);

            btnMenos.addActionListener(e -> {
                Sonidos.reproducirClick();
                if (valor > 0.0) {
                    valor = Math.max(0.0, valor - 5.0);
                    actualizarDisplay();
                }
            });

            btnMas.addActionListener(e -> {
                Sonidos.reproducirClick();
                if (valor < 100.0) {
                    valor = Math.min(100.0, valor + 5.0);
                    actualizarDisplay();
                }
            });

            add(btnMenos);
            add(lblDisplay);
            add(btnMas);
        }

        private void actualizarDisplay() {
            lblDisplay.setText(String.format("%.0f%%", valor));
        }

        public double getValor() {
            return valor;
        }

        public void setValor(double nuevo) {
            this.valor = nuevo;
            actualizarDisplay();
        }

        private JButton crearBotonPaso(String signo) {
            JButton btn = new JButton(signo) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth();
                    int h = getHeight();
                    boolean pres = getModel().isPressed();
                    boolean hov = getModel().isRollover();

                    Color cFondo = hov ? new Color(55, 80, 115) : new Color(32, 45, 68);
                    Color cBorde = pres ? EstilosUI.TEXTO_DORADO : new Color(70, 95, 135);

                    int offY = pres ? 1 : 0;
                    g2.setColor(new Color(12, 16, 24));
                    g2.fillRoundRect(1, 2, w - 2, h - 2, 6, 6); // sombra 3D

                    g2.setColor(cFondo);
                    g2.fillRoundRect(1, 1 + offY, w - 2, h - 3, 6, 6);

                    g2.setColor(cBorde);
                    g2.drawRoundRect(1, 1 + offY, w - 3, h - 4, 6, 6);

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                    java.awt.FontMetrics fm = g2.getFontMetrics();
                    int tx = (w - fm.stringWidth(signo)) / 2;
                    int ty = ((h - 2) + fm.getAscent() - fm.getDescent()) / 2 + offY;
                    g2.drawString(signo, tx, ty);
                    g2.dispose();
                }
            };
            btn.setPreferredSize(new Dimension(28, 28));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int lineaCostera = (int) (h * 0.40);

        // 1. Cielo Tropical Retro
        GradientPaint cielo = new GradientPaint(0, 0, new Color(65, 190, 235), 0, (int)(lineaCostera * 0.55), new Color(254, 250, 224));
        g2.setPaint(cielo);
        g2.fillRect(0, 0, w, (int)(lineaCostera * 0.55));

        // Sol Tropical Pixel Art
        g2.setColor(new Color(255, 235, 120, 80));
        g2.fillOval(w - 145, 15, 75, 75);
        g2.setColor(new Color(255, 215, 60, 150));
        g2.fillOval(w - 135, 25, 55, 55);
        g2.setColor(new Color(255, 248, 170));
        g2.fillOval(w - 125, 35, 35, 35);

        // Nubes esponjosas en la costa
        g2.setColor(new Color(255, 255, 255, 175));
        g2.fillOval((int)(w * 0.08), 22, 85, 22);
        g2.fillOval((int)(w * 0.12), 15, 55, 24);
        g2.fillOval((int)(w * 0.65), 26, 90, 20);
        g2.fillOval((int)(w * 0.69), 18, 55, 24);

        // 2. Océano Turquesa / Zafiro con olas retro
        GradientPaint oceano = new GradientPaint(0, (int)(lineaCostera * 0.55), new Color(0, 110, 175), 0, lineaCostera, new Color(0, 185, 220));
        g2.setPaint(oceano);
        g2.fillRect(0, (int)(lineaCostera * 0.55), w, (int)(lineaCostera * 0.45));

        // Silueta de Isla y Palmeras en el horizonte lejano
        int islaX = (int) (w * 0.18);
        int islaY = (int) (lineaCostera * 0.62);
        g2.setColor(new Color(30, 95, 105));
        g2.fillArc(islaX - 55, islaY - 12, 115, 26, 0, 180);

        // Palmera en la isla
        g2.setColor(new Color(45, 65, 45));
        g2.fillRect(islaX + 5, islaY - 28, 3, 22);
        g2.setColor(new Color(25, 110, 65));
        g2.drawLine(islaX + 6, islaY - 28, islaX - 8, islaY - 33);
        g2.drawLine(islaX + 6, islaY - 28, islaX + 20, islaY - 33);
        g2.drawLine(islaX + 6, islaY - 28, islaX + 3, islaY - 37);

        // Olas pixel art y espuma blanca
        g2.setColor(new Color(202, 240, 248, 190));
        for (int x = -10; x < w + 30; x += 35) {
            g2.drawArc(x, (int)(lineaCostera * 0.75), 30, 8, 0, 180);
            g2.drawArc(x + 15, (int)(lineaCostera * 0.90), 25, 6, 0, 180);
        }

        // Línea de rompiente de olas con espuma blanca
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        for (int x = -15; x < w + 30; x += 40) {
            g2.drawArc(x, lineaCostera - 4, 38, 8, 0, 180);
        }

        // 3. Playa de Arena Dorada Retro
        GradientPaint arena = new GradientPaint(0, lineaCostera, new Color(238, 202, 115), 0, h, new Color(205, 155, 105));
        g2.setPaint(arena);
        g2.fillRect(0, lineaCostera, w, h - lineaCostera);

        // Brillo y textura de arena dorada
        g2.setColor(new Color(255, 235, 170, 160));
        int[] arenasX = {100, 270, 410, 630, 790, 180, 520, 860};
        int[] arenasY = {lineaCostera + 20, lineaCostera + 55, lineaCostera + 30, lineaCostera + 65, lineaCostera + 35, lineaCostera + 120, lineaCostera + 150, lineaCostera + 110};
        for (int i = 0; i < arenasX.length; i++) {
            g2.fillRect(arenasX[i], arenasY[i], 2, 2);
        }

        // Conchas en la arena
        g2.setColor(new Color(244, 162, 97));
        int[] conchasX = {60, 220, 480, 680, 830, 150, 560, 780};
        int[] conchasY = {lineaCostera + 35, lineaCostera + 80, lineaCostera + 45, lineaCostera + 90, lineaCostera + 60, lineaCostera + 140, lineaCostera + 170, lineaCostera + 210};
        for (int i = 0; i < conchasX.length; i++) {
            g2.fillOval(conchasX[i], conchasY[i], 6, 5);
        }

        // Estrellas de mar pequeñas color coral
        g2.setColor(new Color(231, 111, 81));
        g2.fillOval(320, lineaCostera + 110, 7, 7);
        g2.fillOval(720, lineaCostera + 130, 7, 7);

        g2.dispose();
    }
}
