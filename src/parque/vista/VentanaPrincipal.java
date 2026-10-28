package parque.vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import parque.hilos.GaritaEntrada;
import parque.hilos.GaritaSalida;
import parque.modelo.Residencial;

public class VentanaPrincipal extends JFrame{
    private final Residencial residencial;
    private final GaritaEntrada[] entradas;
    private final GaritaSalida[] salidas;
    private final JTabbedPane pestanas = new JTabbedPane();

    public VentanaPrincipal(Residencial residencial, GaritaEntrada[] entradas, GaritaSalida[] salidas) {
        super("Parque Residencial - Sistema de Gestion de Parqueo");
        this.residencial = residencial;
        this.entradas = entradas;
        this.salidas = salidas;

        pestanas.addTab("Parqueo", new PanelParqueo(residencial.getParqueo()));
         pestanas.addTab("Residentes", new PanelResidentes(residencial));
        pestanas.addTab("Vehiculos", new PanelVehiculos(residencial));
        pestanas.addTab("Entrada", new PanelEntrada(residencial, entradas));
        pestanas.addTab("Salida", new PanelSalida(residencial, salidas));
                pestanas.addTab("Eventos", new PanelEventos(residencial));

        add(pestanas, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override 
            public void windowClosing(WindowEvent e) {
                cerrar();
            }
        });

        Timer refresco = new Timer(500, e -> actualizarVisible());
        refresco.start();

        setSize(1100, 650);
        setLocationRelativeTo(null);
    }

    private JPanel marcador(String nombre) {
        JPanel p = new JPanel(new BorderLayout());
    p.add(new JLabel("Panel de " + nombre + "(pendiente)", SwingConstants.CENTER), BorderLayout.CENTER);
     return p;
    }

    private void actualizarVisible() {
        Component c = pestanas.getSelectedComponent();
        if ( c instanceof Actualizable) {
            ((Actualizable) c).actualizar();
        }
    }

    private void cerrar(){
        try{
            residencial.guardarDatos("data/residentes.txt", "data/vehiculos.txt");
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this,
             "No se pudieron guardar los datos: " + ex.getMessage(),
              "Erorr", JOptionPane.ERROR_MESSAGE);
    }
    residencial.getColaEntrada().detener();
    residencial.getColaSalida().detener();
    dispose();
    System.exit(0);
   }
}
