package org.project.game_objects;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Representa al jugador de inteligencia artificial (IA).
 * Utiliza el algoritmo Minimax con poda Alfa-Beta para determinar el mejor movimiento.
 */
public class AIPlayer {
    // Constantes para la evaluación del tablero
    private static final int VALOR_REY = 30;
    private static final int VALOR_PIEZA = 10;
    private static final int PUNTUACION_VICTORIA = 10000;
    private static final int BONIFICACION_CENTRO = 5;
    private static final int BONIFICACION_MOVILIDAD = 2;
    /**
     * El color de las piezas que controla la IA (true si son blancas, false si son negras).
     */
    private final boolean esBlanca;
    /**
     * La profundidad máxima de búsqueda en el árbol de Minimax.
     * Un valor más alto implica una IA más fuerte pero más lenta.
     */
    private final int profundidadMaxima;

    /**
     * Construye un nuevo jugador de IA.
     *
     * @param esBlanca          El color de las piezas de la IA.
     * @param profundidadMaxima La profundidad de búsqueda del algoritmo Minimax.
     */
    public AIPlayer(boolean esBlanca, int profundidadMaxima) {
        this.esBlanca = esBlanca;
        this.profundidadMaxima = profundidadMaxima;
    }

    /**
     * Encuentra el mejor movimiento posible utilizando procesamiento paralelo.
     *
     * @param tabla El estado actual del tablero.
     * @return El mejor movimiento encontrado.
     */
    public Movimiento getBestMove(Tabla tabla) {
        // Validación de entrada
        if (tabla == null) {
            throw new IllegalArgumentException("El tablero no puede ser null");
        }
        
        List<Movimiento> movimientosDisponibles = tabla.getMovimientosDisponibles(esBlanca);

        if (movimientosDisponibles.isEmpty()) {
            return null;
        }
        if (movimientosDisponibles.size() == 1) {
            return movimientosDisponibles.getFirst();
        }

        AtomicReference<Movimiento> mejorMovimiento = new AtomicReference<>(movimientosDisponibles.getFirst());
        AtomicInteger mejorPuntuacion = new AtomicInteger(Integer.MIN_VALUE);

        // Evalúa los movimientos en paralelo para acelerar la búsqueda
        movimientosDisponibles.parallelStream().forEach(movimiento -> {
            Tabla tableroSimulado = new Tabla(tabla);
            tableroSimulado.ejecutarMovimiento(movimiento, esBlanca);

            int puntuacion = minimax(tableroSimulado, profundidadMaxima - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            // Actualización atómica del mejor movimiento encontrado usando compareAndSet
            // para evitar condiciones de carrera
            int puntuacionActual = mejorPuntuacion.get();
            while (puntuacion > puntuacionActual) {
                if (mejorPuntuacion.compareAndSet(puntuacionActual, puntuacion)) {
                    mejorMovimiento.set(movimiento);
                    break;
                }
                puntuacionActual = mejorPuntuacion.get();
            }
        });

        return mejorMovimiento.get();
    }

    /**
     * Encuentra el mejor movimiento posible utilizando procesamiento secuencial.
     *
     * @param tabla El estado actual del tablero.
     * @return El mejor movimiento encontrado.
     */
    public Movimiento getBestMoveSequential(Tabla tabla) {
        // Validación de entrada
        if (tabla == null) {
            throw new IllegalArgumentException("El tablero no puede ser null");
        }
        
        List<Movimiento> movimientosDisponibles = tabla.getMovimientosDisponibles(esBlanca);

        if (movimientosDisponibles.isEmpty()) {
            return null;
        }
        if (movimientosDisponibles.size() == 1) {
            return movimientosDisponibles.getFirst();
        }

        Movimiento mejorMovimiento = movimientosDisponibles.getFirst();
        int mejorPuntuacion = Integer.MIN_VALUE;

        // Evalúa los movimientos de forma secuencial
        for (Movimiento movimiento : movimientosDisponibles) {
            Tabla tableroSimulado = new Tabla(tabla);
            tableroSimulado.ejecutarMovimiento(movimiento, esBlanca);

            int puntuacion = minimax(tableroSimulado, profundidadMaxima - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            if (puntuacion > mejorPuntuacion) {
                mejorPuntuacion = puntuacion;
                mejorMovimiento = movimiento;
            }
        }

        return mejorMovimiento;
    }


    /**
     * Implementación del algoritmo Minimax con poda Alfa-Beta.
     *
     * @param tabla             El estado del tablero a evaluar.
     * @param profundidad       La profundidad restante de búsqueda.
     * @param alpha             El valor alfa para la poda.
     * @param beta              El valor beta para la poda.
     * @param esJugadorMaximizador True si el jugador actual busca maximizar la puntuación.
     * @return La puntuación evaluada para el estado del tablero.
     */
    private int minimax(Tabla tabla, int profundidad, int alpha, int beta, boolean esJugadorMaximizador) {
        if (profundidad == 0 || tabla.haTerminado()) {
            return evaluarTablero(tabla);
        }

        boolean turnoActual = esJugadorMaximizador == esBlanca;
        List<Movimiento> movimientos = tabla.getMovimientosDisponibles(turnoActual);

        if (esJugadorMaximizador) {
            int maxEval = Integer.MIN_VALUE;
            for (Movimiento movimiento : movimientos) {
                Tabla tableroSimulado = new Tabla(tabla);
                tableroSimulado.ejecutarMovimiento(movimiento, turnoActual);
                int eval = minimax(tableroSimulado, profundidad - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Poda beta
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Movimiento movimiento : movimientos) {
                Tabla tableroSimulado = new Tabla(tabla);
                tableroSimulado.ejecutarMovimiento(movimiento, turnoActual);
                int eval = minimax(tableroSimulado, profundidad - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Poda alfa
                }
            }
            return minEval;
        }
    }

    /**
     * Evalúa la puntuación de un estado del tablero desde la perspectiva de la IA.
     *
     * @param tabla El tablero a evaluar.
     * @return La puntuación calculada.
     */
    private int evaluarTablero(Tabla tabla) {
        if (tabla.haTerminado()) {
            String ganador = tabla.getGanador();
            if (ganador == null) return 0;
            boolean ganoBlanco = ganador.equals("Blancas");
            return (ganoBlanco == esBlanca) ? PUNTUACION_VICTORIA : -PUNTUACION_VICTORIA;
        }

        return evaluarMaterial(tabla) + evaluarPosicion(tabla) + evaluarMovilidad(tabla) + evaluarEstructura(tabla);
    }
    
    /**
     * Evalúa el material del tablero (piezas y reyes).
     *
     * @param tabla El tablero a evaluar.
     * @return La puntuación del material.
     */
    private int evaluarMaterial(Tabla tabla) {
        int puntuacion = 0;
        Casillero[][] tablero = tabla.getTablero();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tablero[i][j].tienePieza()) {
                    Pieza pieza = tablero[i][j].getPieza();
                    int valorPieza = pieza.esRey() ? VALOR_REY : VALOR_PIEZA;

                    if (pieza.esBlanca() == esBlanca) {
                        puntuacion += valorPieza;
                    } else {
                        puntuacion -= valorPieza;
                    }
                }
            }
        }
        return puntuacion;
    }
    
    /**
     * Evalúa la posición de las piezas en el tablero.
     *
     * @param tabla El tablero a evaluar.
     * @return La puntuación posicional.
     */
    private int evaluarPosicion(Tabla tabla) {
        int puntuacion = 0;
        Casillero[][] tablero = tabla.getTablero();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tablero[i][j].tienePieza()) {
                    Pieza pieza = tablero[i][j].getPieza();
                    
                    // Bonificación por posición para incentivar el avance
                    int bonificacionPosicion = 0;
                    if (pieza.esBlanca()) {
                        bonificacionPosicion = 7 - i; // Más cerca de la coronación
                    } else {
                        bonificacionPosicion = i; // Más cerca de la coronación
                    }
                    
                    // Bonificación por control del centro
                    int bonificacionCentro = 0;
                    if ((i >= 3 && i <= 4) && (j >= 3 && j <= 4)) {
                        bonificacionCentro = BONIFICACION_CENTRO;
                    }

                    if (pieza.esBlanca() == esBlanca) {
                        puntuacion += bonificacionPosicion + bonificacionCentro;
                    } else {
                        puntuacion -= bonificacionPosicion + bonificacionCentro;
                    }
                }
            }
        }
        return puntuacion;
    }
    
    /**
     * Evalúa la movilidad de las piezas (cantidad de movimientos disponibles).
     *
     * @param tabla El tablero a evaluar.
     * @return La puntuación de movilidad.
     */
    private int evaluarMovilidad(Tabla tabla) {
        int movimientosIA = tabla.getMovimientosDisponibles(esBlanca).size();
        int movimientosOponente = tabla.getMovimientosDisponibles(!esBlanca).size();
        
        return (movimientosIA - movimientosOponente) * BONIFICACION_MOVILIDAD;
    }
    
    /**
     * Evalúa la estructura de piezas en el tablero.
     *
     * @param tabla El tablero a evaluar.
     * @return La puntuación de estructura.
     */
    private int evaluarEstructura(Tabla tabla) {
        int puntuacion = 0;
        Casillero[][] tablero = tabla.getTablero();
        
        // Evalúa la protección de piezas (piezas que tienen apoyo)
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tablero[i][j].tienePieza()) {
                    Pieza pieza = tablero[i][j].getPieza();
                    
                    // Verifica si la pieza tiene apoyo diagonal
                    boolean tieneApoyo = false;
                    if (pieza.esBlanca()) {
                        // Verifica apoyo desde atrás (fila i+1)
                        if (i + 1 < 8) {
                            if (j - 1 >= 0 && tablero[i + 1][j - 1].tienePieza() && 
                                tablero[i + 1][j - 1].getPieza().esBlanca()) {
                                tieneApoyo = true;
                            }
                            if (j + 1 < 8 && tablero[i + 1][j + 1].tienePieza() && 
                                tablero[i + 1][j + 1].getPieza().esBlanca()) {
                                tieneApoyo = true;
                            }
                        }
                    } else {
                        // Verifica apoyo desde atrás (fila i-1)
                        if (i - 1 >= 0) {
                            if (j - 1 >= 0 && tablero[i - 1][j - 1].tienePieza() && 
                                !tablero[i - 1][j - 1].getPieza().esBlanca()) {
                                tieneApoyo = true;
                            }
                            if (j + 1 < 8 && tablero[i - 1][j + 1].tienePieza() && 
                                !tablero[i - 1][j + 1].getPieza().esBlanca()) {
                                tieneApoyo = true;
                            }
                        }
                    }
                    
                    if (tieneApoyo) {
                        if (pieza.esBlanca() == esBlanca) {
                            puntuacion += 3; // Bonificación por apoyo
                        } else {
                            puntuacion -= 3; // Penalización por apoyo del oponente
                        }
                    }
                }
            }
        }
        
        return puntuacion;
    }
}