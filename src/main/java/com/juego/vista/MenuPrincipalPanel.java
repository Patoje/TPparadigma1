package com.juego.vista;

import com.juego.modelo.elementos.TipoElemento;
import com.juego.modelo.ia.TipoIA;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Menú Principal con diseño dividido estilo retro landscape animado:
 * - Mitad Superior: Cielo pixel art, nubes flotantes y fondo de bosque.
 * - Mitad Inferior: Pradera/tierra retro con los 3 botones 3D y selector de IA.
 */
public class MenuPrincipalPanel extends JPanel {

    private final JComboBox<String> comboIA;

    // Animación de nubes en el paisaje retro
    private final double[] nubesX = {40.0, 310.0, 620.0, 850.0};
    private final double[] nubesY = {35.0, 75.0, 45.0, 85.0};
    private final double[] nubesVel = {0.45, 0.30, 0.55, 0.25};
    private Timer timerAnimacion;

    public MenuPrincipalPanel(
            ActionListener onJugar,
            ActionListener onConfigurar,
            ActionListener onSalir
    ) {
        setLayout(new GridLayout(2, 1));
        iniciarAnimacionPaisaje();

        // ==========================================
        // 1. MITAD SUPERIOR: CIELO Y BOSQUE RETRO
        // ==========================================
        JPanel mitadSuperior = new JPanel();
        mitadSuperior.setOpaque(false);
        mitadSuperior.setLayout(new BoxLayout(mitadSuperior, BoxLayout.Y_AXIS));
        mitadSuperior.setBorder(BorderFactory.createEmptyBorder(12, 20, 10, 20));

        // Barra superior con control de volumen deslizable (arriba a la derecha)
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);
        barraSuperior.setMaximumSize(new Dimension(880, 36));

        JPanel panelVolumen = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 23, 42, 215));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(EstilosUI.BORDE_PANEL);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelVolumen.setOpaque(false);
        panelVolumen.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        JLabel lblIconoVol = new JLabel("🔊");
        lblIconoVol.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));

        int volPorcInicial = (int) (Sonidos.getVolumenGlobal() * 100);
        javax.swing.JSlider sliderVolumen = new javax.swing.JSlider(0, 100, volPorcInicial);
        sliderVolumen.setPreferredSize(new Dimension(130, 22));
        sliderVolumen.setOpaque(false);
        sliderVolumen.setFocusable(false);
        sliderVolumen.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        JLabel lblPorcVol = new JLabel(volPorcInicial + "%");
        lblPorcVol.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 12));
        lblPorcVol.setForeground(EstilosUI.TEXTO_DORADO);
        lblPorcVol.setPreferredSize(new Dimension(38, 20));

        sliderVolumen.addChangeListener(e -> {
            int val = sliderVolumen.getValue();
            lblPorcVol.setText(val + "%");
            Sonidos.setVolumenGlobal(val / 100.0f);
        });

        panelVolumen.add(lblIconoVol);
        panelVolumen.add(sliderVolumen);
        panelVolumen.add(lblPorcVol);

        barraSuperior.add(panelVolumen, BorderLayout.EAST);
        mitadSuperior.add(barraSuperior);

        // Título estilizado estilo arcade
        JPanel panelTitulo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 23, 42, 210));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(EstilosUI.TEXTO_DORADO);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelTitulo.setOpaque(false);
        panelTitulo.setMaximumSize(new Dimension(520, 90));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("★ BATALLA ELEMENTAL ★", SwingConstants.CENTER);
        lblTitulo.setFont(EstilosUI.FUENTE_TITULO);
        lblTitulo.setForeground(EstilosUI.TEXTO_DORADO);

        JLabel lblSubtitulo = new JLabel("Agua 💧 · Tierra 🌱 · Fuego 🔥", SwingConstants.CENTER);
        lblSubtitulo.setFont(EstilosUI.FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(new Color(140, 215, 255));

        panelTitulo.add(lblTitulo, BorderLayout.NORTH);
        panelTitulo.add(lblSubtitulo, BorderLayout.SOUTH);
        panelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Vitrina de los 3 iniciales (Squirtle, Bulbasaur, Charmander)
        JPanel panelShowcase = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 8));
        panelShowcase.setOpaque(false);
        panelShowcase.add(crearInsigniaStarter(TipoElemento.AGUA));
        panelShowcase.add(crearInsigniaStarter(TipoElemento.TIERRA));
        panelShowcase.add(crearInsigniaStarter(TipoElemento.FUEGO));
        panelShowcase.setAlignmentX(Component.CENTER_ALIGNMENT);

        mitadSuperior.add(Box.createRigidArea(new Dimension(0, 10)));
        mitadSuperior.add(panelTitulo);
        mitadSuperior.add(Box.createRigidArea(new Dimension(0, 12)));
        mitadSuperior.add(panelShowcase);

        // ==========================================
        // 2. MITAD INFERIOR: TIERRA/PASTO Y BOTONES RETRO
        // ==========================================
        JPanel mitadInferior = new JPanel();
        mitadInferior.setOpaque(false);
        mitadInferior.setLayout(new BoxLayout(mitadInferior, BoxLayout.Y_AXIS));
        mitadInferior.setBorder(BorderFactory.createEmptyBorder(10, 30, 25, 30));

        // Selector de IA Oponente
        JPanel panelIA = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelIA.setOpaque(false);

        JLabel lblTipoIA = new JLabel("Oponente IA: ");
        lblTipoIA.setFont(EstilosUI.FUENTE_NEGRITA);
        lblTipoIA.setForeground(Color.WHITE);

        comboIA = new JComboBox<>(new String[]{
                "🎲 Aleatoria al azar (Sorpresa)",
                "IA Aleatoria (Fácil)",
                "IA Estratégica (Media)",
                "Super IA (Difícil)"
        });
        comboIA.setFont(EstilosUI.FUENTE_NEGRITA);
        comboIA.setBackground(new Color(22, 32, 48));
        comboIA.setForeground(EstilosUI.TEXTO_DORADO);
        comboIA.setPreferredSize(new Dimension(290, 42));
        comboIA.setFocusable(false);
        comboIA.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.TEXTO_DORADO, 2, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 4)
        ));

        comboIA.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▼") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(36, 52, 78));
                        g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
                        g2.setColor(EstilosUI.TEXTO_DORADO);
                        g2.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
                        java.awt.FontMetrics fm = g2.getFontMetrics();
                        int tx = (getWidth() - fm.stringWidth("▼")) / 2;
                        int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                        g2.drawString("▼", tx, ty);
                        g2.dispose();
                    }
                };
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                btn.setPreferredSize(new Dimension(28, 28));
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, java.awt.Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(22, 32, 48));
                g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                g2.dispose();
            }
        });

        comboIA.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(EstilosUI.FUENTE_NEGRITA);
                label.setOpaque(true);
                if (isSelected) {
                    label.setBackground(new Color(42, 85, 138));
                    label.setForeground(EstilosUI.TEXTO_DORADO);
                } else {
                    label.setBackground(new Color(20, 28, 42));
                    label.setForeground(Color.WHITE);
                }
                label.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
                return label;
            }
        });

        comboIA.addActionListener(e -> Sonidos.reproducirClick());

        panelIA.add(lblTipoIA);
        panelIA.add(comboIA);
        panelIA.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Los 3 botones retro 3D (estilo referencia de sprites pill-buttons)
        JButton btnJugar = EstilosUI.crearBotonRetro("▶  JUGAR PARTIDA", EstilosUI.RETRO_VERDE, Color.WHITE, 18);
        btnJugar.setPreferredSize(new Dimension(290, 48));
        btnJugar.setMaximumSize(new Dimension(290, 48));
        btnJugar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnJugar.addActionListener(onJugar);

        JButton btnConfig = EstilosUI.crearBotonRetro("⚙️  CONFIGURAR DAÑOS", EstilosUI.RETRO_AZUL, Color.WHITE, 18);
        btnConfig.setPreferredSize(new Dimension(290, 46));
        btnConfig.setMaximumSize(new Dimension(290, 46));
        btnConfig.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfig.addActionListener(onConfigurar);

        JButton btnSalir = EstilosUI.crearBotonRetro("🚪  SALIR", EstilosUI.RETRO_NARANJA, Color.WHITE, 18);
        btnSalir.setPreferredSize(new Dimension(290, 46));
        btnSalir.setMaximumSize(new Dimension(290, 46));
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.addActionListener(onSalir);

        mitadInferior.add(panelIA);
        mitadInferior.add(Box.createRigidArea(new Dimension(0, 16)));
        mitadInferior.add(btnJugar);
        mitadInferior.add(Box.createRigidArea(new Dimension(0, 12)));
        mitadInferior.add(btnConfig);
        mitadInferior.add(Box.createRigidArea(new Dimension(0, 12)));
        mitadInferior.add(btnSalir);

        add(mitadSuperior);
        add(mitadInferior);
    }

    private void iniciarAnimacionPaisaje() {
        timerAnimacion = new Timer(35, e -> {
            for (int i = 0; i < nubesX.length; i++) {
                nubesX[i] += nubesVel[i];
                if (nubesX[i] > getWidth() + 50) {
                    nubesX[i] = -120;
                }
            }
            repaint();
        });
        timerAnimacion.start();
    }

    private JPanel crearInsigniaStarter(TipoElemento tipo) {
        JPanel card = new JPanel(new BorderLayout(2, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 28, 42, 210));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(EstilosUI.getColorTipo(tipo));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(110, 85));
        card.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        BufferedImage img = RecursosGraficos.getSpriteFrente(tipo);
        BufferedImage scaled = RecursosGraficos.escalarPixelArt(img, 52, 52);
        JLabel lblImg = new JLabel();
        if (scaled != null) {
            lblImg.setIcon(new javax.swing.ImageIcon(scaled));
        }
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTxt = new JLabel(tipo.getIcono() + " " + tipo.getNombreCriatura(), SwingConstants.CENTER);
        lblTxt.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 10));
        lblTxt.setForeground(Color.WHITE);

        card.add(lblImg, BorderLayout.CENTER);
        card.add(lblTxt, BorderLayout.SOUTH);
        return card;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int mitadY = h / 2;

        // 1. Mitad Superior: Cielo retro
        GradientPaint cielo = new GradientPaint(0, 0, new Color(110, 185, 245), 0, mitadY, new Color(210, 240, 255));
        g2.setPaint(cielo);
        g2.fillRect(0, 0, w, mitadY);

        // Nubes pixel animadas flotando
        g2.setColor(new Color(255, 255, 255, 190));
        for (int i = 0; i < nubesX.length; i++) {
            int nx = (int) nubesX[i];
            int ny = (int) nubesY[i];
            dibujarNubeRetro(g2, nx, ny);
        }

        // Siluetas de copas del bosque en el horizonte (entre cielo y pradera)
        g2.setColor(new Color(40, 85, 55));
        for (int x = -10; x < w + 40; x += 45) {
            int r = 35 + (Math.abs(x) % 20);
            g2.fillOval(x, mitadY - r / 2, r * 2, r);
        }

        // 2. Mitad Inferior: Pradera/tierra retro
        GradientPaint pasto = new GradientPaint(0, mitadY, new Color(68, 142, 54), 0, h, new Color(32, 82, 38));
        g2.setPaint(pasto);
        g2.fillRect(0, mitadY, w, h - mitadY);

        // Borde divisorio sutil de césped
        g2.setColor(new Color(45, 105, 40));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, mitadY, w, mitadY);

        // Detalles retro en el pasto (briznas y florecillas)
        g2.setColor(new Color(105, 185, 75, 160));
        for (int px = 25; px < w; px += 55) {
            int py = mitadY + 15 + ((px * 7) % (h / 2 - 30));
            g2.drawLine(px, py, px - 2, py - 4);
            g2.drawLine(px, py, px + 2, py - 4);
        }

        g2.dispose();
    }

    private void dibujarNubeRetro(Graphics2D g2, int x, int y) {
        g2.fillOval(x, y, 65, 20);
        g2.fillOval(x + 12, y - 8, 40, 22);
        g2.fillOval(x + 30, y - 4, 30, 18);
    }

    public TipoIA getTipoIASeleccionado() {
        int index = comboIA.getSelectedIndex();
        return switch (index) {
            case 1 -> TipoIA.ALEATORIA;
            case 2 -> TipoIA.ESTRATEGICA;
            case 3 -> TipoIA.SUPER_IA;
            default -> TipoIA.obtenerAleatoria();
        };
    }
}
