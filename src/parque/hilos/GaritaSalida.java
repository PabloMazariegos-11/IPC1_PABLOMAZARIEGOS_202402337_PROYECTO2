package parque.hilos;

import parque.modelo.Residencial;
import parque.modelo.Vehiculo;

public class GaritaSalida extends Thread {
    private final int numero;
    private final Residencial residencial;
    private volatile Vehiculo vehiculoActual;

    public GaritaSalida(int numero, Residencial residencial) {
        super("Garita-Salida-" + numero);
        this.numero = numero;
        this.residencial = residencial;
    }

    public int getNumero() { return numero; }
    public Vehiculo getVehiculoActual() { return vehiculoActual; }

    @Override
    public void run() {
        try {
            while (true) {
                Vehiculo v = residencial.getColaSalida().desencolar();
                if (v == null) {
                    return;
                }
                vehiculoActual = v;
                residencial.getHistorial().registrar(
                        "Garita S" + numero + " atiende la salida de " + v.getPlaca());
                Thread.sleep(residencial.getTiempoAtencion());
                procesar(v);
                vehiculoActual = null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void procesar(Vehiculo v) {
        String idEspacio = residencial.getParqueo().idEspacioDe(v.getPlaca());
        if (idEspacio == null) {
            residencial.getHistorial().registrar(
                    "Salida rechazada: " + v.getPlaca() + " no esta estacionado");
            return;
        }
        residencial.getParqueo().liberar(v.getPlaca());
        residencial.getHistorial().registrar("Vehiculo " + v.getPlaca() + " salio");
        residencial.getHistorial().registrar("Espacio " + idEspacio + " liberado");
    }
}