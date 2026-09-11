package com.juego.vista;

import com.juego.modelo.elementos.Elemento;
import com.juego.modelo.elementos.TipoElemento;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Escenario 2D de Batalla estilo Pokémon.
 * Renderiza el campo de hierba, plataformas, sprites y barras de vida animadas.
 */
public class BatallaPanel extends JPanel {

    private Elemento elementoHumano;
    private Elemento elementoIA;
    private String nombreIA;

    // Valores animados de vida para efecto suave
    private double vidaAnimadaHumano = 100.0;
    private double vidaAnimadaIA = 100.0;
    private Timer timerAnimacionVida;

    // Efecto de sacudida (shake) al recibir daño
    private int shakeHumanoX = 0;
    private int shakeIAX = 0;
    private Timer timerShake;

    public BatallaPanel() {
        setOpaque(true);
        inicializarTimers();
    }

    private void inicializarTimers() {
        // Timer de reducción suave de la barra de vida
        timerAnimacionVida = new Timer(20, e -> {
            boolean cambio = false;
            if (elementoHumano != null) {
                double targetH = elementoHumano.getPorcentajeVida();
                if (Math.abs(vidaAnimadaHumano - targetH) > 0.5) {
                    vidaAnimadaHumano += (targetH - vidaAnimadaHumano) * 0.15;
                    cambio = true;
                } else {
                    vidaAnimadaHumano = targetH;
                }
            }
            if (elementoIA != null) {
                double targetIA = elementoIA.getPorcentajeVida();
                if (Math.abs(vidaAnimadaIA - targetIA) > 0.5) {
                    vidaAnimadaIA += (targetIA - vidaAnimadaIA) * 0.15;
                    cambio = true;
                } else {
                    vidaAnimadaIA = targetIA;
                }
            }
            if (cambio) {
                repaint();
            } else {
                timerAnimacionVida.stop();
            }
        });
    }

    public void actualizarCombatientes(Elemento humano, Elemento ia, String nombreIA) {
        this.elementoHumano = humano;
        this.elementoIA = ia;
        this.nombreIA = nombreIA;

        if (timerAnimacionVida != null) {
            timerAnimacionVida.start();
        }
        repaint();
    }

    public void reproducirEfectoImpacto(boolean danioAHumano, boolean danioAIA) {
        if (timerShake != null && timerShake.isRunning()) {
            timerShake.stop();
        }

        final int[] frame = {0};
        final int[] offsets = {0, -10, 10, -8, 8, -4, 4, 0};

        timerShake = new Timer(35, e -> {
            if (frame[0] < offsets.length) {
                int offset = offsets[frame[0]];
                if (danioAHumano) shakeHumanoX = offset;
                if (danioAIA) shakeIAX = offset;
                frame[0]++;
                repaint();
            } else {
                shakeHumanoX = 0;
                shakeIAX = 0;
                timerShake.stop();
                repaint();
            }
        });
        timerShake.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int w = getWidth();
        int h = getHeight();

        // 1. Dibujar escenario completo de Bosque Retro
        dibujarEscenarioBosque(g2, w, h);

        // 2. Plataformas de combate integradas al claro del bosque
        // Plataforma Rival (asentada sobre la pradera del bosque, con árboles detrás)
        int platIAX = (int) (w * 0.68);
        int platIAY = (int) (h * 0.44);
        int platIAW = 230;
        int platIAH = 60;

        // Sombra de plataforma
        g2.setColor(new Color(25, 60, 25, 140));
        g2.fillOval(platIAX - platIAW / 2 + 5, platIAY - platIAH / 2 + 8, platIAW, platIAH);

        // Superficie plataforma rival (hierba clara)
        g2.setColor(new Color(118, 196, 70));
        g2.fillOval(platIAX - platIAW / 2, platIAY - platIAH / 2, platIAW, platIAH);
        g2.setColor(new Color(60, 130, 35));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(platIAX - platIAW / 2, platIAY - platIAH / 2, platIAW, platIAH);

        // Plataforma Jugador (Abajo a la izquierda en primer plano)
        int platHX = (int) (w * 0.24);
        int platHY = (int) (h * 0.74);
        int platHW = 300;
        int platHH = 80;

        // Sombra plataforma jugador
        g2.setColor(new Color(20, 50, 20, 150));
        g2.fillOval(platHX - platHW / 2 + 6, platHY - platHH / 2 + 10, platHW, platHH);

        // Superficie plataforma jugador
        g2.setColor(new Color(118, 196, 70));
        g2.fillOval(platHX - platHW / 2, platHY - platHH / 2, platHW, platHH);
        g2.setColor(new Color(60, 130, 35));
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(platHX - platHW / 2, platHY - platHH / 2, platHW, platHH);

        // 3. Sprites de Pokémon
        // Sprite Rival (Frente)
        if (elementoIA != null && elementoIA.estaVivo()) {
            BufferedImage spriteIA = RecursosGraficos.getSpriteFrente(elementoIA.getTipo());
            if (spriteIA != null) {
                int spriteW = 150;
                int spriteH = 150;
                int x = (platIAX - spriteW / 2) + shakeIAX;
                int y = platIAY - spriteH + 20;
                g2.drawImage(spriteIA, x, y, spriteW, spriteH, null);
            }
        }

        // Sprite Jugador (Espalda)
        if (elementoHumano != null && elementoHumano.estaVivo()) {
            BufferedImage imgSpriteJugador = RecursosGraficos.getSpriteEspalda(elementoHumano.getTipo());
            if (imgSpriteJugador != null) {
                int anchoSprite = 165;
                int altoSprite = 165;
                int x = (platHX - anchoSprite / 2) + shakeHumanoX;
                int y = platHY - altoSprite + 30;
                g2.drawImage(imgSpriteJugador, x, y, anchoSprite, altoSprite, null);
            }
        }

        // 4. Cajas HUD de Estado estilo Pokémon
        dibujarHUDIA(g2, 35, 30);
        dibujarHUDHumano(g2, w - 315, (int) (h * 0.48));

        g2.dispose();
    }

    private void dibujarHUDIA(Graphics2D g2, int x, int y) {
        if (elementoIA == null) return;

        int cajaW = 295;
        int cajaH = 80;

        // Fondo caja
        g2.setColor(new Color(25, 32, 45, 230));
        g2.fillRoundRect(x, y, cajaW, cajaH, 14, 14);
        g2.setColor(new Color(60, 75, 100));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, cajaW, cajaH, 14, 14);

        // Nombre e info con etiqueta (Rival) explícita
        g2.setFont(EstilosUI.FUENTE_NEGRITA);
        g2.setColor(Color.WHITE);
        g2.drawString(elementoIA.getNombre() + " (Rival)", x + 14, y + 24);

        // Badge de Tipo
        TipoElemento tipo = elementoIA.getTipo();
        g2.setFont(EstilosUI.FUENTE_PEQUENA);
        g2.setColor(EstilosUI.getColorTipo(tipo));
        g2.drawString(tipo.getIcono() + " " + tipo.getNombre(), x + 195, y + 24);

        // Indicador IA
        g2.setColor(new Color(180, 195, 220));
        g2.drawString((nombreIA != null ? nombreIA : "Oponente"), x + 14, y + 42);

        // Barra de salud
        dibujarBarraVida(g2, x + 14, y + 50, 265, 12, vidaAnimadaIA);
    }

    private void dibujarHUDHumano(Graphics2D g2, int x, int y) {
        if (elementoHumano == null) return;

        int cajaW = 280;
        int cajaH = 90;

        // Fondo caja
        g2.setColor(new Color(25, 32, 45, 230));
        g2.fillRoundRect(x, y, cajaW, cajaH, 14, 14);
        g2.setColor(new Color(60, 75, 100));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, cajaW, cajaH, 14, 14);

        // Nombre
        g2.setFont(EstilosUI.FUENTE_NEGRITA);
        g2.setColor(Color.WHITE);
        g2.drawString(elementoHumano.getNombre(), x + 14, y + 24);

        // Badge de Tipo
        TipoElemento tipo = elementoHumano.getTipo();
        g2.setFont(EstilosUI.FUENTE_PEQUENA);
        g2.setColor(EstilosUI.getColorTipo(tipo));
        g2.drawString(tipo.getIcono() + " " + tipo.getNombre(), x + 175, y + 24);

        // Indicador PS
        g2.setColor(new Color(180, 195, 220));
        g2.drawString(String.format("PS: %.0f / %.0f", elementoHumano.getVidaActual(), elementoHumano.getVidaMaxima()), x + 14, y + 44);

        // Barra de salud
        dibujarBarraVida(g2, x + 14, y + 54, 250, 14, vidaAnimadaHumano);
    }

    private void dibujarBarraVida(Graphics2D g2, int x, int y, int width, int height, double porcentajeVida) {
        // Fondo barra
        g2.setColor(new Color(40, 40, 45));
        g2.fillRoundRect(x, y, width, height, 6, 6);

        // Relleno vida
        int fillW = (int) (width * (Math.max(0.0, Math.min(100.0, porcentajeVida)) / 100.0));
        g2.setColor(EstilosUI.getColorVida(porcentajeVida));
        g2.fillRoundRect(x, y, fillW, height, 6, 6);

        // Borde
        g2.setColor(new Color(80, 90, 110));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(x, y, width, height, 6, 6);

        // Texto porcentaje
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        String txt = String.format("%.0f%%", porcentajeVida);
        g2.drawString(txt, x + width - 30, y + height - 2);
    }

    private void dibujarEscenarioBosque(Graphics2D g2, int w, int h) {
        int horizonte = (int) (h * 0.36);

        // 1. Cielo brumoso retro estilo pixel art (Verde menta a cian pálido como la referencia)
        GradientPaint cielo = new GradientPaint(0, 0, new Color(135, 210, 200), 0, horizonte, new Color(205, 240, 230));
        g2.setPaint(cielo);
        g2.fillRect(0, 0, w, horizonte);

        // Nubes suaves de fondo
        g2.setColor(new Color(255, 255, 255, 150));
        g2.fillOval((int)(w * 0.10), (int)(h * 0.06), 110, 24);
        g2.fillOval((int)(w * 0.65), (int)(h * 0.08), 130, 28);

        // 2. CAPA 1: Siluetas de pinos lejanos en la niebla (Cian pálido / Teal difuso)
        Color colorPinosBruma = new Color(95, 165, 155, 190);
        int[] spiresX1 = { 20, 70, 130, 190, 250, 310, 370, 440, 500, 560, 620, 680, 750, 810, 870, 920 };
        for (int sx : spiresX1) {
            int altoSpire = 85 + (Math.abs(sx * 7) % 35);
            dibujarPinoSilueta(g2, sx, horizonte + 5, 46, altoSpire, colorPinosBruma);
        }

        // Capa de niebla entre capas
        g2.setColor(new Color(180, 225, 215, 110));
        g2.fillRect(0, horizonte - 15, w, 25);

        // 3. CAPA 2: Siluetas de bosque denso medio (Teal marino oscuro)
        Color colorPinosMedio = new Color(24, 60, 68);
        int[] spiresX2 = { -10, 45, 100, 160, 220, 280, 340, 410, 470, 530, 590, 650, 720, 780, 840, 900 };
        for (int sx : spiresX2) {
            int altoSpire = 100 + (Math.abs(sx * 11) % 40);
            dibujarPinoSilueta(g2, sx, horizonte + 20, 58, altoSpire, colorPinosMedio);
        }

        // 4. Suelo de pradera y claro de bosque (Capa de tierra/musgo)
        GradientPaint pradera = new GradientPaint(0, horizonte, new Color(55, 135, 65), 0, h, new Color(20, 55, 28));
        g2.setPaint(pradera);
        g2.fillRect(0, horizonte, w, h - horizonte);

        // Borde de musgo sombreado
        g2.setColor(new Color(15, 45, 25, 90));
        g2.fillRect(0, horizonte, w, 28);

        // 5. CAPA 3: PINOS PIXEL ART DE PRIMER PLANO (Detallados con niveles de agujas y luces)
        // Lado izquierdo
        dibujarPinoPixelArt(g2, (int)(w * 0.03), horizonte + 28, 66, 135);
        dibujarPinoPixelArt(g2, (int)(w * 0.11), horizonte + 40, 78, 150);
        dibujarPinoPixelArt(g2, (int)(w * 0.20), horizonte + 22, 60, 120);

        // Centro (fondo del claro)
        dibujarPinoPixelArt(g2, (int)(w * 0.38), horizonte + 16, 68, 115);
        dibujarPinoPixelArt(g2, (int)(w * 0.49), horizonte + 24, 74, 130);

        // Detrás del rival (bosque denso de pinos)
        dibujarPinoPixelArt(g2, (int)(w * 0.61), horizonte + 10, 64, 120);
        dibujarPinoPixelArt(g2, (int)(w * 0.73), horizonte + 8, 76, 140);
        dibujarPinoPixelArt(g2, (int)(w * 0.84), horizonte + 26, 82, 155);
        dibujarPinoPixelArt(g2, (int)(w * 0.95), horizonte + 38, 88, 160);

        // 6. Arbustos redondeados pixel art (Idénticos a la referencia de imagen 5)
        dibujarArbustoPixelArt(g2, (int)(w * 0.07), horizonte + 45, 20);
        dibujarArbustoPixelArt(g2, (int)(w * 0.17), horizonte + 36, 16);
        dibujarArbustoPixelArt(g2, (int)(w * 0.32), horizonte + 28, 18);
        dibujarArbustoPixelArt(g2, (int)(w * 0.55), horizonte + 32, 22);
        dibujarArbustoPixelArt(g2, (int)(w * 0.67), horizonte + 25, 17);
        dibujarArbustoPixelArt(g2, (int)(w * 0.79), horizonte + 35, 20);
        dibujarArbustoPixelArt(g2, (int)(w * 0.90), horizonte + 48, 24);

        // 7. Rayos de luz solar que atraviesan los pinos (God Rays)
        Graphics2D gRayos = (Graphics2D) g2.create();
        gRayos.setColor(new Color(245, 255, 220, 22));
        int[] rx1 = {(int)(w * 0.22), (int)(w * 0.42), (int)(w * 0.32), (int)(w * 0.12)};
        int[] ry1 = {0, 0, h, h};
        gRayos.fillPolygon(rx1, ry1, 4);

        int[] rx2 = {(int)(w * 0.60), (int)(w * 0.80), (int)(w * 0.70), (int)(w * 0.50)};
        int[] ry2 = {0, 0, h, h};
        gRayos.fillPolygon(rx2, ry2, 4);
        gRayos.dispose();

        // 8. Briznas de hierba silvestre pixel art
        g2.setColor(new Color(125, 215, 105, 200));
        int[] pastosX = {35, 110, 250, 340, 460, 590, 750, 840, 170, 510, 680, 880};
        int[] pastosY = {horizonte + 40, horizonte + 90, horizonte + 50, horizonte + 130, horizonte + 70, horizonte + 160, horizonte + 60, horizonte + 120, horizonte + 180, horizonte + 210, horizonte + 220, horizonte + 150};
        for (int i = 0; i < pastosX.length; i++) {
            int px = pastosX[i];
            int py = pastosY[i];
            g2.drawLine(px, py, px - 2, py - 6);
            g2.drawLine(px, py, px + 2, py - 6);
            g2.drawLine(px, py, px, py - 8);
        }

        // 9. Hongos del bosque (rojos con puntos blancos estilo pixel art como en imagen 5)
        dibujarHongo(g2, (int)(w * 0.08), horizonte + 52);
        dibujarHongo(g2, (int)(w * 0.33), horizonte + 35);
        dibujarHongo(g2, (int)(w * 0.56), horizonte + 40);
        dibujarHongo(g2, (int)(w * 0.80), horizonte + 42);

        // Florecillas silvestres del bosque
        g2.setColor(new Color(255, 235, 120));
        g2.fillOval((int)(w * 0.14), horizonte + 85, 5, 5);
        g2.fillOval((int)(w * 0.44), horizonte + 115, 5, 5);
        g2.fillOval((int)(w * 0.81), horizonte + 95, 5, 5);
        g2.setColor(new Color(255, 130, 130));
        g2.fillOval((int)(w * 0.37), horizonte + 68, 5, 5);
        g2.fillOval((int)(w * 0.62), horizonte + 145, 5, 5);
    }

    /**
     * Dibuja un pino detallado en pixel art con niveles de agujas escalonadas y sombreado 3 tonos.
     */
    private void dibujarPinoPixelArt(Graphics2D g2, int x, int y, int ancho, int alto) {
        // Tronco pixel art
        int troncoW = Math.max(6, ancho / 5);
        int troncoH = alto / 4;
        g2.setColor(new Color(65, 42, 28));
        g2.fillRect(x - troncoW / 2, y - troncoH, troncoW, troncoH);
        g2.setColor(new Color(42, 26, 16));
        g2.fillRect(x, y - troncoH, troncoW / 2, troncoH); // sombra tronco

        // 4 niveles de ramas de agujas en pixel art
        int niveles = 4;
        int pasoAlto = (int) (alto * 0.80) / niveles;

        Color cLuz = new Color(110, 215, 125);
        Color cMedio = new Color(38, 125, 75);
        Color cSombra = new Color(16, 52, 35);

        for (int i = 0; i < niveles; i++) {
            int nivelY = y - (int)(alto * 0.18) - (i * pasoAlto);
            int nivelW = (int) (ancho * (1.0 - (i * 0.18)));
            int nivelH = pasoAlto + 8;

            int escalones = 3;
            int hEscalon = nivelH / escalones;

            for (int s = 0; s < escalones; s++) {
                int escW = nivelW - (s * (nivelW / (escalones + 1)));
                int escY = nivelY - nivelH + (s * hEscalon);

                // Sombra inferior
                g2.setColor(cSombra);
                g2.fillRect(x - escW / 2, escY + hEscalon - 3, escW, 4);

                // Cuerpo verde
                g2.setColor(cMedio);
                g2.fillRect(x - escW / 2, escY, escW, hEscalon - 2);

                // Resplandor superior de aguja
                if (s == 0 || s == 1) {
                    g2.setColor(cLuz);
                    g2.fillRect(x - escW / 2 + 2, escY, (escW / 2) - 1, 3);
                }
            }
        }

        // Punta del pino
        g2.setColor(cLuz);
        g2.fillRect(x - 2, y - alto, 5, 6);
    }

    /**
     * Dibuja siluetas de pinos de fondo para las capas de niebla y profundidad.
     */
    private void dibujarPinoSilueta(Graphics2D g2, int x, int y, int ancho, int alto, Color color) {
        g2.setColor(color);
        int niveles = 4;
        int pasoAlto = alto / niveles;

        for (int i = 0; i < niveles; i++) {
            int nivelY = y - (i * pasoAlto);
            int nivelW = (int) (ancho * (1.0 - (i * 0.20)));
            int nivelH = pasoAlto + 6;

            int[] px = { x - nivelW / 2, x, x + nivelW / 2 };
            int[] py = { nivelY, nivelY - nivelH, nivelY };
            g2.fillPolygon(px, py, 3);
        }
    }

    /**
     * Dibuja arbustos redondeados pixel art (idénticos a la referencia de imagen 5).
     */
    private void dibujarArbustoPixelArt(Graphics2D g2, int x, int y, int radio) {
        // Base oscura
        g2.setColor(new Color(18, 58, 38));
        g2.fillOval(x - radio, y - radio, radio * 2, radio * 2);

        // Centro frondoso
        g2.setColor(new Color(48, 150, 78));
        g2.fillOval(x - radio + 2, y - radio + 2, (radio * 2) - 4, (radio * 2) - 6);

        // Brillo superior verde lima
        g2.setColor(new Color(125, 220, 135));
        g2.fillOval(x - radio / 2, y - radio + 2, radio, radio / 2);
    }

    private void dibujarHongo(Graphics2D g2, int x, int y) {
        // Tallo blanco
        g2.setColor(new Color(240, 240, 235));
        g2.fillRoundRect(x + 2, y + 4, 4, 6, 2, 2);

        // Sombrero rojo
        g2.setColor(new Color(225, 45, 45));
        g2.fillArc(x - 2, y, 12, 8, 0, 180);

        // Motas blancas pixel
        g2.setColor(Color.WHITE);
        g2.fillRect(x + 1, y + 2, 2, 2);
        g2.fillRect(x + 5, y + 1, 2, 2);
    }
}
