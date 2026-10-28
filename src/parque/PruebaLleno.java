package parque;

import parque.hilos.GaritaEntrada;
import parque.modelo.EspacioParqueo;
import parque.modelo.Evento;
import parque.modelo.Parqueo;
import parque.modelo.Residencial;
import parque.modelo.Residente;
import parque.modelo.Vehiculo;

public class PruebaLleno {
    public static void main(String[] args) throws InterruptedException {
        Residencial res = new Residencial();
        res.setTiempoAtencion(5);
        res.agregarResidente(new Residente("RS", "Socio de prueba", "1", true));

        GaritaEntrada[] garitas = new GaritaEntrada[3];
        for (int i = 0; i < garitas.length; i++) {
            garitas[i] = new GaritaEntrada(i + 1, res);
            garitas[i].start();
        }

        for (int i = 0; i < 80; i++) {
            res.getColaEntrada().encolar(new Vehiculo("S" + i, "M", "M", "Gris", "Carro", "RS"));
            res.getColaEntrada().encolar(new Vehiculo("V" + i, "M", "M", "Rojo", "Carro", null));
        }

        Thread.sleep(4000);
        res.getColaEntrada().detener();
        for (int i = 0; i < garitas.length; i++) {
            garitas[i].join();
        }

        Parqueo p = res.getParqueo();
        System.out.println("Libres socio: " + p.contarLibres("SOCIO")
                + " | general: " + p.contarLibres("GENERAL"));

        int rechazados = 0;
        for (int i = 0; i < res.getHistorial().getTamanio(); i++) {
            Evento e = res.getHistorial().obtener(i);
            if (e.getDescripcion().contains("rechazado")) {
                rechazados++;
            }
        }
        System.out.println("Rechazados: " + rechazados);
        System.out.println("Total de eventos: " + res.getHistorial().getTamanio());

        String[] placas = new String[150];
        int n = 0;
        int duplicados = 0;
        EspacioParqueo[] cabezas = {p.getCabezaSocios(), p.getCabezaGeneral()};
        for (int c = 0; c < cabezas.length; c++) {
            for (EspacioParqueo fila = cabezas[c]; fila != null; fila = fila.getAbajo()) {
                for (EspacioParqueo e = fila; e != null; e = e.getSiguiente()) {
                    Vehiculo v = e.getVehiculo();
                    if (v == null) {
                        continue;
                    }
                    for (int k = 0; k < n; k++) {
                        if (placas[k].equals(v.getPlaca())) {
                            duplicados++;
                        }
                    }
                    placas[n] = v.getPlaca();
                    n++;
                }
            }
        }
        System.out.println("Espacios ocupados: " + n + " | placas duplicadas: " + duplicados);
    }
}