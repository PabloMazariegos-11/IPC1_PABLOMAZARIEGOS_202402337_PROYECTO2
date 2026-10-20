# Parque Residencial: Sistema de Gestión de Parqueo

Proyecto de Introducción a la Programación y Computación 1 (IPC1) - USAC, Segundo Semestre 2026.

**Autor:** Pablo Adrian Mazariegos Ruiz - Carnet 202402337

Simulación en Java (JDK 17+) con interfaz Java Swing de un parqueo residencial:
3 garitas de entrada, 2 de salida (hilos concurrentes), 150 espacios, y
estructuras de datos implementadas manualmente (sin Java Collections Framework).

## Estructura de paquetes

| Paquete | Contenido |
|---|---|
| `parque.estructuras` | Nodos, lista simple, lista doble, lista de listas, cola y pila |
| `parque.modelo` | Residente, Vehiculo, Visitante, EspacioParqueo, Evento |
| `parque.hilos` | Garitas de entrada y salida (Threads) |
| `parque.persistencia` | Lectura y escritura de `residentes.txt` y `vehiculos.txt` |
| `parque.vista` | Paneles y ventana principal en Swing |

## Archivos de datos (`data/`)

- `residentes.txt`: `ID|NOMBRE|CASA|SOCIO`
- `vehiculos.txt`: `PLACA|MARCA|MODELO|COLOR|TIPO|ID_RESIDENTE`

## Restricciones

- Sin `ArrayList`, `LinkedList`, `Stack`, `Queue`, `Deque` ni otras colecciones de `java.util` para reemplazar las estructuras pedidas.
- Sin JavaFX, bases de datos ni frameworks externos.

## Compilar y ejecutar

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out parque.Main
```

## Convención de commits

`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`, `build:`
