# Manual Técnico - Parque Residencial

Oct 29, 2026 · @Pablo

## 1. Introducción y alcance

El sistema simula, en Java con interfaz Swing, el control de acceso de un residencial: los vehículos llegan, esperan en una cola, son atendidos por una de tres garitas de entrada, ocupan uno de 150 espacios y salen por una de dos garitas de salida. Todas las estructuras de datos están programadas desde cero, sin usar colecciones de `java.util`.

Este manual explica cómo está construido: paquetes, estructuras, parqueo, hilos, persistencia, interfaz y las decisiones de diseño.

| Requisito del enunciado | Dónde se cumple |
| --- | --- |
| Lista doble de residentes | `ListaDoble<Residente>` dentro de `Residencial` |
| Lista simple de vehículos por residente, máximo 3 | `ListaSimple<Vehiculo>` dentro de `Residente`; el límite está en `agregarVehiculo` |
| Lista doble de listas simples del parqueo, 150 nodos | `Parqueo` y `EspacioParqueo` |
| Cola de entrada y cola de salida FIFO | `Cola` y `ColaSegura` dentro de `Residencial` |
| Pila de eventos LIFO | `Pila` dentro de `HistorialEventos` |
| 3 garitas de entrada y 2 de salida como hilos | `GaritaEntrada` y `GaritaSalida` |
| Persistencia en archivos de texto | `Persistencia` |
| Seis paneles en Java Swing | paquete `vista` |

## 2. Arquitectura y organización de paquetes

El código se organiza en capas: la interfaz depende del modelo y de los hilos, estos dependen del modelo y todos descansan sobre las estructuras de datos propias.

| Paquete | Clases | Responsabilidad |
| --- | --- | --- |
| `parque.estructuras` | `NodoSimple`, `ListaSimple`, `NodoDoble`, `ListaDoble`, `Cola`, `Pila`, `ColaSegura` | Estructuras genéricas implementadas desde cero |
| `parque.modelo` | `Vehiculo`, `Residente`, `EspacioParqueo`, `Parqueo`, `Evento`, `HistorialEventos`, `Residencial` | Datos del negocio y sus reglas |
| `parque.hilos` | `GaritaEntrada`, `GaritaSalida` | Las cinco garitas como hilos concurrentes |
| `parque.persistencia` | `Persistencia` | Lectura y escritura de `residentes.txt` y `vehiculos.txt` |
| `parque.vista` | `VentanaPrincipal`, `Actualizable` y seis paneles | Interfaz Swing |
| `parque` | `Main` y clases de prueba por consola | Arranque del sistema y pruebas |

`Residencial` es el centro del sistema: contiene el parqueo, las dos colas de acceso, el historial de eventos, la lista de residentes y el tiempo de atención de las garitas. `Main` crea una sola instancia y se la entrega a todas las garitas y a todos los paneles, de modo que todos trabajan sobre el mismo estado compartido.

## 3. Diagrama de clases

&#91;embedded content: paquetes y clases · 5 capas\]

Las flechas indican quién usa a quién. `persistencia` y `modelo` se usan en los dos sentidos: `Residencial` delega en `Persistencia`, y esta crea residentes y vehículos.

| Clase | Contiene o usa | Relación |
| --- | --- | --- |
| `Residencial` | `Parqueo`, dos `ColaSegura<Vehiculo>`, `HistorialEventos`, `ListaDoble<Residente>` | Composición: es el estado compartido |
| `Residente` | `ListaSimple<Vehiculo>` | Composición: sus vehículos, máximo 3 |
| `Parqueo` | `EspacioParqueo` enlazados con `siguiente`, `arriba` y `abajo` | Composición: 150 nodos en dos áreas |
| `EspacioParqueo` | `Vehiculo` | Asociación: el vehículo estacionado |
| `HistorialEventos` | `Pila<Evento>` | Composición |
| `ColaSegura` | `Cola` | Composición: la protege con `synchronized` |
| `Cola` y `Pila` | `ListaSimple` | Composición |
| `ListaSimple` y `ListaDoble` | `NodoSimple` y `NodoDoble` | Composición |
| `GaritaEntrada` y `GaritaSalida` | `Residencial` | Asociación: consumen sus colas y usan el parqueo; extienden `Thread` |
| `VentanaPrincipal` | Los seis paneles, `Residencial` y las garitas | Composición; los paneles implementan `Actualizable` |

## 4. Estructuras de datos

Todas son genéricas (`<T>`), lo que permite reutilizarlas para vehículos, residentes y eventos. Los genéricos son una característica del lenguaje, no una colección, por lo que no contradicen la restricción del enunciado.

| Estructura | Cómo funciona | Operaciones principales | Dónde se usa |
| --- | --- | --- | --- |
| `NodoSimple<T>` | Guarda un `dato` y una referencia `siguiente` | getters y setters | Base de `ListaSimple` |
| `ListaSimple<T>` | Cadena de nodos con `cabeza` y `tamanio` | `insertarAlFinal`, `insertarAlInicio`, `obtener`, `contiene`, `eliminar`, `eliminarPrimero`, `verPrimero`, `imprimir` | Vehículos de cada residente; base de `Cola` y `Pila` |
| `NodoDoble<T>` | Guarda `dato`, `siguiente` y `anterior` | getters y setters | Base de `ListaDoble` |
| `ListaDoble<T>` | Lista con `cabeza` y `cola`, recorrible en ambos sentidos | `insertarAlFinal`, `eliminar` (arregla los dos enlaces), `obtener`, `buscar`, `imprimir`, `imprimirAlReves` | Lista de residentes |
| `Cola<T>` | FIFO sobre una `ListaSimple`: entra por el final, sale por el frente | `encolar`, `desencolar`, `verFrente`, `ver(i)` | Base de las colas de entrada y salida |
| `Pila<T>` | LIFO sobre una `ListaSimple`: entra y sale por el inicio | `apilar`, `desapilar`, `verTope`, `ver(i)` | Base del historial de eventos |
| `ColaSegura<T>` | `Cola` protegida con `synchronized`, `wait()` y `notifyAll()` | `encolar`, `desencolar` (espera si está vacía), `detener`, `ver(i)` | Cola de entrada y cola de salida |

**Igualdad de objetos.** `eliminar`, `contiene` y `buscar` comparan con `equals`. `Vehiculo` es igual a otro si tiene la misma placa y `Residente` si tiene el mismo ID, de modo que las listas encuentran el elemento sin conocer su tipo.

**Actualizar un residente.** `ListaDoble.buscar` devuelve el mismo objeto que está guardado, no una copia, por lo que actualizar es buscar al residente y usar sus setters.

**Lectura sin extraer.** `Cola.ver(i)` y `Pila.ver(i)` permiten que los paneles dibujen el contenido sin sacar elementos. La posición 0 es el frente de la cola o el tope de la pila.

## 5. Parqueo: lista doble de listas simples y reglas de asignación

El parqueo tiene 150 nodos y cada nodo es un espacio físico, no un vehículo. `EspacioParqueo` guarda su identificador (por ejemplo `A12`), si está ocupado, el vehículo que contiene, su tipo (`SOCIO` o `GENERAL`) y tres enlaces: `siguiente`, `arriba` y `abajo`.

**Construcción.** `Parqueo.construir()` recorre las letras `A B C E F G H I`, sin letra D. Las filas A, B y C tienen 25 espacios (75 en total, área de socios) y las filas E, F, G, H e I tienen 15 (75 en total, área general).

- Cada **fila** es una lista simple enlazada con `siguiente`.
- Las filas de una misma área se enlazan entre sí con `arriba` y `abajo`, columna por columna: `B3` tiene como `arriba` a `A3` y como `abajo` a `C3`. Un puntero auxiliar `encima` recorre la fila anterior al mismo ritmo que se crea la nueva.
- Las dos áreas no se enlazan entre sí, porque no hay fila D entre ellas. Por eso se guardan dos cabezas: `cabezaSocios` (`A1`) y `cabezaGeneral` (`E1`).
- Para recorrer un área se avanza con `abajo` por las filas y con `siguiente` dentro de cada fila. Así buscan `buscarLibre`, `buscarPorPlaca`, `contarLibres` y el panel de parqueo.

**Reglas de asignación.** El enunciado deja las reglas del área general al estudiante. Se decidió lo siguiente:

| Tipo de usuario | Primero busca en | Si está llena | Si todo está lleno |
| --- | --- | --- | --- |
| Socio | Área de socios (A–C) | Usa el área general (E–I) | Se rechaza y se registra el evento |
| Residente no socio | Área general (E–I) | No tiene alternativa | Se rechaza y se registra el evento |
| Visitante | Área general (E–I) | No tiene alternativa | Se rechaza y se registra el evento |

Así ningún vehículo no autorizado ocupa un espacio exclusivo de socios, porque `asignar` solo mira el área de socios cuando `esSocio` es verdadero. Dentro de un área se toma el primer espacio libre, fila por fila y de izquierda a derecha.

**Métodos protegidos.** `asignar`, `liberar`, `idEspacioDe` y `buscarVehiculo` son `synchronized`, por lo que dos garitas nunca reciben el mismo espacio.

## 6. Flujo de entrada y salida de vehículos

&#91;embedded content: flujo de entrada y salida · 2 recorridos, 1 decisión\]

Los dos recuadros con borde azul son los únicos pasos que modifican el parqueo, y por eso están protegidos con `synchronized`. Cada paso además registra un evento en la pila.

**Entrada**

1. **Llegada.** Desde el panel de entrada, el vehículo de un residente o un visitante pasa al estado `EN_COLA` y se encola en la cola de entrada. Se registra "llega a la cola de entrada" (y "visitante registrado" si lo es).
2. **Atención.** Una de las tres garitas toma el siguiente vehículo (si la cola está vacía, espera con `wait()`), registra "Garita E# atiende a ..." y espera el tiempo de atención.
3. **Validación.** Se rechaza si la placa ya está estacionada o si el vehículo dice pertenecer a un residente que no existe. Si el residente existe, se averigua si es socio; un visitante nunca es socio.
4. **Asignación.** `Parqueo.asignar` aplica las reglas de áreas y devuelve el espacio o `null`.
5. **Resultado.** Con espacio, el vehículo pasa a `ESTACIONADO` y se registra "asignado al espacio X" con su tipo (socio, residente o visitante). Sin espacio, vuelve a `AFUERA` y se registra "rechazado" con el motivo.

**Salida**

1. **Solicitud.** En el panel de salida se elige un vehículo estacionado. Pasa a `SALIENDO`, se encola en la cola de salida y se registra "solicita salida".
2. **Atención.** Una de las dos garitas lo toma, registra "Garita S# atiende la salida de ..." y espera el tiempo de atención.
3. **Comprobación.** Si el vehículo ya no está en el parqueo, se registra "salida rechazada" y termina.
4. **Liberación.** `Parqueo.liberar` vacía el espacio y el vehículo vuelve a `AFUERA`.
5. **Registro.** Se anotan "vehículo salió" y "espacio X liberado".

## 7. Hilos y sincronización

Hay cinco hilos con función real: tres `GaritaEntrada` que consumen de la cola de entrada y dos `GaritaSalida` que consumen de la cola de salida. Ambas clases extienden `Thread` y se arrancan con `start()` desde `Main`.

**Ciclo de una garita.** Pide el siguiente vehículo a su `ColaSegura`, registra en el historial que lo atiende, espera con `Thread.sleep` el tiempo de atención (1,5 segundos por defecto, para que la simulación se pueda observar), procesa al vehículo y repite. Cuando la cola es detenida y queda vacía, recibe `null` y el hilo termina.

**Espera y notificación.** En `ColaSegura.desencolar`, si la cola está vacía el hilo ejecuta `wait()` dentro de un ciclo `while` y suelta el candado. `encolar` agrega el elemento y ejecuta `notifyAll()`. Se usa `while` y no `if` porque `notifyAll` despierta a todas las garitas pero llega un solo vehículo: las demás deben volver a comprobar la condición y dormirse otra vez. `detener()` marca la cola como inactiva y despierta a todos para que las garitas puedan terminar al cerrar el programa.

**Recursos compartidos y su protección.**

| Recurso | Quién lo comparte | Protección |
| --- | --- | --- |
| Cola de entrada y cola de salida | Interfaz y garitas | Métodos `synchronized` de `ColaSegura` con `wait` y `notifyAll` |
| Parqueo (150 espacios) | Las cinco garitas y la interfaz | `asignar`, `liberar`, `idEspacioDe` y `buscarVehiculo` son `synchronized` |
| Historial de eventos | Las cinco garitas y la interfaz | `registrar`, `obtener` y `getTamanio` son `synchronized` |
| Lista de residentes | Garitas (lectura) e interfaz (edición) | Métodos `synchronized` de `Residencial` |
| `ocupado` y `vehiculo` de cada espacio | Garitas e interfaz | Atributos `volatile` para que la interfaz vea siempre el último valor |
| `vehiculoActual` de cada garita | Garita e interfaz | Atributo `volatile` |
| Tiempo de atención | Interfaz y garitas | Atributo `volatile` |

**Prevención de condiciones de carrera.** Como `asignar` es `synchronized`, mientras una garita busca y ocupa un espacio las demás esperan, y dos garitas no pueden recibir el mismo espacio. Además, `GaritaEntrada` rechaza un vehículo cuya placa ya está estacionada.

**Evidencia de las pruebas.** `PruebaLleno` envía 160 vehículos (80 socios y 80 visitantes) a un parqueo de 150 espacios con las tres garitas de entrada compitiendo. En cada ejecución se obtuvo el mismo resultado: 0 espacios libres, 150 ocupados, 10 vehículos rechazados, 320 eventos y 0 placas duplicadas. `PruebaGaritas` repite el ejercicio con las cinco garitas, entradas y salidas incluidas.

## 8. Persistencia en archivos de texto

Los residentes y sus vehículos se guardan en dos archivos de texto plano dentro de la carpeta `data/`, con los campos separados por el carácter `|`.

| Archivo | Formato de cada línea | Ejemplo |
| --- | --- | --- |
| `residentes.txt` | `ID\|NOMBRE\|CASA\|SOCIO` | `R1\|Ana Lopez\|12\|true` |
| `vehiculos.txt` | `PLACA\|MARCA\|MODELO\|COLOR\|TIPO\|ID_RESIDENTE` | `P111AAA\|Toyota\|Corolla\|Rojo\|Carro\|R1` |

El formato sugerido por el enunciado no incluye la columna `TIPO`. Se agregó porque el tipo de vehículo es uno de los datos que debe tener cada vehículo, y así no se pierde al reiniciar.

**Cuándo se guarda y se carga.** `Main` llama a `Residencial.cargarDatos` al iniciar, y `VentanaPrincipal.cerrar` llama a `Residencial.guardarDatos` al cerrar la ventana. Ambos delegan en la clase `Persistencia`.

- **Guardar:** recorre la lista doble de residentes. Escribe una línea por residente y, por cada vehículo de su lista simple, una línea en el otro archivo.
- **Cargar:** lee primero los residentes y los inserta en una lista doble. Después lee los vehículos y busca a cada dueño por ID con `ListaDoble.buscar` para agregarlo con `agregarVehiculo`, que vuelve a aplicar el límite de 3. Si un archivo no existe, devuelve una lista vacía sin error.

**Los visitantes no se persisten.** Un visitante es un `Vehiculo` sin residente (`idResidente` nulo) que nunca se agrega a la lista de residentes. Como solo se escriben los vehículos que cuelgan de un residente, los visitantes desaparecen al cerrar el programa.

**Qué no se guarda.** El estado de la simulación no se persiste: al iniciar, el parqueo está vacío, las colas están vacías, el historial está en blanco y todos los vehículos están fuera.

**Validación.** Los paneles de residentes y vehículos rechazan el carácter `|` en los datos, porque rompeía el formato del archivo.

**Uso de la API de Java.** Se usan `BufferedReader`, `FileReader`, `PrintWriter` y `FileWriter` de `java.io`, que el enunciado permite como clases auxiliares para leer y escribir archivos.

## 9. Interfaz gráfica

La interfaz usa solo Java Swing, escrita a mano, sin diseñadores visuales. `VentanaPrincipal` es un `JFrame` con un `JTabbedPane` de seis pestañas, una por panel.

| Panel | Función | Qué lee o modifica |
| --- | --- | --- |
| `PanelParqueo` | Dibuja los 150 espacios, verdes si están libres y rojos si están ocupados, con la placa | Recorre la lista de listas del `Parqueo` |
| `PanelResidentes` | Registrar, buscar, modificar y eliminar residentes | `ListaDoble` de residentes |
| `PanelVehiculos` | Registrar, filtrar por residente (indica si es socio) y eliminar vehículos | `ListaSimple` de cada residente |
| `PanelEntrada` | Enviar vehículos de residentes o registrar visitantes, ver la cola de entrada y qué atiende cada garita | `ColaSegura` de entrada y `GaritaEntrada` |
| `PanelSalida` | Lista de estacionados, solicitar salida, cola de salida y garitas | `Parqueo`, `ColaSegura` de salida y `GaritaSalida` |
| `PanelEventos` | Historial con lo más reciente primero y filtro de últimos 10, 25, 50 o todos | `HistorialEventos` (pila) |

**Actualización de la vista y manejo de hilos.** Swing solo permite modificar componentes desde su propio hilo de eventos, y las garitas son otros hilos. Por eso las garitas nunca tocan la interfaz. Los paneles implementan `Actualizable` y un `javax.swing.Timer` de 500 ms, que se ejecuta en el hilo de eventos de Swing, llama a `actualizar()` solo sobre la pestaña visible, que lee el estado compartido ya protegido. La ventana se crea con `SwingUtilities.invokeLater` desde `Main`.

**Tablas.** Las tablas son `JTable` con un `DefaultTableModel` que solo sirve para mostrar: los datos reales viven en las estructuras propias y el modelo se reconstruye a partir de ellas. Los paneles que se redibujan cada 500 ms conservan la fila seleccionada.

**Cierre.** La ventana usa `DO_NOTHING_ON_CLOSE` y `cerrar()` guarda los archivos, detiene las dos colas para que las garitas terminen y sale del programa.

## 10. Decisiones de diseño y limitaciones

- **Reglas del área general.** Los socios usan primero A–C y pasan a E–I si está llena; los residentes no socios y los visitantes solo usan E–I.
- **Enlaces `arriba` y `abajo` por área.** Las filas se enlazan columna por columna dentro de cada área, y las dos áreas no se conectan porque no hay fila D.
- **Estados de un vehículo.** Pasa por `AFUERA`, `EN_COLA`, `ESTACIONADO` y `SALIENDO`. El estado `SALIENDO` impide solicitar dos veces la salida del mismo vehículo.
- **Refresco con `Timer`.** Se prefirió que la interfaz consulte el estado cada 500 ms, en lugar de que las garitas avisen a la ventana con `invokeLater`, porque separa por completo los hilos de la interfaz y evita errores de sincronización.
- **Protecciones de borrado.** No se puede eliminar un residente ni un vehículo que esté en una cola o estacionado, para no dejar vehículos sin dueño dentro del parqueo.
- **Visitantes temporales.** Se crean desde el panel de entrada y viven solo mientras están en cola o estacionados.
- **El estado de la simulación no se guarda.** Solo se persisten residentes y vehículos.
- **Eficiencia.** `obtener(i)` y la inserción al final de `ListaSimple` recorren la lista, y `obtener(i)` de `ListaDoble` y de `Pila` también. Con máximo 3 vehículos por residente y pocos cientos de eventos, el costo es irrelevante.
- **Si el tipo de socio cambia** mientras un vehículo espera en la cola, la garita usa el valor que tenga el residente al momento de atenderlo.
- **Alcance opcional.** No se implementaron las funciones opcionales (deshacer, pausar y ajustar velocidad, llegadas automáticas). El tiempo de atención ya es un atributo del sistema, por lo que ajustar la velocidad sería una extensión pequeña.

## 11. Compilación, empaquetado y ejecución

Requiere JDK 17 o superior. Desde la carpeta raíz del proyecto, en PowerShell:

```powershell
mkdir out -Force
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
"Main-Class: parque.Main" | Out-File -Encoding ascii manifest.txt
jar cfm ParqueResidencial.jar manifest.txt -C out .
java -jar ParqueResidencial.jar
```

En Linux o macOS, la compilación es `javac -d out $(find src -name "*.java")`.

**Carpeta `data/`.** El programa lee y escribe `data/residentes.txt` y `data/vehiculos.txt` relativos a la carpeta desde donde se ejecuta. El entregable debe llevar el `.jar` y la carpeta `data/` juntos, y el `.jar` se ejecuta desde esa carpeta. Si `data/` no existe, el sistema arranca vacío.

**Pruebas por consola.** Las clases del paquete `parque` con nombre `Prueba...` se ejecutan por separado y cada una verifica una parte: `PruebaEstructuras` (listas, cola y pila), `PruebaParqueo` (asignar, liberar y reglas), `PruebaModelo` (residentes, límite de 3 y persistencia), `PruebaColaSegura` y `PruebaGaritas` (hilos) y `PruebaLleno` (parqueo lleno con 160 vehículos concurrentes).
