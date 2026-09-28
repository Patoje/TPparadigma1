package com.juego.vista;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Ventana que aparece al terminar: dice si ganaste o perdiste,
 * y deja jugar de nuevo o volver al menú.
 */
public class ModalFinPartida extends JDialog {

    public ModalFinPartida(JFrame parent, boolean esVictoria, String mensajeDetalle, Runnable onRevancha, Runnable onMenu) {
        super(parent, true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(540, 290);
        setLocationRelativeTo(parent);

        // Panel principal con borde retro y fondo degradado
        JPanel panelFondo = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Fondo degradado oscuro
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 28, 42), 0, h, new Color(10, 14, 22));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 20, 20);

                // Doble marco retro
                Color colorBorde = esVictoria ? EstilosUI.TEXTO_DORADO : new Color(231, 76, 60);
                g2.setColor(colorBorde);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(2, 2, w - 5, h - 5, 20, 20);

                g2.setColor(new Color(colorBorde.getRed(), colorBorde.getGreen(), colorBorde.getBlue(), 60));
                g2.drawRoundRect(7, 7, w - 15, h - 15, 14, 14);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        panelFondo.setOpaque(false);
        panelFondo.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        // Contenido central
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        String tituloTexto = esVictoria ? "🏆 ¡VICTORIA TOTAL! 🏆" : "💀 ¡HAS SIDO DERROTADO! 💀";
        Color colorTitulo = esVictoria ? EstilosUI.TEXTO_DORADO : new Color(255, 100, 100);

        JLabel lblTitulo = new JLabel(tituloTexto, SwingConstants.CENTER);
        lblTitulo.setFont(EstilosUI.FUENTE_TITULO);
        lblTitulo.setForeground(colorTitulo);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel(esVictoria ? "¡Has superado todas las cartas de la IA con éxito!" : "Todas tus criaturas han quedado fuera de combate.", SwingConstants.CENTER);
        lblSub.setFont(EstilosUI.FUENTE_NEGRITA);
        lblSub.setForeground(EstilosUI.TEXTO_CLARO);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDetalle = new JLabel(mensajeDetalle, SwingConstants.CENTER);
        lblDetalle.setFont(EstilosUI.FUENTE_NORMAL);
        lblDetalle.setForeground(new Color(180, 195, 220));
        lblDetalle.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(lblTitulo);
        contenido.add(Box.createRigidArea(new Dimension(0, 10)));
        contenido.add(lblSub);
        contenido.add(Box.createRigidArea(new Dimension(0, 6)));
        contenido.add(lblDetalle);

        // Botones de acción retro
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        panelBotones.setOpaque(false);

        JButton btnRevancha = EstilosUI.crearBoton("🔄 JUGAR OTRA VEZ", EstilosUI.RETRO_VERDE, Color.WHITE);
        btnRevancha.setPreferredSize(new Dimension(190, 46));
        btnRevancha.addActionListener(e -> {
            dispose();
            onRevancha.run();
        });

        JButton btnMenu = EstilosUI.crearBoton("🏠 VOLVER AL INICIO", EstilosUI.RETRO_AZUL, Color.WHITE);
        btnMenu.setPreferredSize(new Dimension(200, 46));
        btnMenu.addActionListener(e -> {
            dispose();
            onMenu.run();
        });

        panelBotones.add(btnRevancha);
        panelBotones.add(btnMenu);

        panelFondo.add(contenido, BorderLayout.CENTER);
        panelFondo.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(panelFondo);
    }
}
