package parque;

import parque.hilos.GaritaEntrada;
import parque.hilos.GaritaSalida;
import parque.modelo.Evento;
import parque.modelo.Residencial;
import parque.modelo.Residente;
import parque.modelo.Vehiculo;

public class PruebaGaritas {
    public static void main(String[] args) throws InterruptedException {
        Residencial res = new Residencial();
        res.setTiempoAtencion(300);
        res.agregarResidente(new Residente("R1", "Ana Lopez", "12", true));
        res.agregarResidente(new Residente("R2", "Luis Perez", "7", false));

        GaritaEntrada[] entradas = new GaritaEntrada[3];
        for (int i = 0; i < entradas.length; i++) {
            entradas[i] = new GaritaEntrada(i + 1, res);
            entradas[i].start();
        }
        GaritaSalida[] salidas = new GaritaSalida[2];
        for (int i = 0; i < salidas.length; i++) {
            salidas[i] = new GaritaSalida(i + 1, res);
            salidas[i].start();
        }

        Vehiculo[] autos = {
            new Vehiculo("S1", "Toyota", "Corolla", "Rojo", "Carro", "R1"),
            new Vehiculo("S2", "Honda", "Civic", "Azul", "Carro", "R1"),
            new Vehiculo("S3", "Kia", "Rio", "Blanco", "Carro", "R1"),
            new Vehiculo("N1", "Suzuki", "Swift", "Verde", "Carro", "R2"),
            new Vehiculo("N2", "Yamaha", "FZ", "Negro", "Moto", "R2"),
            new Vehiculo("V1", "Mazda", "3", "Gris", "Carro", null),
            new Vehiculo("V2", "Ford", "Fiesta", "Rojo", "Carro", null),
            new Vehiculo("X1", "Fiat", "Uno", "Azul", "Carro", "R9")
        };
        for (int i = 0; i < autos.length; i++) {
            autos[i].setEstado("EN_COLA");
            res.getColaEntrada().encolar(autos[i]);
        }

        Thread.sleep(3000);
        System.out.println("=== Despues de las entradas ===");
        System.out.println("Libres socio: " + res.getParqueo().contarLibres("SOCIO")
                + " | general: " + res.getParqueo().contarLibres("GENERAL"));

        res.getColaSalida().encolar(autos[0]);
        res.getColaSalida().encolar(autos[3]);
        res.getColaSalida().encolar(autos[5]);
        res.getColaSalida().encolar(autos[7]);

        Thread.sleep(2000);
        System.out.println("=== Despues de las salidas ===");
        System.out.println("Libres socio: " + res.getParqueo().contarLibres("SOCIO")
                + " | general: " + res.getParqueo().contarLibres("GENERAL"));

        res.getColaEntrada().detener();
        res.getColaSalida().detener();
        for (int i = 0; i < entradas.length; i++) {
            entradas[i].join();
        }
        for (int i = 0; i < salidas.length; i++) {
            salidas[i].join();
        }

        System.out.println("=== Historial (del mas antiguo al mas reciente) ===");
        for (int i = res.getHistorial().getTamanio() - 1; i >= 0; i--) {
            Evento e = res.getHistorial().obtener(i);
            System.out.println(e);
        }
        System.out.println("Total de eventos: " + res.getHistorial().getTamanio());
    }
}