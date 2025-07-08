package org.project.game_objects;

/**
 * Representa una única casilla en el tablero de ajedrez.
 * Cada casilla tiene coordenadas, un color y puede contener una pieza.
 */
public class Casillero {
     /**
      * Coordenada de la fila (0-7).
      */
     int fila;
     /**
      * Coordenada de la columna (0-7).
      */
     int columna;
     /**
      * Color de la casilla (true para blanco, false para negro).
      * En las damas, las piezas solo se mueven en las casillas oscuras.
      */
     boolean esBlanco;
     /**
      * La pieza que ocupa actualmente la casilla. Puede ser null si la casilla está vacía.
      */
     Pieza pieza;

     /**
      * Construye una nueva casilla con sus coordenadas y color.
      *
      * @param fila     La fila de la casilla (0-7).
      * @param columna  La columna de la casilla (0-7).
      * @param esBlanco true si la casilla es blanca, false si es negra.
      */
     public Casillero(int fila, int columna, boolean esBlanco) {
          this.fila = fila;
          this.columna = columna;
          this.esBlanco = esBlanco;
          this.pieza = null; // Inicialmente, la casilla está vacía.
     }

     /**
      * Obtiene la pieza que se encuentra en esta casilla.
      *
      * @return La pieza en la casilla, o null si está vacía.
      */
     public Pieza getPieza() {
          return this.pieza;
     }

     /**
      * Coloca una pieza en esta casilla.
      *
      * @param pieza La pieza a colocar.
      */
     public void setPieza(Pieza pieza) {
          this.pieza = pieza;
     }

     /**
      * Verifica si la casilla tiene una pieza.
      *
      * @return true si la casilla no está vacía, false en caso contrario.
      */
     public boolean tienePieza() {
          return pieza != null;
     }

     /**
      * Obtiene la representación de la parte superior de la casilla para la consola.
      *
      * @return Una cadena que representa el borde superior de la casilla.
      */
     public String getFilaSuperior() {
          return " -----";
     }

     /**
      * Obtiene la representación del contenido de la casilla para la consola.
      *
      * @return Una cadena que muestra la pieza (o un espacio si está vacía).
      */
     public String getFilaInferior() {
          return "  %s  |".formatted(this.pieza != null ? this.pieza.toString() : " ");
     }
}
