# Informe de Desarrollo - Parque Residencial

 Pablo Adrian Mazariegos Ruiz 202402337

## 1. Resumen

Se desarrolló en Java 17 con Swing un sistema para gestionar un parqueo de 150 espacios con tres garitas de entrada y dos de salida, cada una en su propio hilo. Las estructuras de datos (listas simples y dobles, cola, pila) son propias, sin colecciones de `java.util`. Los residentes y vehículos se guardan en archivos `.txt`. El proyecto se entrega como `.jar` y con el código en un repositorio de GitHub con commits incrementales.

## 2. Plan por etapas

1. **Estructuras:** `ListaSimple`, `ListaDoble`, `Cola`, `Pila` y `ColaSegura`, con pruebas por consola.
2. **Modelo:** `Vehiculo`, `Residente`, `EspacioParqueo`, `Parqueo`, `Evento` e `HistorialEventos`.
3. **Hilos:** `GaritaEntrada` y `GaritaSalida`, con sincronización.
4. **Interfaz:** ventana principal y seis paneles.
5. **Persistencia:** carga y guardado de residentes y vehículos.
6. **Pruebas y entrega:** pruebas completas, `.jar`, documentación y presentación.

Las fechas reales de cada etapa se pueden ver en el historial de commits del repositorio.

## 3. Avance

El trabajo se hizo por etapas, con un commit al terminar cada bloque del plan:

- Primeros días: repositorio, estructura de carpetas y estructuras de datos propias.
- Luego: modelo, parqueo con sus 150 espacios y pruebas por consola.
- Martes 27 de octubre: clase `Residencial`, ventana principal y los seis paneles.
- Miércoles 28 de octubre: pruebas completas (incluida la prueba de parqueo lleno), corrección de errores y verificación del `.jar`.
- Jueves 29 de octubre: documentación y paquete de entrega.

## 4. Problemas y soluciones

| Problema | Causa | Solución |
| --- | --- | --- |
| Errores de compilación en `Vehiculo` | El constructor tenía un parámetro `estado` de más y faltaban getters | Se comparó el archivo con el código esperado y se corrigió |
| `NullPointerException` en `ListaSimple.obtener` | El ciclo avanzaba un nodo de más | El ciclo parte de la cabeza y avanza `indice` veces |
| "El vehículo ya no está estacionado" al salir | En `Parqueo.buscarVehiculo` se recorría dos veces la misma cabeza (`cabezaGeneral`) | Se recorre primero `cabezaSocios` y luego `cabezaGeneral` |
| Riesgo de perder datos por la ruta | El proyecto estaba en una carpeta de OneDrive | Se movió a `C:\DESARROLLO\IPC1` |
| Datos de prueba en los archivos | Las pruebas modificaban `data/` | Se restauraron los datos de ejemplo antes de generar el `.jar` |

## 5. Pruebas y resultados

Se usaron clases de prueba por consola (`PruebaEstructuras`, `PruebaParqueo`, `PruebaModelo`, `PruebaColaSegura`, `PruebaGaritas`, `PruebaLleno`) y pruebas manuales en la interfaz.

**Prueba de parqueo lleno:** se enviaron 160 vehículos. Resultado: 0 espacios libres, 150 ocupados, 10 rechazados, 320 eventos registrados y ningún espacio asignado dos veces.

También se comprobó que el `.jar` abre con Java 17 o superior, que los datos se guardan al cerrar y se cargan al abrir, y que las reglas de áreas (socios en A–C primero, el resto en E–I) se cumplen.

## 6. Herramientas y apoyo recibido

- Java 17, Swing, VS Code, Git y GitHub.
- **Apoyo de inteligencia artificial:** durante el desarrollo se usó Claude (Anthropic) como asistente. Redactó gran parte del código y fue explicando cada clase mientras se colocaba y probaba en el proyecto; también ayudó a depurar errores y a redactar esta documentación. Lo declaro aquí de forma abierta, y en la presentación puedo explicar el funcionamiento de cada parte.
- Enunciado del proyecto proporcionado por el curso.

## 7. Conclusiones

- Se cumplieron los requisitos del enunciado: estructuras propias, parqueo de 150 espacios, hilos, interfaz gráfica y persistencia.
- Los hilos obligan a pensar en el acceso compartido: sin `synchronized` dos garitas podrían asignar el mismo espacio.
- Programar las estructuras desde cero ayuda a entender qué hace una lista o una cola por dentro.
- Lo que mejoraría: guardar también el estado del parqueo y añadir más pruebas automáticas.
