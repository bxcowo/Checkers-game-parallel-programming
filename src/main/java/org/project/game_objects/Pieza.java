package org.project.game_objects;

/**
 * Representa una pieza del juego de damas.
 * Una pieza tiene un color, puede ser coronada (convertida en rey) y puede ser capturada.
 */
public class Pieza {
    /**
     * Coordenada de la fila actual de la pieza (0-7).
     */
    int fila;
    /**
     * Coordenada de la columna actual de la pieza (0-7).
     */
    int columna;
    /**
     * El color de la pieza (true para blanco, false para negro).
     */
    final boolean esBlanca;
    /**
     * Estado de la pieza, true si ha sido capturada.
     */
    boolean capturada;
    /**
     * Estado de la pieza, true si ha sido coronada (es un rey).
     */
    boolean esRey;

    /**
     * Construye una nueva pieza en una posición y con un color determinados.
     *
     * @param fila     La fila inicial de la pieza.
     * @param columna  La columna inicial de la pieza.
     * @param esBlanca El color de la pieza (true si es blanca, false si es negra).
     */
    public Pieza(int fila, int columna, boolean esBlanca) {
        this.fila = fila;
        this.columna = columna;
        this.esBlanca = esBlanca;
        this.esRey = false;
    }

    /**
     * Verifica si la pieza es de color blanco.
     *
     * @return true si la pieza es blanca, false si es negra.
     */
    public boolean esBlanca() {
        return esBlanca;
    }

    /**
     * Verifica si la pieza es un rey.
     *
     * @return true si la pieza ha sido coronada.
     */
    public boolean esRey() {
        return esRey;
    }

    /**
     * Corona la pieza, convirtiéndola en un rey.
     */
    public void coronar() {
        this.esRey = true;
    }

    /**
     * Devuelve la representación en cadena de la pieza.
     * ●/○ para piezas normales, ◆/◇ para reyes.
     *
     * @return El símbolo que representa la pieza.
     */
    @Override
    public String toString() {
        return this.esBlanca ? (this.esRey ? "◆" : "●") : (this.esRey ? "◇" : "○");
    }
}
