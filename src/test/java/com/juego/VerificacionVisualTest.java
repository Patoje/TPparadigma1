package com.juego;

import com.juego.controlador.ControladorJuego;
import com.juego.modelo.ia.TipoIA;
import com.juego.vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

/**
 * Prueba de humo visual para verificar que la interfaz y componentes
 * se inicialicen y procesen eventos sin excepciones.
 */
public class VerificacionVisualTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                VentanaPrincipal ventana = new VentanaPrincipal();
                ControladorJuego controlador = new ControladorJuego();
                controlador.setVista(ventana);

                // 1. Verificar cambio a pantalla de configuración
                ventana.mostrarPantalla(VentanaPrincipal.PANTALLA_CONFIG);

                // 2. Iniciar una partida con Super IA
                controlador.iniciarPartida(TipoIA.SUPER_IA);

                // 3. Simular una ronda de ataque
                controlador.ejecutarAtaqueRonda();

                System.out.println("✅ [SMOKE TEST] La ventana, sprites y controlador operan correctamente.");
                ventana.dispose();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
