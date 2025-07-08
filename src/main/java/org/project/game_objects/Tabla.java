package org.project.game_objects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static edu.rice.pcdp.PCDP.*;

/**
 * Representa el tablero de juego de damas.
 * Gestiona el estado del tablero, las piezas y la ejecución de movimientos.
 */
public class Tabla {
    /**
     * Matriz 8x8 de casillas que representa el tablero.
     */
    Casillero[][] tablero;

    /**
     * Construye un nuevo tablero y lo inicializa con la disposición estándar de piezas de damas.
     */
    public Tabla() {
        this.tablero = new Casillero[8][8];

        // Inicializa las casillas del tablero
        forall2dChunked(0, 7, 0, 7, (i, j)->{
            boolean comienzaBlanco = i % 2 == 0;
            boolean esBlanco;
            if (comienzaBlanco) {
                esBlanco = j % 2 == 0;
            }else{
                esBlanco = j % 2 == 1;
            }
            tablero[i][j] = new Casillero(i, j, esBlanco);
        });

        // Coloca las piezas negras en las primeras 3 filas
        forall2dChunked(0, 2, 0, 7, (i, j) -> {
            if(!this.tablero[i][j].esBlanco) {
                this.tablero[i][j].setPieza(new Pieza(i, j, false));
            }
        });

        // Coloca las piezas blancas en las últimas 3 filas
        forall2dChunked(5, 7, 0, 7, (i, j) -> {
            if(!this.tablero[i][j].esBlanco) {
                this.tablero[i][j].setPieza(new Pieza(i, j, true));
            }
        });
    }

    /**
     * Constructor de copia para crear una copia profunda del tablero.
     * @param original La tabla original a copiar.
     */
    public Tabla(Tabla original) {
        this.tablero = new Casillero[8][8];

        forall2dChunked(0, 7, 0, 7, (i, j) -> {
            Casillero originalCasillero = original.tablero[i][j];
            Casillero nuevoCasillero = new Casillero(i, j, originalCasillero.esBlanco);
            if (originalCasillero.tienePieza()) {
                Pieza originalPieza = originalCasillero.getPieza();
                Pieza nuevaPieza = new Pieza(i, j, originalPieza.esBlanca());
                if (originalPieza.esRey()) {
                    nuevaPieza.coronar();
                }
                nuevoCasillero.setPieza(nuevaPieza);
            }
            this.tablero[i][j] = nuevoCasillero;
        });
    }


    /**
     * Obtiene la matriz de casillas del tablero.
     *
     * @return La matriz 8x8 de casillas.
     */
    public Casillero[][] getTablero() {
        return this.tablero;
    }

    /**
     * Ejecuta un movimiento en el tablero.
     *
     * @param movimiento       El movimiento a ejecutar.
     * @param esTurnoDeBlancas True si el jugador actual es blanco.
     */
    public void ejecutarMovimiento(Movimiento movimiento, boolean esTurnoDeBlancas) {
        if (!esMovimientoValido(movimiento, esTurnoDeBlancas)) {
            return;
        }

        Casillero casilleroOrigen = tablero[movimiento.filaOrigen][movimiento.columnaOrigen];
        Casillero casilleroDestino = tablero[movimiento.filaDestino][movimiento.columnaDestino];
        Pieza pieza = casilleroOrigen.getPieza();

        // Mueve la pieza
        casilleroDestino.setPieza(pieza);
        casilleroOrigen.setPieza(null);

        // Actualiza las coordenadas de la pieza
        pieza.fila = movimiento.filaDestino;
        pieza.columna = movimiento.columnaDestino;

        // Gestiona la captura de piezas
        if (movimiento.esCaptura()) {
            int filaCapturada = movimiento.getFilaPiezaCapturada();
            int columnaCapturada = movimiento.getColumnaPiezaCapturada();
            Casillero casilleroCapturado = tablero[filaCapturada][columnaCapturada];
            casilleroCapturado.setPieza(null);
        }

        // Promoción a rey
        if (!pieza.esRey()) {
            if ((pieza.esBlanca() && movimiento.filaDestino == 0) || (!pieza.esBlanca() && movimiento.filaDestino == 7)) {
                pieza.coronar();
            }
        }

    }

    /**
     * Obtiene todos los movimientos disponibles para un jugador.
     * Da prioridad a los movimientos de captura si existen.
     *
     * @param esTurnoDeBlancas True si se buscan movimientos para las piezas blancas.
     * @return Una lista de movimientos válidos.
     */
    public List<Movimiento> getMovimientosDisponibles(boolean esTurnoDeBlancas) {
        List<Movimiento> movimientosDeCaptura = getTodosMovimientosDeCaptura(esTurnoDeBlancas);
        if (!movimientosDeCaptura.isEmpty()) {
            return movimientosDeCaptura;
        }
        return getTodosMovimientosRegulares(esTurnoDeBlancas);
    }

    // --- Lógica de validación de movimientos (anteriormente en ValidadorMov) ---

    /**
     * Verifica si un movimiento es válido.
     *
     * @param m                  El movimiento a validar.
     * @param esTurnoDeBlancas   True si el turno es de las blancas.
     * @return true si el movimiento es legal.
     */
    public boolean esMovimientoValido(Movimiento m, boolean esTurnoDeBlancas) {
        if (!estanEnTablero(m.filaOrigen, m.columnaOrigen) || !estanEnTablero(m.filaDestino, m.columnaDestino)) {
            return false;
        }

        Casillero origen = tablero[m.filaOrigen][m.columnaOrigen];
        Casillero destino = tablero[m.filaDestino][m.columnaDestino];
        Pieza pieza = origen.getPieza();

        if (!origen.tienePieza() || pieza.esBlanca() != esTurnoDeBlancas) {
            return false;
        }

        if (destino.tienePieza() || destino.esBlanco) {
            return false;
        }

        if (m.esCaptura()) {
            return esMovimientoDeCapturaValido(m, pieza);
        } else {
            return esMovimientoRegularValido(m, pieza);
        }
    }

    private boolean esMovimientoRegularValido(Movimiento m, Pieza p) {
        if (!m.esRegular()) {
            return false;
        }
        if (p.esRey()) {
            return true;
        }
        if (p.esBlanca()) {
            return m.filaOrigen > m.filaDestino;
        } else {
            return m.filaOrigen < m.filaDestino;
        }
    }

    private boolean esMovimientoDeCapturaValido(Movimiento m, Pieza p) {
        int filaCapturada = m.getFilaPiezaCapturada();
        int colCapturada = m.getColumnaPiezaCapturada();
        Casillero casilleroCapturado = tablero[filaCapturada][colCapturada];

        if (!casilleroCapturado.tienePieza() || casilleroCapturado.getPieza().esBlanca() == p.esBlanca()) {
            return false;
        }

        if (!p.esRey()) {
            if (p.esBlanca()) {
                return m.filaOrigen > m.filaDestino; // Blancas capturan hacia adelante
            } else {
                return m.filaOrigen < m.filaDestino; // Negras capturan hacia adelante
            }
        }
        return true;
    }

    private List<Movimiento> getTodosMovimientosDeCaptura(boolean esTurnoDeBlancas) {
        return IntStream.range(0, 8)
                .boxed()
                .flatMap(i -> IntStream.range(0, 8)
                        .filter(j -> tablero[i][j].tienePieza() && tablero[i][j].getPieza().esBlanca() == esTurnoDeBlancas)
                        .mapToObj(j -> getMovimientosDeCapturaParaPieza(i, j))
                        .flatMap(List::stream))
                .parallel()
                .collect(Collectors.toList());
    }

    private List<Movimiento> getTodosMovimientosRegulares(boolean esTurnoDeBlancas) {
        return IntStream.range(0, 8)
                .boxed()
                .flatMap(i -> IntStream.range(0, 8)
                        .filter(j -> tablero[i][j].tienePieza() && tablero[i][j].getPieza().esBlanca() == esTurnoDeBlancas)
                        .mapToObj(j -> getMovimientosRegularesParaPieza(i, j))
                        .flatMap(List::stream))
                .parallel()
                .collect(Collectors.toList());
    }

    private List<Movimiento> getMovimientosDeCapturaParaPieza(int fila, int col) {
        List<Movimiento> movimientos = new ArrayList<>();
        Pieza pieza = tablero[fila][col].getPieza();
        int[][] direcciones;

        if (pieza.esRey()) {
            direcciones = new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        } else if (pieza.esBlanca()) {
            direcciones = new int[][]{{-2, -2}, {-2, 2}};
        } else {
            direcciones = new int[][]{{2, -2}, {2, 2}};
        }

        for (int[] dir : direcciones) {
            int filaDestino = fila + dir[0];
            int colDestino = col + dir[1];
            if (estanEnTablero(filaDestino, colDestino) && !tablero[filaDestino][colDestino].tienePieza()) {
                Movimiento mov = new Movimiento(fila, col, filaDestino, colDestino);
                if (esMovimientoDeCapturaValido(mov, pieza)) {
                    movimientos.add(mov);
                }
            }
        }
        return movimientos;
    }

    private List<Movimiento> getMovimientosRegularesParaPieza(int fila, int col) {
        List<Movimiento> movimientos = new ArrayList<>();
        Pieza pieza = tablero[fila][col].getPieza();
        int[][] direcciones;

        if (pieza.esRey()) {
            direcciones = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        } else if (pieza.esBlanca()) {
            direcciones = new int[][]{{-1, -1}, {-1, 1}};
        } else {
            direcciones = new int[][]{{1, -1}, {1, 1}};
        }

        for (int[] dir : direcciones) {
            int filaDestino = fila + dir[0];
            int colDestino = col + dir[1];
            if (estanEnTablero(filaDestino, colDestino) && !tablero[filaDestino][colDestino].tienePieza()) {
                Movimiento mov = new Movimiento(fila, col, filaDestino, colDestino);
                if (esMovimientoRegularValido(mov, pieza)) {
                    movimientos.add(mov);
                }
            }
        }
        return movimientos;
    }

    private boolean estanEnTablero(int fila, int col) {
        return fila >= 0 && fila < 8 && col >= 0 && col < 8;
    }

    /**
     * Verifica si el juego ha terminado.
     *
     * @return true si el juego ha concluido.
     */
    public boolean haTerminado() {
        return !(tieneMovimientosDisponibles(true) && tieneMovimientosDisponibles(false));
    }

    /**
     * Determina el ganador del juego.
     *
     * @return "Blancas", "Negras", "Empate" o null si el juego no ha terminado.
     */
    public String getGanador() {
        if (!haTerminado()) {
            return null;
        }

        if (!tieneMovimientosDisponibles(true)) {
            return "Negras";
        }

        return "Blancas";
    }

    private boolean tieneMovimientosDisponibles(boolean esBlanco) {
        return !getMovimientosDisponibles(esBlanco).isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        char[] letras = {'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        result.append("       [ 1 ] [ 2 ] [ 3 ] [ 4 ] [ 5 ] [ 6 ] [ 7 ] [ 8 ]\n");
        for (int i = 0; i < 8; i++) {
            result.append("      ");
            for (int j = 0; j < 8; j++) {
                result.append(tablero[i][j].getFilaSuperior());
            }
            result.append("\n[ %s ] |".formatted(letras[i]));
            for (int j = 0; j < 8; j++) {
                result.append(tablero[i][j].getFilaInferior());
            }
            result.append("\n");
        }
        result.append("      ");
        for (int j = 0; j < 8; j++) {
            result.append(tablero[0][j].getFilaSuperior());
        }
        return result.toString();
    }
}
