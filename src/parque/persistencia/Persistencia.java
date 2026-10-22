package parque.persistencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import parque.estructuras.ListaDoble;
import parque.modelo.Residente;
import parque.modelo.Vehiculo;

public class Persistencia {

    public static void guardar(ListaDoble<Residente> residentes,
                               String rutaResidentes, String rutaVehiculos) throws IOException {
        try (PrintWriter pr = new PrintWriter(new FileWriter(rutaResidentes));
             PrintWriter pv = new PrintWriter(new FileWriter(rutaVehiculos))) {

            for (int i = 0; i < residentes.getTamanio(); i++) {
                Residente r = residentes.obtener(i);
                pr.println(r.getId() + "|" + r.getNombre() + "|" + r.getCasa() + "|" + r.isSocio());

                for (int j = 0; j < r.getVehiculos().getTamanio(); j++) {
                    Vehiculo v = r.getVehiculos().obtener(j);
                    pv.println(v.getPlaca() + "|" + v.getMarca() + "|" + v.getModelo() + "|"
                            + v.getColor() + "|" + v.getTipo() + "|" + r.getId());
                }
            }
        }
    }

    public static ListaDoble<Residente> cargar(String rutaResidentes,
                                               String rutaVehiculos) throws IOException {
        ListaDoble<Residente> residentes = new ListaDoble<>();

        File archivoResidentes = new File(rutaResidentes);
        if (archivoResidentes.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivoResidentes))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) {
                        continue;
                    }
                    String[] p = linea.split("\\|");
                    if (p.length < 4) {
                        continue;
                    }
                    residentes.insertarAlFinal(
                            new Residente(p[0], p[1], p[2], Boolean.parseBoolean(p[3])));
                }
            }
        }

        File archivoVehiculos = new File(rutaVehiculos);
        if (archivoVehiculos.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivoVehiculos))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) {
                        continue;
                    }
                    String[] p = linea.split("\\|");
                    if (p.length < 6) {
                        continue;
                    }
                    Residente dueno = residentes.buscar(new Residente(p[5], "", "", false));
                    if (dueno != null) {
                        dueno.agregarVehiculo(new Vehiculo(p[0], p[1], p[2], p[3], p[4], p[5]));
                    }
                }
            }
        }
        return residentes;
    }
}