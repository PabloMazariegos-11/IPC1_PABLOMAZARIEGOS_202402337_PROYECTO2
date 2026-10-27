package parque.hilos;

import parque.modelo.EspacioParqueo;
import parque.modelo.Residencial;
import parque.modelo.Residente;
import parque.modelo.Vehiculo;

public class GaritaEntrada extends Thread {
    private final int numero;
    private final Residencial residencial;
    private volatile Vehiculo vehiculoActual;

    public GaritaEntrada(int numero, Residencial residencial) {
        super("Garita-Entrada-" + numero);
        this.numero = numero;
        this.residencial = residencial;
    }

    public int getNumero() { return numero; }
    public Vehiculo getVehiculoActual() { return vehiculoActual; }

    @Override
    public void run() {
        try {
            while (true) {
                Vehiculo v = residencial.getColaEntrada().desencolar();
                if (v == null) {
                    return;
                }
                vehiculoActual = v;
                residencial.getHistorial().registrar(
                        "Garita E" + numero + " atiende a " + v.getPlaca());
                Thread.sleep(residencial.getTiempoAtencion());
                procesar(v);
                vehiculoActual = null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void procesar(Vehiculo v) {
        if (residencial.getParqueo().idEspacioDe(v.getPlaca()) != null) {
            rechazar(v, "ya esta estacionado");
            return;
        }

        boolean esSocio = false;
        if (!v.esVisitante()) {
            Residente r = residencial.buscarResidente(v.getIdResidente());
            if (r == null) {
                rechazar(v, "residente no registrado");
                return;
            }
            esSocio = r.isSocio();
        }

        EspacioParqueo espacio = residencial.getParqueo().asignar(v, esSocio);
        if (espacio == null) {
            rechazar(v, "parqueo lleno");
            return;
        }

        String tipo = v.esVisitante() ? "visitante" : (esSocio ? "socio" : "residente");
        residencial.getHistorial().registrar("Vehiculo " + v.getPlaca() + " (" + tipo
                + ") asignado al espacio " + espacio.getId());
    }

    private void rechazar(Vehiculo v, String motivo) {
        v.setEstado("AFUERA");
        residencial.getHistorial().registrar(
                "Vehiculo " + v.getPlaca() + " rechazado: " + motivo);
    }
}