package com.juego.vista;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sistema de sonido procedural y reproductor de bandas sonoras WAV en loop.
 * - Soporta archivos de audio dedicados (.wav) para Menú, Combate, Victoria y Derrota.
 * - Control de volumen global dinámico (0% a 100%) con inicio calibrado al 30%.
 * - Bucle continuo sin interrupciones y fallback procedural en caso de ausencia de archivo.
 */
public class Sonidos {

    private static final int SAMPLE_RATE = 22050;

    private static final ExecutorService POOL_SFX = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "ChiptuneSFXThread");
        t.setDaemon(true);
        return t;
    });

    private static volatile boolean habilitado = true;
    // Volumen global inicial al 30% (suave y agradable)
    private static volatile float volumenGlobal = 0.30f;

    // Estado de pista actual
    public static final int PISTA_SILENCIO = 0;
    public static final int PISTA_MENU = 1;
    public static final int PISTA_COMBATE = 2;
    public static final int PISTA_VICTORIA = 3;
    public static final int PISTA_DERROTA = 4;

    private static volatile int pistaActual = PISTA_SILENCIO;
    private static Thread hiloMusica = null;
    private static final Object lockMusica = new Object();

    public static void setHabilitado(boolean activo) {
        habilitado = activo;
        if (!activo) {
            detenerMusica();
        }
    }

    public static boolean isHabilitado() {
        return habilitado;
    }

    public static void setVolumenGlobal(float vol) {
        volumenGlobal = Math.max(0.0f, Math.min(1.0f, vol));
    }

    public static float getVolumenGlobal() {
        return volumenGlobal;
    }

    // =========================================================================
    // EFECTOS DE SONIDO (SFX) - Con volumen atenuado y vinculado al volumen global
    // =========================================================================

    public static void reproducirClick() {
        if (!habilitado || volumenGlobal <= 0.001f) return;
        POOL_SFX.execute(() -> {
            byte[] buffer = generarTono(780, 35, 0.12 * volumenGlobal, TipoOnda.CUADRADA);
            reproducirBufferSFX(buffer);
        });
    }

    public static void reproducirGolpe() {
        if (!habilitado || volumenGlobal <= 0.001f) return;
        POOL_SFX.execute(() -> {
            int duracionMs = 150;
            int totalMuestras = (int) (SAMPLE_RATE * (duracionMs / 1000.0));
            byte[] buffer = new byte[totalMuestras];

            for (int i = 0; i < totalMuestras; i++) {
                double progreso = (double) i / totalMuestras;
                double frec = 380.0 - (320.0 * Math.pow(progreso, 0.6));
                double envolvente = Math.pow(1.0 - progreso, 1.8);

                double t = (double) i / SAMPLE_RATE;
                double onda = Math.sin(2.0 * Math.PI * frec * t) > 0 ? 1.0 : -1.0;
                double ruido = (Math.random() * 2.0 - 1.0) * 0.30;
                double muestra = (onda * 0.70 + ruido) * envolvente * 0.25 * volumenGlobal;

                buffer[i] = (byte) Math.max(-128, Math.min(127, (int) (muestra * 127.0)));
            }

            reproducirBufferSFX(buffer);
        });
    }

    public static void reproducirCambio() {
        if (!habilitado || volumenGlobal <= 0.001f) return;
        POOL_SFX.execute(() -> {
            byte[] t1 = generarTono(440, 50, 0.20 * volumenGlobal, TipoOnda.CUADRADA);
            byte[] t2 = generarTono(660, 75, 0.22 * volumenGlobal, TipoOnda.CUADRADA);
            byte[] buffer = new byte[t1.length + t2.length];
            System.arraycopy(t1, 0, buffer, 0, t1.length);
            System.arraycopy(t2, 0, buffer, t1.length, t2.length);
            reproducirBufferSFX(buffer);
        });
    }

    // =========================================================================
    // CONTROL DE MÚSICA DE FONDO (BGM)
    // =========================================================================

    public static void reproducirMusicaMenu() {
        cambiarPista(PISTA_MENU, "menu.wav");
    }

    public static void reproducirMusicaCombate() {
        cambiarPista(PISTA_COMBATE, "batalla.wav");
    }

    public static void reproducirMusicaVictoria() {
        cambiarPista(PISTA_VICTORIA, "victoria.wav");
    }

    public static void reproducirMusicaDerrota() {
        cambiarPista(PISTA_DERROTA, "derrota.wav");
    }

    public static void detenerMusica() {
        cambiarPista(PISTA_SILENCIO, null);
    }

    private static void cambiarPista(int nuevaPista, String nombreArchivo) {
        if (!habilitado && nuevaPista != PISTA_SILENCIO) return;

        synchronized (lockMusica) {
            if (pistaActual == nuevaPista) return;
            pistaActual = nuevaPista;

            if (hiloMusica != null && hiloMusica.isAlive()) {
                hiloMusica.interrupt();
            }

            if (nuevaPista != PISTA_SILENCIO) {
                hiloMusica = new Thread(() -> bucleReproductorWAV(nuevaPista, nombreArchivo), "BGMStreamPlayer");
                hiloMusica.setDaemon(true);
                hiloMusica.start();
            }
        }
    }

    /**
     * Reproduce un archivo WAV en bucle continuo y sin interrupciones con ajuste dinámico de volumen.
     */
    private static void bucleReproductorWAV(int pista, String nombreArchivo) {
        AudioInputStream primerStream = abrirStreamWav(nombreArchivo);
        if (primerStream == null) {
            bucleMusicaProcedural(pista);
            return;
        }

        try {
            AudioFormat baseFormat = primerStream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            try (SourceDataLine line = AudioSystem.getSourceDataLine(decodedFormat)) {
                line.open(decodedFormat, 16384);
                line.start();
                byte[] buffer = new byte[4096];

                AudioInputStream streamActual = primerStream;
                while (pistaActual == pista && !Thread.currentThread().isInterrupted()) {
                    if (streamActual == null) {
                        streamActual = abrirStreamWav(nombreArchivo);
                        if (streamActual == null) break;
                    }

                    try (AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, streamActual)) {
                        int bytesRead;
                        while (pistaActual == pista && !Thread.currentThread().isInterrupted() &&
                                (bytesRead = decodedStream.read(buffer, 0, buffer.length)) != -1) {

                            float vol = volumenGlobal;
                            for (int i = 0; i < bytesRead - 1; i += 2) {
                                short sample = (short) (((buffer[i + 1] & 0xFF) << 8) | (buffer[i] & 0xFF));
                                short scaled = (short) Math.max(-32768, Math.min(32767, (int) (sample * vol)));
                                buffer[i] = (byte) (scaled & 0xFF);
                                buffer[i + 1] = (byte) ((scaled >> 8) & 0xFF);
                            }

                            line.write(buffer, 0, bytesRead);
                        }
                    } catch (Exception ignored) {
                    } finally {
                        try {
                            if (streamActual != null) streamActual.close();
                        } catch (Exception ignored) {}
                        streamActual = null;
                    }
                }

                line.stop();
            }
        } catch (Exception e) {
            bucleMusicaProcedural(pista);
        }
    }

    private static AudioInputStream abrirStreamWav(String nombreArchivo) {
        if (nombreArchivo == null) return null;

        String[] posiblesRutas = {
                "audio/" + nombreArchivo,
                "src/main/resources/audio/" + nombreArchivo,
                "resources/audio/" + nombreArchivo,
                nombreArchivo
        };

        for (String ruta : posiblesRutas) {
            File f = new File(ruta);
            if (f.exists() && f.isFile()) {
                try {
                    return AudioSystem.getAudioInputStream(f);
                } catch (Exception ignored) {}
            }
        }

        // Búsqueda en recursos del Classpath / JAR
        try {
            InputStream is = Sonidos.class.getResourceAsStream("/audio/" + nombreArchivo);
            if (is != null) {
                return AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            }
        } catch (Exception ignored) {}

        return null;
    }

    // =========================================================================
    // FALLBACK PROCEDURAL (Por si faltan archivos WAV)
    // =========================================================================

    private static void bucleMusicaProcedural(int pista) {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format, 4096);
            line.start();

            while (pistaActual == pista && !Thread.currentThread().isInterrupted()) {
                switch (pista) {
                    case PISTA_MENU -> {
                        int[] notas = {1046, 784, 988, 740};
                        for (int n : notas) {
                            if (pistaActual != pista) return;
                            emitirNotaDoble(line, n, 131, 180, 0.08 * volumenGlobal, 0.05 * volumenGlobal, TipoOnda.CUADRADA, TipoOnda.TRIANGULAR);
                        }
                    }
                    case PISTA_COMBATE -> {
                        int[] notas = {440, 523, 659, 784, 659, 587, 523, 494};
                        for (int n : notas) {
                            if (pistaActual != pista) return;
                            emitirNotaDoble(line, n, 110, 130, 0.09 * volumenGlobal, 0.06 * volumenGlobal, TipoOnda.CUADRADA, TipoOnda.CUADRADA);
                        }
                    }
                    case PISTA_VICTORIA -> {
                        int[] notas = {523, 659, 784, 1046, 880, 1046, 784};
                        for (int n : notas) {
                            if (pistaActual != pista) return;
                            emitirNotaDoble(line, n, 131, 140, 0.10 * volumenGlobal, 0.07 * volumenGlobal, TipoOnda.CUADRADA, TipoOnda.TRIANGULAR);
                        }
                    }
                    case PISTA_DERROTA -> {
                        int[] notas = {392, 370, 349, 311, 294, 261};
                        for (int n : notas) {
                            if (pistaActual != pista) return;
                            emitirNotaDoble(line, n, 98, 300, 0.09 * volumenGlobal, 0.06 * volumenGlobal, TipoOnda.TRIANGULAR, TipoOnda.SENOIDAL);
                        }
                    }
                    default -> { return; }
                }
            }

            line.drain();
            line.stop();
        } catch (Exception ignored) {}
    }

    private enum TipoOnda { CUADRADA, TRIANGULAR, SENOIDAL }

    private static void emitirNotaDoble(
            SourceDataLine line,
            double frecMelodia,
            double frecBajo,
            int duracionMs,
            double volMelodia,
            double volBajo,
            TipoOnda ondaMelodia,
            TipoOnda ondaBajo
    ) {
        int muestras = (int) (SAMPLE_RATE * (duracionMs / 1000.0));
        byte[] buf = new byte[muestras];

        for (int i = 0; i < muestras; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progreso = (double) i / muestras;
            double env = Math.min(1.0, (1.0 - progreso) * 2.0);

            double mMel = sampleOnda(frecMelodia, t, ondaMelodia) * volMelodia;
            double mBaj = frecBajo > 0 ? sampleOnda(frecBajo, t, ondaBajo) * volBajo : 0;
            double total = (mMel + mBaj) * env;

            buf[i] = (byte) Math.max(-128, Math.min(127, (int) (total * 127.0)));
        }

        line.write(buf, 0, buf.length);
    }

    private static double sampleOnda(double frec, double t, TipoOnda tipo) {
        if (frec <= 0) return 0.0;
        double fase = (frec * t) % 1.0;
        return switch (tipo) {
            case CUADRADA -> fase < 0.5 ? 1.0 : -1.0;
            case TRIANGULAR -> 4.0 * Math.abs(fase - 0.5) - 1.0;
            default -> Math.sin(2.0 * Math.PI * fase);
        };
    }

    private static byte[] generarTono(double frecuencia, int duracionMs, double volumen, TipoOnda tipo) {
        int totalMuestras = (int) (SAMPLE_RATE * (duracionMs / 1000.0));
        byte[] buffer = new byte[totalMuestras];

        for (int i = 0; i < totalMuestras; i++) {
            double progreso = (double) i / totalMuestras;
            double envolvente = Math.min(1.0, (1.0 - progreso) * 1.6);
            double t = (double) i / SAMPLE_RATE;
            double muestra = sampleOnda(frecuencia, t, tipo) * volumen * envolvente;
            buffer[i] = (byte) Math.max(-128, Math.min(127, (int) (muestra * 127.0)));
        }

        return buffer;
    }

    private static void reproducirBufferSFX(byte[] buffer) {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                line.open(format);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
            }
        } catch (Exception ignored) {}
    }
}
