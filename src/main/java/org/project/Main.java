package org.project;

import org.project.game_objects.AIPlayer;
import org.project.game_objects.Movimiento;
import org.project.game_objects.Tabla;

import java.util.List;
import java.util.Scanner;

/**
 * Clase principal que contiene el bucle del juego y gestiona la interacción con el usuario.
 */
public class Main {

    public static void main(String[] args) {
        iniciarJuego();
    }

    /**
     * Configura e inicia una nueva partida de damas.
     * Permite al usuario elegir entre jugar contra otro humano o contra la IA.
     */
    public static void iniciarJuego() {
        System.out.println("Bienvenido al proyecto de damas, un trabajo para el curso de Programación Paralela.");
        Scanner sc = new Scanner(System.in);

        boolean jugarContraIA = preguntarModoDeJuego(sc);

        if (jugarContraIA) {
            boolean jugadorEsBlanco = preguntarColor(sc);
            boolean aiEsParalelo = preguntarModoIA(sc);
            jugarHumanoVsIA(sc, jugadorEsBlanco, aiEsParalelo);
        } else {
            jugarHumanoVsHumano(sc);
        }
    }

    private static boolean preguntarModoDeJuego(Scanner sc) {
        while (true) {
            System.out.print("¿Desea jugar contra la IA? (S/N): ");
            String respuesta = sc.next().toLowerCase();
            if (respuesta.equals("s")) return true;
            if (respuesta.equals("n")) return false;
            System.out.println("Entrada inválida. Por favor ingrese S para sí o N para no.");
        }
    }

    private static boolean preguntarColor(Scanner sc) {
        while (true) {
            System.out.print("Ingrese el color con el que desea jugar (B para Blancas / N para Negras): ");
            char color = sc.next().toLowerCase().charAt(0);
            if (color == 'b') return true;
            if (color == 'n') return false;
            System.out.println("Entrada inválida. Por favor ingrese B o N.");
        }
    }

    private static boolean preguntarModoIA(Scanner sc) {
        while (true) {
            System.out.print("¿Desea que la IA use procesamiento paralelo o secuencial? (P/S): ");
            String respuesta = sc.next().toLowerCase();
            if (respuesta.equals("p")) return true;
            if (respuesta.equals("s")) return false;
            System.out.println("Entrada inválida. Por favor ingrese P para paralelo o S para secuencial.");
        }
    }

    /**
     * Gestiona el bucle de juego para una partida de Humano vs. IA.
     *
     * @param sc             El objeto Scanner para la entrada del usuario.
     * @param jugadorEsBlanco True si el jugador humano eligió las piezas blancas.
     * @param aiEsParalelo   True si la IA debe usar el modo de procesamiento paralelo.
     */
    public static void jugarHumanoVsIA(Scanner sc, boolean jugadorEsBlanco, boolean aiEsParalelo) {
        AIPlayer ia = new AIPlayer(!jugadorEsBlanco, 6);
        Tabla tabla = new Tabla();
        imprimirBannerJuego(true, jugadorEsBlanco);

        boolean turnoDeBlancas = true;

        while (!tabla.haTerminado()) {
            System.out.println(tabla);
            boolean esTurnoHumano = (turnoDeBlancas == jugadorEsBlanco);

            boolean seEjecutoCaptura = false;
            if (esTurnoHumano) {
                seEjecutoCaptura = gestionTurnoHumano(sc, tabla, turnoDeBlancas);
            } else {
                seEjecutoCaptura = gestionTurnoIA(ia, tabla, turnoDeBlancas, aiEsParalelo);
            }

            // Solo cambia de turno si no hay capturas adicionales disponibles después de una captura
            if (seEjecutoCaptura && nohayCapturasAdicionales(tabla, turnoDeBlancas)) {
                turnoDeBlancas = !turnoDeBlancas;
            } else if (!seEjecutoCaptura) {
                turnoDeBlancas = !turnoDeBlancas;
            }
        }

        finalizarPartida(tabla);
    }

    /**
     * Gestiona el bucle de juego para una partida de Humano vs. Humano.
     *
     * @param sc El objeto Scanner para la entrada del usuario.
     */
    public static void jugarHumanoVsHumano(Scanner sc) {
        Tabla tabla = new Tabla();
        imprimirBannerJuego(false, true);
        boolean turnoDeBlancas = true;

        while (!tabla.haTerminado()) {
            System.out.println(tabla);
            boolean seEjecutoCaptura = gestionTurnoHumano(sc, tabla, turnoDeBlancas);

            // Solo cambia de turno si no hay capturas adicionales disponibles después de una captura
            if (seEjecutoCaptura && nohayCapturasAdicionales(tabla, turnoDeBlancas)) {
                turnoDeBlancas = !turnoDeBlancas;
            } else if (!seEjecutoCaptura) {
                turnoDeBlancas = !turnoDeBlancas;
            }
        }
        finalizarPartida(tabla);
    }

    private static boolean gestionTurnoHumano(Scanner sc, Tabla tabla, boolean esTurnoDeBlancas)
    {
        List<Movimiento> movimientosDisponibles = tabla.getMovimientosDisponibles(esTurnoDeBlancas);
        if (movimientosDisponibles.isEmpty()) return false;

        mostrarCapturasObligatorias(movimientosDisponibles);

        // Bucle hasta que se ingrese un movimiento válido
        while (true) {
            System.out.print("\nTurno de " + (esTurnoDeBlancas ? "BLANCAS (●/◆)" : "NEGRAS (○/◇)") + ". Ingrese su movimiento: ");

            try {
                String input = sc.next().toLowerCase();
                Movimiento movimiento = new Movimiento(input);

                if (esMovimientoValidoEnLista(movimiento, movimientosDisponibles)) {
                    boolean esCaptura = movimiento.esCaptura();
                    tabla.ejecutarMovimiento(movimiento, esTurnoDeBlancas);
                    System.out.println("Movimiento ejecutado: " + movimiento);
                    return esCaptura;
                } else {
                    System.out.println("¡Movimiento inválido! Inténtelo de nuevo.");
                    mostrarMovimientosDisponibles(movimientosDisponibles);
                    // Continúa el bucle para pedir otro movimiento
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Formato de movimiento inválido. Use el formato: a1-b2");
                // Continúa el bucle para pedir otro movimiento
            }
        }
    }

    private static boolean gestionTurnoIA(AIPlayer ia, Tabla tabla, boolean esTurnoDeBlancas, boolean esParalelo) {
        System.out.println("\nTurno de la IA (" + (esTurnoDeBlancas ? "BLANCAS (●/◆)" : "NEGRAS (○/◇)") + ")...");
        System.out.println("La IA está pensando (usando procesamiento " + (esParalelo ? "paralelo" : "secuencial") + ")...");

        long startTime = System.currentTimeMillis();
        Movimiento aiMove = esParalelo ? ia.getBestMove(tabla) : ia.getBestMoveSequential(tabla);
        long endTime = System.currentTimeMillis();

        if (aiMove != null) {
            boolean esCaptura = aiMove.esCaptura();
            tabla.ejecutarMovimiento(aiMove, esTurnoDeBlancas);
            System.out.println("La IA mueve: " + aiMove + " (Tiempo: " + (endTime - startTime) + "ms)");
            return esCaptura;
        } else {
            System.out.println("La IA no tiene movimientos disponibles.");
            return false;
        }
    }

    private static boolean nohayCapturasAdicionales(Tabla tabla, boolean esTurnoDeBlancas) {
        List<Movimiento> movimientos = tabla.getMovimientosDisponibles(esTurnoDeBlancas);
        // Si el primer movimiento disponible es una captura, significa que hay más capturas obligatorias.
        if (!movimientos.isEmpty() && movimientos.getFirst().esCaptura()) {
            System.out.println("¡Puedes hacer capturas adicionales con la misma pieza!");
            mostrarMovimientosDisponibles(movimientos);
            return false;
        }
        return true;
    }

    private static void finalizarPartida(Tabla tabla) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(tabla);
        System.out.println("=".repeat(50));
        System.out.println("¡JUEGO TERMINADO!");
        String ganador = tabla.getGanador();
        if (ganador != null) {
            System.out.println("🏆 GANADOR: " + ganador + " 🏆");
        }
    }

    // --- Métodos de ayuda para la interfaz de usuario ---

    private static void imprimirBannerJuego(boolean contraIA, boolean jugadorEsBlanco) {
        System.out.println("\n¡Qué comience el juego!");
        System.out.println("Formato de movimiento: a1-b2");
        System.out.println("Reglas: Las capturas son obligatorias.");
        if (contraIA) {
            System.out.println("Usted juega como: " + (jugadorEsBlanco ? "BLANCAS (●/◆)" : "NEGRAS (○/◇)"));
            System.out.println("La IA juega como: " + (!jugadorEsBlanco ? "BLANCAS (●/◆)" : "NEGRAS (○/◇)"));
        }
        System.out.println();
    }

    private static void mostrarCapturasObligatorias(List<Movimiento> movimientos) {
        if (!movimientos.isEmpty() && movimientos.getFirst().esCaptura()) {
            System.out.println("¡ATENCIÓN! Tienes movimientos de captura obligatorios:");
            mostrarMovimientosDisponibles(movimientos);
        }
    }

    private static void mostrarMovimientosDisponibles(List<Movimiento> movimientos) {
        for (int i = 0; i < Math.min(movimientos.size(), 10); i++) {
            System.out.println("- " + movimientos.get(i));
        }
        if (movimientos.size() > 10) {
            System.out.println("  ... y " + (movimientos.size() - 10) + " más.");
        }
    }

    private static boolean esMovimientoValidoEnLista(Movimiento mov, List<Movimiento> lista) {
        for (Movimiento valido : lista) {
            if (valido.filaOrigen == mov.filaOrigen && valido.columnaOrigen == mov.columnaOrigen &&
                valido.filaDestino == mov.filaDestino && valido.columnaDestino == mov.columnaDestino) {
                return true;
            }
        }
        return false;
    }
}
