package parque;

import parque.modelo.EspacioParqueo;
import parque.modelo.Parqueo;
import parque.modelo.Vehiculo;

public class PruebaParqueo {
    public static void main(String[] args) {
        Parqueo p = new Parqueo();
        System.out.println("Total: " + p.getTotal());
        System.out.println("Libres socio: " + p.contarLibres("SOCIO")
                + " | general: " + p.contarLibres("GENERAL"));

        Vehiculo v1 = new Vehiculo("P111AAA", "Toyota", "Corolla", "Rojo", "Carro", "R1");
        Vehiculo v2 = new Vehiculo("P222BBB", "Honda", "Civic", "Azul", "Carro", "R2");

        System.out.println("v1 (socio) -> " + p.asignar(v1, true).getId());
        System.out.println("v2 (no socio) -> " + p.asignar(v2, false).getId());
        System.out.println("Estado v1: " + v1.getEstado());
        System.out.println("Libres socio: " + p.contarLibres("SOCIO")
                + " | general: " + p.contarLibres("GENERAL"));

        p.imprimirMapa();

        Vehiculo sale = p.liberar("P111AAA");
        System.out.println("Salio: " + sale + ", estado: " + sale.getEstado());
        System.out.println("Libres socio: " + p.contarLibres("SOCIO")
                + " | general: " + p.contarLibres("GENERAL"));

        // Llenar el area de socios (75 espacios)
        for (int i = 0; i < 75; i++) {
            p.asignar(new Vehiculo("S" + i, "Marca", "Modelo", "Gris", "Carro", "R3"), true);
        }
        System.out.println("Libres socio tras llenar: " + p.contarLibres("SOCIO"));

        // Un socio mas: A-C esta llena, debe pasar al area general
        EspacioParqueo e = p.asignar(new Vehiculo("SX", "Marca", "Modelo", "Gris", "Carro", "R3"), true);
        System.out.println("Socio con A-C llena -> " + e.getId());

        // Llenar el area general (quedan 73)
        for (int i = 0; i < 73; i++) {
            p.asignar(new Vehiculo("N" + i, "Marca", "Modelo", "Negro", "Carro", "R4"), false);
        }
        System.out.println("Libres general tras llenar: " + p.contarLibres("GENERAL"));

        EspacioParqueo r1 = p.asignar(new Vehiculo("NX", "Marca", "Modelo", "Negro", "Carro", "R4"), false);
        System.out.println("No socio con general llena: " + (r1 == null ? "RECHAZADO" : r1.getId()));

        EspacioParqueo r2 = p.asignar(new Vehiculo("SY", "Marca", "Modelo", "Gris", "Carro", "R3"), true);
        System.out.println("Socio con todo lleno: " + (r2 == null ? "RECHAZADO" : r2.getId()));
    }
}