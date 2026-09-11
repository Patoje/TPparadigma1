# Trabajo Práctico Integrador: Batalla Elemental (Agua, Tierra y Fuego)
**Materia:** Programación Orientada a Objetos  
**Carrera:** Ingeniería en Sistemas de Información  
**Lenguaje:** Java 21 (LTS)  
**Interfaz Gráfica:** Java Swing 2D (Estilo Batalla Pokémon Retro)

---

## 📌 Descripción del Proyecto

Videojuego por turnos basado en la mecánica de Piedra–Papel–Tijera utilizando elementos (**Agua 💧, Tierra 🌱 y Fuego 🔥**). Cada jugador (Humano vs Computadora) recibe al comenzar una dotación de 5 cartas/elementos aleatorios con 100% de energía (vida).

El juego incorpora una **interfaz gráfica 2D inspirada en los clásicos combates Pokémon**, con:
- Sprites pixel art frontales y traseros.
- Escenario de combate con plataformas elípticas y fondos degradados.
- Animaciones de sacudida (*shake*) ante impactos y barras de vida animadas.
- Consola de diálogo retro estilo RPG con relato de turnos.
- Banco inferior de 5 cartas interactivas con barras de vida en miniatura.
- Menú principal con selección de tipo de IA rival.
- Pantalla de configuración interactiva para modificar la matriz de efectividad de daño.

---

## 🏛️ Cumplimiento Estricto de las Directivas del TP (Cátedra)

1. **🚫 Prohibido resolver interacciones con múltiples `if` o `switch`:**
   * La efectividad y el porcentaje de daño se gestionan a través de la clase `TablaEfectividad` y la clave inmutable `ParTipos(TipoElemento atacante, TipoElemento defensor)`.
   * La consulta se resuelve en tiempo constante $O(1)$ sin recurrir a estructuras condicionales para determinar el daño entre tipos.

2. **🚫 Prohibido lógica centralizada en una sola clase (Clase Dios):**
   * El sistema está desacoplado mediante el patrón arquitectónico **MVC (Modelo - Vista - Controlador)**:
     * `com.juego.modelo`: Lógica pura de negocio y dominio (entidades, combate, jugadores, IAs). No posee ninguna dependencia hacia Swing ni bibliotecas de interfaz gráfica.
     * `com.juego.vista`: Componentes gráficos, ventanas, HUDs y canvas 2D.
     * `com.juego.controlador`: Orquesta la interacción entre las acciones del usuario y el modelo.

3. **🚫 Prohibido uso de estructuras sin encapsulación ni funciones globales:**
   * Todos los atributos son privados, con constructores que validan invariantes de estado y métodos de negocio encapsulados (`recibirDanio()`, `estaVivo()`, `necesitaReemplazo()`).

4. **Patrón Strategy para las distintas Inteligencias Artificiales:**
   * Se definió la interfaz `EstrategiaIA` con tres implementaciones intercambiables:
     * **`IAAleatoria`**: Elige al azar una de sus cartas disponibles.
     * **`IAEstrategica`**: Calcula la ventaja neta (daño infligido menos daño recibido) y selecciona la carta óptima.
     * **`SuperIA`**: Evalúa si la criatura rival puede ser derrotada en el turno (daño $\ge$ vida restante del rival) para liquidarla eficientemente, reservando recursos clave.

5. **Sistema de daño configurable:**
   * Accesible desde el Menú Principal mediante el botón **"Configurar Daños"**, permitiendo alterar los porcentajes de daño en caliente antes de iniciar cualquier partida y restaurar los valores por defecto según el PDF.

---

## 🚀 Cómo Ejecutar el Proyecto

### Opción A (Doble clic - Recomendada en Windows):
* Hacé doble clic en el archivo `ejecutar_juego.bat`.
* O ejecuta en la terminal:
  ```bash
  java -jar JuegoElemental.jar
  ```

### Opción B (Batería de Pruebas Unitarias):
* Hacé doble clic en el archivo `ejecutar_tests.bat`.
* Ejecuta 24 pruebas automatizadas que validan la reducción de vida, matriz de efectividad sin condicionales, comportamiento de las 3 IAs, penalizaciones de cambio y flujo de victoria.

---

## 📂 Estructura del Código

```
TPParadigmas/
├── JuegoElemental.jar                  # JAR ejecutable listo para correr
├── ejecutar_juego.bat                 # Lanzador de Windows
├── ejecutar_tests.bat                 # Ejecutor de pruebas automatizadas
├── src/
│   ├── main/
│   │   ├── java/com/juego/
│   │   │   ├── modelo/
│   │   │   │   ├── elementos/          # Elemento, TipoElemento, FabricaElementos
│   │   │   │   ├── combate/            # TablaEfectividad, ParTipos, Partida, EstadoPartida, ResultadoRonda
│   │   │   │   ├── ia/                 # EstrategiaIA, IAAleatoria, IAEstrategica, SuperIA, TipoIA
│   │   │   │   └── jugador/            # Jugador, JugadorHumano, JugadorMaquina
│   │   │   ├── controlador/            # ControladorJuego
│   │   │   ├── vista/                  # VentanaPrincipal, BatallaPanel, CartasReservaPanel, DialogoCombatePanel, etc.
│   │   │   └── Main.java               # Punto de entrada
│   │   └── resources/sprites/          # Sprites pixel art (frente y espalda)
│   └── test/java/com/juego/
│       ├── PruebasDominioTest.java     # Suite de 24 tests unitarios
│       └── VerificacionVisualTest.java # Smoke test de la interfaz
```
