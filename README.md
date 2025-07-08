# Juego de Damas - Programación Paralela

Un juego de damas implementado en Java con soporte para inteligencia artificial paralela y secuencial.

## Descripción

Este proyecto implementa un juego de damas completo con las siguientes características:

- **Juego Humano vs Humano**: Dos jugadores pueden jugar en la misma computadora
- **Juego Humano vs IA**: Juega contra una inteligencia artificial
- **IA Paralela**: La IA puede usar procesamiento paralelo para mejorar el rendimiento
- **IA Secuencial**: Alternativa de procesamiento secuencial para comparación
- **Reglas completas**: Implementa todas las reglas del juego de damas, incluyendo capturas obligatorias

## Estructura del Proyecto

```
├── pom.xml                    # Configuración de Maven
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── project/
│                   ├── Main.java          # Clase principal con la lógica del juego
│                   └── game_objects/
│                       ├── AIPlayer.java  # Implementación de la IA
│                       ├── Casillero.java # Unidad mínima del tablero
│                       ├── Movimiento.java # Representación de movimientos
│                       ├── Pieza.java     # Representación de piezas
│                       └── Tabla.java     # Tablero de juego
└── target/                    # Archivos compilados (generado por Maven)
```

## Requisitos

- Java 24 o superior
- Maven 3.6 o superior
- PCDP (Parallel Computing with Data Parallelism) library

## Instalación y Ejecución

### 1. Compilar el proyecto

```bash
mvn clean compile
```

### 2. Ejecutar el juego

```bash
mvn exec:java -Dexec.mainClass="org.project.Main"
```

### 3. Crear JAR ejecutable (opcional)

```bash
mvn clean package
java -jar target/Tarea_6_Cheekers_Project-1.0-SNAPSHOT.jar
```

## Cómo Jugar

### Formato de Movimientos

Los movimientos se ingresan en el formato: `origen-destino`

Ejemplos:
- `a1-b2`: Mueve la pieza de la casilla a1 a la casilla b2
- `c3-d4`: Mueve la pieza de la casilla c3 a la casilla d4

### Modos de Juego

Al iniciar el juego, se te presentarán las siguientes opciones:

1. **Modo de Juego**: Elige entre Humano vs Humano o Humano vs IA
2. **Color de Piezas** (si juegas contra IA): Elige entre piezas blancas (●/◆) o negras (○/◇)
3. **Tipo de IA** (si juegas contra IA): Elige entre procesamiento paralelo o secuencial

### Reglas del Juego

- Las piezas se mueven diagonalmente
- Las capturas son obligatorias
- Cuando una pieza llega al extremo opuesto del tablero, se convierte en dama
- Las damas pueden moverse en todas las direcciones diagonales
- El juego termina cuando un jugador no tiene piezas o no puede mover

## Características Técnicas

### Inteligencia Artificial

La IA utiliza algoritmos de búsqueda con las siguientes características:

- **Profundidad de búsqueda**: 6 niveles por defecto
- **Procesamiento paralelo**: Utiliza la librería PCDP para paralelización
- **Evaluación de tablero**: Algoritmo de evaluación optimizado
- **Medición de rendimiento**: Muestra el tiempo de cálculo de cada movimiento

### Dependencias

- **PCDP Core**: Para el procesamiento paralelo
- **Java 24**: Aprovecha las últimas características del lenguaje
