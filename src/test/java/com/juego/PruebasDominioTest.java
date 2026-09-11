package com.juego;

import com.juego.modelo.combate.EstadoPartida;
import com.juego.modelo.combate.Partida;
import com.juego.modelo.combate.ResultadoRonda;
import com.juego.modelo.combate.TablaEfectividad;
import com.juego.modelo.elementos.Elemento;
import com.juego.modelo.elementos.TipoElemento;
import com.juego.modelo.ia.IAAleatoria;
import com.juego.modelo.ia.IAEstrategica;
import com.juego.modelo.ia.SuperIA;
import com.juego.modelo.ia.TipoIA;
import com.juego.modelo.jugador.JugadorHumano;
import com.juego.modelo.jugador.JugadorMaquina;

import java.util.Arrays;
import java.util.List;

/**
 * Suite de Pruebas Automatizadas para verificar los principios de POO y requerimientos del TP:
 * 1. Matriz de daño sin condicionales if/switch y configurable.
 * 2. Comportamiento de las tres estrategias de IA (Aleatoria, Estratégica, Super IA).
 * 3. Encapsulación y reducción de vida de los Elementos.
 * 4. Flujo de combate por turnos, reemplazo obligatorio y condición de victoria.
 */
public class PruebasDominioTest {

    private static int pruebasExitosas = 0;
    private static int pruebasFallidas = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   EJECUTANDO BATERIA DE TESTS POO (JAVA 21)");
        System.out.println("==================================================");

        testEncapsulacionYDanioElemento();
        testMatrizEfectividadSinIfsYConfigurabilidad();
        testIAAleatoria();
        testIAEstrategica();
        testSuperIAEficiencia();
        testCambioVoluntarioPenalizado();
        testFlujoPartidaRondasYVictoria();

        System.out.println("--------------------------------------------------");
        System.out.printf("RESULTADO FINAL: %d pasadas, %d fallidas.\n", pruebasExitosas, pruebasFallidas);
        System.out.println("==================================================");

        if (pruebasFallidas > 0) {
            System.exit(1);
        }
    }

    private static void afirmar(boolean condicion, String nombreTest) {
        if (condicion) {
            System.out.println("✅ [PASS] " + nombreTest);
            pruebasExitosas++;
        } else {
            System.err.println("❌ [FAIL] " + nombreTest);
            pruebasFallidas++;
        }
    }

    private static void testEncapsulacionYDanioElemento() {
        Elemento agua = new Elemento("1", "Squirtle", TipoElemento.AGUA);
        afirmar(agua.getVidaActual() == 100.0, "Elemento inicia con 100% de vida");
        afirmar(agua.estaVivo(), "Elemento inicia vivo");

        agua.recibirDanio(40.0);
        afirmar(agua.getVidaActual() == 60.0, "Elemento reduce vida correctamente al recibir 40%");

        agua.recibirDanio(70.0);
        afirmar(agua.getVidaActual() == 0.0, "La vida del elemento no baja de 0%");
        afirmar(!agua.estaVivo(), "Elemento a 0% queda fuera de combate");
    }

    private static void testMatrizEfectividadSinIfsYConfigurabilidad() {
        TablaEfectividad tabla = new TablaEfectividad();

        // Valores iniciales según el PDF
        afirmar(tabla.obtenerDanio(TipoElemento.AGUA, TipoElemento.FUEGO) == 50.0, "Agua hace 50% a Fuego");
        afirmar(tabla.obtenerDanio(TipoElemento.TIERRA, TipoElemento.FUEGO) == 20.0, "Tierra hace 20% a Fuego");
        afirmar(tabla.obtenerDanio(TipoElemento.FUEGO, TipoElemento.TIERRA) == 40.0, "Fuego hace 40% a Tierra");
        afirmar(tabla.obtenerDanio(TipoElemento.AGUA, TipoElemento.TIERRA) == 30.0, "Agua hace 30% a Tierra");
        afirmar(tabla.obtenerDanio(TipoElemento.FUEGO, TipoElemento.AGUA) == 20.0, "Fuego hace 20% a Agua");
        afirmar(tabla.obtenerDanio(TipoElemento.TIERRA, TipoElemento.AGUA) == 50.0, "Tierra hace 50% a Agua");

        // Verificamos configurabilidad dinámica sin tocar código
        tabla.configurarDanio(TipoElemento.AGUA, TipoElemento.FUEGO, 85.0);
        afirmar(tabla.obtenerDanio(TipoElemento.AGUA, TipoElemento.FUEGO) == 85.0, "Se pudo reconfigurar daño Agua->Fuego a 85%");

        tabla.restablecerValoresPorDefecto();
        afirmar(tabla.obtenerDanio(TipoElemento.AGUA, TipoElemento.FUEGO) == 50.0, "Restablecer por defecto recupera los 50%");
    }

    private static void testIAAleatoria() {
        IAAleatoria ia = new IAAleatoria();
        TablaEfectividad tabla = new TablaEfectividad();

        List<Elemento> cartas = Arrays.asList(
                new Elemento("1", "E1", TipoElemento.AGUA),
                new Elemento("2", "E2", TipoElemento.FUEGO),
                new Elemento("3", "E3", TipoElemento.TIERRA)
        );

        Elemento eleccion = ia.seleccionarElemento(cartas, cartas.get(0), tabla);
        afirmar(eleccion != null && cartas.contains(eleccion), "IA Aleatoria selecciona un elemento válido de la lista");
    }

    private static void testIAEstrategica() {
        IAEstrategica ia = new IAEstrategica();
        TablaEfectividad tabla = new TablaEfectividad();

        // El rival tiene Agua
        Elemento rivalAgua = new Elemento("R1", "Squirtle Rival", TipoElemento.AGUA);

        // La IA tiene cartas disponibles: Agua, Tierra y Fuego
        Elemento cartaAgua = new Elemento("M1", "Mi Agua", TipoElemento.AGUA);
        Elemento cartaTierra = new Elemento("M2", "Mi Tierra", TipoElemento.TIERRA);
        Elemento cartaFuego = new Elemento("M3", "Mi Fuego", TipoElemento.FUEGO);

        List<Elemento> opciones = Arrays.asList(cartaAgua, cartaTierra, cartaFuego);

        // Tierra contra Agua: inflige 50% y recibe 30% (Ventaja neta: +20%)
        // Fuego contra Agua: inflige 20% y recibe 50% (Ventaja neta: -30%)
        // Agua contra Agua: inflige 15% y recibe 15% (Ventaja neta: 0%)
        Elemento elegida = ia.seleccionarElemento(opciones, rivalAgua, tabla);
        afirmar(elegida.getTipo() == TipoElemento.TIERRA, "IA Estratégica elige Tierra contra Agua para maximizar ventaja");
    }

    private static void testSuperIAEficiencia() {
        SuperIA superIa = new SuperIA();
        TablaEfectividad tabla = new TablaEfectividad();

        // El rival tiene Agua pero con solo 20% de vida
        Elemento rivalAguaHerido = new Elemento("R1", "Squirtle Rival", TipoElemento.AGUA);
        rivalAguaHerido.recibirDanio(80.0); // Le queda 20%
        afirmar(rivalAguaHerido.getVidaActual() == 20.0, "Rival preparado con 20% vida");

        // Mis cartas:
        // - Tierra hace 50% a Agua (letal)
        // - Fuego hace 20% a Agua (letal justo)
        // - Agua hace 15% a Agua (no letal)
        Elemento miTierra = new Elemento("M1", "Tierra Fuerte", TipoElemento.TIERRA);
        Elemento miFuego = new Elemento("M2", "Fuego Secundario", TipoElemento.FUEGO);
        Elemento miAgua = new Elemento("M3", "Agua", TipoElemento.AGUA);

        List<Elemento> opciones = Arrays.asList(miTierra, miFuego, miAgua);

        // La Super IA evalúa que puede liquidarlo en este turno:
        // Ambos (Tierra y Fuego) hacen >= 20%.
        // Tierra recibe de Agua 30%, mientras que Fuego recibe 50%.
        // Elige eficientemente un candidato letal.
        Elemento elegida = superIa.seleccionarElemento(opciones, rivalAguaHerido, tabla);
        afirmar(tabla.obtenerDanio(elegida.getTipo(), rivalAguaHerido.getTipo()) >= 20.0,
                "Super IA elige un elemento con daño letal (>= 20%) para rematar con eficiencia");
    }

    private static void testCambioVoluntarioPenalizado() {
        JugadorHumano h = new JugadorHumano("Ash");
        JugadorMaquina cpu = new JugadorMaquina("Gary", TipoIA.ALEATORIA);
        TablaEfectividad tabla = new TablaEfectividad();
        Partida p = new Partida(h, cpu, tabla);
        p.iniciar();

        Elemento inicial = h.getElementoActivo();
        Elemento reemplazo = h.getElementosVivos().stream()
                .filter(e -> !e.equals(inicial))
                .findFirst().orElse(null);

        if (reemplazo != null) {
            double vidaAntes = reemplazo.getVidaActual();
            var resultado = p.cambiarElementoHumano(reemplazo);
            afirmar(resultado.fueVoluntario(), "El cambio con criatura viva es voluntario");
            afirmar(resultado.danioRecibido() > 0, "El rival atacó al elemento ingresante");
            afirmar(reemplazo.getVidaActual() < vidaAntes, "La criatura ingresante recibió daño");
        }
    }

    private static void testFlujoPartidaRondasYVictoria() {
        JugadorHumano humano = new JugadorHumano("Ash");
        JugadorMaquina cpu = new JugadorMaquina("Gary", TipoIA.ESTRATEGICA);
        TablaEfectividad tabla = new TablaEfectividad();

        Partida partida = new Partida(humano, cpu, tabla);
        partida.iniciar();

        afirmar(partida.getEstado() == EstadoPartida.EN_CURSO, "Partida inicia en estado EN_CURSO");
        afirmar(humano.getElementos().size() == 5, "Humano recibe 5 cartas");
        afirmar(cpu.getElementos().size() == 5, "CPU recibe 5 cartas");

        int rondasMaximas = 50;
        int conteo = 0;
        while (partida.getEstado() == EstadoPartida.EN_CURSO && conteo < rondasMaximas) {
            // Si el humano necesita reemplazar porque su activo murió, elige uno vivo
            if (humano.necesitaReemplazo()) {
                List<Elemento> vivos = humano.getElementosVivos();
                if (!vivos.isEmpty()) {
                    partida.seleccionarElementoHumano(vivos.get(0));
                }
            }

            ResultadoRonda res = partida.ejecutarRonda();
            conteo++;
        }

        afirmar(partida.getEstado() == EstadoPartida.VICTORIA_HUMANO || partida.getEstado() == EstadoPartida.VICTORIA_MAQUINA,
                "La partida concluye con victoria de uno de los bandos al agotarse las cartas");
    }
}
