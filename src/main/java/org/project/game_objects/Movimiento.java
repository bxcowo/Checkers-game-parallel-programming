package org.project.game_objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representa un movimiento en el juego de damas, desde una casilla de origen a una de destino.
 */
public class Movimiento {
    /**
     * Fila de origen del movimiento (0-7).
     */
    public final int filaOrigen;
    /**
     * Columna de origen del movimiento (0-7).
     */
    public final int columnaOrigen;
    /**
     * Fila de destino del movimiento (0-7).
     */
    public final int filaDestino;
    /**
     * Columna de destino del movimiento (0-7).
     */
    public final int columnaDestino;

    /**
     * Construye un movimiento a partir de una cadena en notación de damas (ej. "a3-b4").
     *
     * @param entrada La cadena que representa el movimiento.
     * @throws IllegalArgumentException si la cadena no tiene el formato esperado.
     */
    public Movimiento(String entrada) {
        Pattern patron = Pattern.compile("^([a-h])([1-8])-([a-h])([1-8])$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patron.matcher(entrada);

        if (matcher.find()) {
            // Convierte la notación de tablero (ej. 'a', '1') a índices de matriz (0-7)
            this.filaOrigen = 'h' - matcher.group(1).toLowerCase().charAt(0);
            this.columnaOrigen = Integer.parseInt(matcher.group(2)) - 1;
            this.filaDestino = 'h' - matcher.group(3).toLowerCase().charAt(0);
            this.columnaDestino = Integer.parseInt(matcher.group(4)) - 1;
        } else {
            throw new IllegalArgumentException("Formato de movimiento ilegal: " + entrada);
        }
    }

    /**
     * Construye un movimiento a partir de coordenadas de la matriz.
     *
     * @param filaOrigen     Fila de origen (0-7).
     * @param columnaOrigen  Columna de origen (0-7).
     * @param filaDestino    Fila de destino (0-7).
     * @param columnaDestino Columna de destino (0-7).
     */
    public Movimiento(int filaOrigen, int columnaOrigen, int filaDestino, int columnaDestino) {
        this.filaOrigen = filaOrigen;
        this.columnaOrigen = columnaOrigen;
        this.filaDestino = filaDestino;
        this.columnaDestino = columnaDestino;
    }

    /**
     * Verifica si el movimiento es una captura (salto de 2 casillas en diagonal).
     *
     * @return true si es un movimiento de captura.
     */
    public boolean esCaptura() {
        return Math.abs(filaOrigen - filaDestino) == 2 && Math.abs(columnaOrigen - columnaDestino) == 2;
    }

    /**
     * Verifica si el movimiento es una captura (salto de 2 casillas en diagonal).
     *
     * @return true si es un movimiento de captura.
     */
    public boolean esRegular() {
        return Math.abs(filaOrigen - filaDestino) == 1 && Math.abs(columnaOrigen - columnaDestino) == 1;
    }

    /**
     * Obtiene la fila de la pieza que sería capturada en este movimiento.
     *
     * @return La fila de la pieza capturada, o -1 si no es un movimiento de captura.
     */
    public int getFilaPiezaCapturada() {
        if (esCaptura()) {
            return (filaOrigen + filaDestino) / 2;
        }
        return -1;
    }

    /**
     * Obtiene la columna de la pieza que sería capturada en este movimiento.
     *
     * @return La columna de la pieza capturada, o -1 si no es un movimiento de captura.
     */
    public int getColumnaPiezaCapturada() {
        if (esCaptura()) {
            return (columnaOrigen + columnaDestino) / 2;
        }
        return -1;
    }

    /**
     * Devuelve la representación del movimiento en notación estándar de damas.
     *
     * @return Una cadena como "a3-b4".
     */
    @Override
    public String toString() {
        char fromLetter = (char) ('h' - filaOrigen);
        char toLetter = (char) ('h' - filaDestino);
        return fromLetter + "" + (columnaOrigen + 1) + "-" + toLetter + "" + (columnaDestino + 1);
    }
}
