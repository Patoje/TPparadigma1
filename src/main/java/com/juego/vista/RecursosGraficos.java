package com.juego.vista;

import com.juego.modelo.elementos.TipoElemento;
import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * Gestor centralizado de recursos gráficos (sprites, íconos, fuentes).
 * Carga los sprites de Pokémon y escala el pixel art conservando nitidez.
 */
public class RecursosGraficos {
    private static final Map<TipoElemento, BufferedImage> SPRITES_FRENTE = new EnumMap<>(TipoElemento.class);
    private static final Map<TipoElemento, BufferedImage> SPRITES_ESPALDA = new EnumMap<>(TipoElemento.class);

    static {
        cargarSprites();
    }

    private static void cargarSprites() {
        for (TipoElemento tipo : TipoElemento.values()) {
            SPRITES_FRENTE.put(tipo, cargarImagen(tipo.getSpriteFrente(), tipo.name() + " Frente"));
            SPRITES_ESPALDA.put(tipo, cargarImagen(tipo.getSpriteEspalda(), tipo.name() + " Espalda"));
        }
    }

    private static BufferedImage cargarImagen(String path, String nombreFallback) {
        try {
            // Intento 1: Como recurso del classpath
            InputStream is = RecursosGraficos.class.getResourceAsStream(path);
            if (is != null) {
                return ImageIO.read(is);
            }

            // Intento 2: Como archivo directo en disco
            File f = new File("src/main/resources" + path);
            if (f.exists()) {
                return ImageIO.read(f);
            }

            File fAlt = new File("." + path);
            if (fAlt.exists()) {
                return ImageIO.read(fAlt);
            }
        } catch (Exception e) {
            System.err.println("Aviso: No se pudo cargar la imagen " + path + ": " + e.getMessage());
        }

        // Fallback: Genera un gráfico básico con el color del tipo
        return generarSpriteFallback(nombreFallback);
    }

    private static BufferedImage generarSpriteFallback(String nombre) {
        BufferedImage img = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(60, 90, 150));
        g.fillOval(8, 8, 80, 80);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString(nombre, 12, 50);
        g.dispose();
        return img;
    }

    public static BufferedImage getSpriteFrente(TipoElemento tipo) {
        return SPRITES_FRENTE.get(tipo);
    }

    public static BufferedImage getSpriteEspalda(TipoElemento tipo) {
        return SPRITES_ESPALDA.get(tipo);
    }

    /**
     * Escala un BufferedImage manteniendo el estilo Pixel Art nítido.
     */
    public static BufferedImage escalarPixelArt(BufferedImage original, int nuevoAncho, int nuevoAlto) {
        if (original == null) return null;
        BufferedImage escalada = new BufferedImage(nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = escalada.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(original, 0, 0, nuevoAncho, nuevoAlto, null);
        g2d.dispose();
        return escalada;
    }
}
