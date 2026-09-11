package com.juego;

import com.juego.controlador.ControladorJuego;
import com.juego.vista.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada principal de la aplicación.
 */
public class Main {
    public static void main(String[] args) {
        // Ejecución segura dentro del Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Look and feel nativo del sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            VentanaPrincipal ventana = new VentanaPrincipal();
            ControladorJuego controlador = new ControladorJuego();
            controlador.setVista(ventana);

            ventana.setVisible(true);
            com.juego.vista.Sonidos.reproducirMusicaMenu();
        });
    }
}
