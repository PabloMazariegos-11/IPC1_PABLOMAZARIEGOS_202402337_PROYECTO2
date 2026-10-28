package parque;

import java.io.IOException;

import javax.swing.SwingUtilities;

import parque.hilos.GaritaEntrada;
import parque.hilos.GaritaSalida;
import parque.modelo.Residencial;
import parque.vista.VentanaPrincipal;

public class Main{
    public static void main(String[] args) {
        Residencial residencial = new Residencial();
        try {
            residencial.cargarDatos("data/residentes.txt", "data/vehiculos.txt");
        } catch (IOException e) {
            System.out.println("No se pudieron cargar los datos: " + e.getMessage());
        }
        GaritaEntrada[] entradas = new GaritaEntrada[3];
        for (int i = 0; i < entradas.length; i++) {
            entradas[i] = new GaritaEntrada(i + 1, residencial);
            entradas[i].start();
        }
        GaritaSalida[] salidas = new GaritaSalida[2];
        for (int i = 0; i < salidas.length; i++){
            salidas[i] = new GaritaSalida(i + 1, residencial);
            salidas[i].start();
        }

        SwingUtilities.invokeLater(
        () -> new VentanaPrincipal(residencial, entradas, salidas).setVisible(true));
    }
}