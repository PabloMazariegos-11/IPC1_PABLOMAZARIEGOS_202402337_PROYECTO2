package parque;

import java.io.IOException;

import parque.estructuras.ListaDoble;
import parque.modelo.Residente;
import parque.modelo.Vehiculo;
import parque.persistencia.Persistencia;

public class PruebaModelo {
    public static void main(String[] args) throws IOException {
        ListaDoble<Residente> residentes = new ListaDoble<>();
        Residente ana = new Residente("R1", "Ana Lopez", "12", true);
        Residente luis = new Residente("R2", "Luis Perez", "7", false);
        residentes.insertarAlFinal(ana);
        residentes.insertarAlFinal(luis);

        System.out.println("V1: " + ana.agregarVehiculo(
                new Vehiculo("P111AAA", "Toyota", "Corolla", "Rojo", "Carro", "R1")));
        System.out.println("Placa repetida: " + ana.agregarVehiculo(
                new Vehiculo("P111AAA", "Toyota", "Yaris", "Gris", "Carro", "R1")));
        System.out.println("V2: " + ana.agregarVehiculo(
                new Vehiculo("P222BBB", "Honda", "Civic", "Azul", "Carro", "R1")));
        System.out.println("V3: " + ana.agregarVehiculo(
                new Vehiculo("P333CCC", "Yamaha", "FZ", "Negro", "Moto", "R1")));
        System.out.println("Cuarto vehiculo: " + ana.agregarVehiculo(
                new Vehiculo("P444DDD", "Kia", "Rio", "Blanco", "Carro", "R1")));
        System.out.println("V1 de Luis: " + luis.agregarVehiculo(
                new Vehiculo("P555EEE", "Suzuki", "Swift", "Verde", "Carro", "R2")));

        System.out.println("-- originales --");
        imprimir(residentes);

        Persistencia.guardar(residentes, "data/residentes.txt", "data/vehiculos.txt");
        ListaDoble<Residente> cargados =
                Persistencia.cargar("data/residentes.txt", "data/vehiculos.txt");

        System.out.println("-- cargados desde los archivos --");
        imprimir(cargados);

        Residente r = cargados.buscar(new Residente("R2", "", "", false));
        r.setSocio(true);
        System.out.println("R2 actualizado: " + r);
        System.out.println("Eliminar vehiculo P555EEE: " + r.eliminarVehiculo("P555EEE"));
        System.out.println("Eliminar residente R1: "
                + cargados.eliminar(new Residente("R1", "", "", false)));
        System.out.println("Residentes que quedan: " + cargados.getTamanio());
    }

    private static void imprimir(ListaDoble<Residente> lista) {
        for (int i = 0; i < lista.getTamanio(); i++) {
            Residente r = lista.obtener(i);
            System.out.println(r);
            for (int j = 0; j < r.getVehiculos().getTamanio(); j++) {
                System.out.println("   - " + r.getVehiculos().obtener(j));
            }
        }
    }
}