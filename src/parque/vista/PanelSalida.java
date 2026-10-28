package parque.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import parque.hilos.GaritaSalida;
import parque.modelo.EspacioParqueo;
import parque.modelo.Residencial;
import parque.modelo.Vehiculo;

public class PanelSalida extends JPanel implements Actualizable {
    private static final Color LIBRE = new Color(235, 235, 235);
    private static final Color OCUPADA = new Color(255, 236, 170);

    private final Residencial residencial;
    private final GaritaSalida[] garitas;
    private final JLabel[] etiquetasGaritas;
    private final JLabel lblEstacionados = new JLabel(" ");
    private final JLabel lblCola = new JLabel(" ");
        private final Vehiculo[] vehiculosFilas = new Vehiculo[150];

    private final DefaultTableModel modeloEstacionados = new DefaultTableModel(
            new String[] {"Espacio", "Placa", "Vehiculo", "Origen", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tablaEstacionados = new JTable(modeloEstacionados);

    private final DefaultTableModel modeloCola = new DefaultTableModel(
            new String[] {"#", "Placa", "Vehiculo"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tablaCola = new JTable(modeloCola);

    public PanelSalida(Residencial residencial, GaritaSalida[] garitas) {
        this.residencial = residencial;
        this.garitas = garitas;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton btnSalida = new JButton("Solicitar salida del vehiculo seleccionado");
        btnSalida.addActionListener(e -> solicitarSalida());
        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        norte.add(btnSalida);
        add(norte, BorderLayout.NORTH);

        JPanel izquierda = new JPanel(new BorderLayout(4, 4));
        izquierda.setBorder(BorderFactory.createTitledBorder(
                "Vehiculos estacionados (selecciona uno)"));
        izquierda.add(lblEstacionados, BorderLayout.NORTH);
        izquierda.add(new JScrollPane(tablaEstacionados), BorderLayout.CENTER);

        JPanel colaPanel = new JPanel(new BorderLayout(4, 4));
        colaPanel.setBorder(BorderFactory.createTitledBorder("Cola de salida"));
        colaPanel.add(lblCola, BorderLayout.NORTH);
        colaPanel.add(new JScrollPane(tablaCola), BorderLayout.CENTER);

        JPanel garitasPanel = new JPanel(new GridLayout(garitas.length, 1, 8, 8));
        garitasPanel.setBorder(BorderFactory.createTitledBorder(
                "Garitas de salida (vehiculos en proceso)"));
        etiquetasGaritas = new JLabel[garitas.length];
        for (int i = 0; i < garitas.length; i++) {
            JLabel etiqueta = new JLabel(" ", SwingConstants.CENTER);
            etiqueta.setOpaque(true);
            etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 14));
            etiqueta.setBorder(BorderFactory.createTitledBorder(
                    "Garita S" + garitas[i].getNumero()));
            etiquetasGaritas[i] = etiqueta;
            garitasPanel.add(etiqueta);
        }

        JPanel derecha = new JPanel(new GridLayout(2, 1, 8, 8));
        derecha.add(colaPanel);
        derecha.add(garitasPanel);

        JPanel centro = new JPanel(new GridLayout(1, 2, 8, 8));
        centro.add(izquierda);
        centro.add(derecha);
        add(centro, BorderLayout.CENTER);

        actualizar();
    }

    private void solicitarSalida() {
        int fila = tablaEstacionados.getSelectedRow();
        if (fila < 0) {
            mensaje("Selecciona un vehiculo de la lista de estacionados.");
            return;
        }
               Vehiculo v = vehiculosFilas[fila];
        String placa = (String) modeloEstacionados.getValueAt(fila, 1);
        if (v == null) {
            mensaje("El vehiculo ya no esta estacionado.");
            return;
        }
        if (!"ESTACIONADO".equals(v.getEstado())) {
            mensaje("El vehiculo " + placa + " ya solicito su salida.");
            return;
        }
        v.setEstado("SALIENDO");
        residencial.getColaSalida().encolar(v);
        residencial.getHistorial().registrar("Vehiculo " + placa + " solicita salida");
        actualizar();
    }

    private void agregarEstacionados(EspacioParqueo cabeza) {
        EspacioParqueo fila = cabeza;
        while (fila != null) {
            EspacioParqueo e = fila;
            while (e != null) {
                Vehiculo v = e.getVehiculo();
                if (v != null) {
                                        vehiculosFilas[modeloEstacionados.getRowCount()] = v;
                    modeloEstacionados.addRow(new Object[] {e.getId(), v.getPlaca(),
                        v.getMarca() + " " + v.getModelo(),
                        v.esVisitante() ? "Visitante" : "Residente " + v.getIdResidente(),
                        v.getEstado()});
                }
                e = e.getSiguiente();
            }
            fila = fila.getAbajo();
        }
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    @Override
    public void actualizar() {
        String seleccionada = null;
        int fila = tablaEstacionados.getSelectedRow();
        if (fila >= 0 && fila < modeloEstacionados.getRowCount()) {
            seleccionada = (String) modeloEstacionados.getValueAt(fila, 1);
        }

        modeloEstacionados.setRowCount(0);
        agregarEstacionados(residencial.getParqueo().getCabezaSocios());
        agregarEstacionados(residencial.getParqueo().getCabezaGeneral());
        lblEstacionados.setText("Vehiculos estacionados: " + modeloEstacionados.getRowCount());

        if (seleccionada != null) {
            for (int i = 0; i < modeloEstacionados.getRowCount(); i++) {
                if (seleccionada.equals(modeloEstacionados.getValueAt(i, 1))) {
                    tablaEstacionados.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }

        modeloCola.setRowCount(0);
        int n = residencial.getColaSalida().getTamanio();
        for (int i = 0; i < n; i++) {
            Vehiculo v = residencial.getColaSalida().ver(i);
            if (v == null) {
                break;
            }
            modeloCola.addRow(new Object[] {i + 1, v.getPlaca(),
                v.getMarca() + " " + v.getModelo()});
        }
        lblCola.setText("Vehiculos en cola de salida: " + n);

        for (int i = 0; i < garitas.length; i++) {
            Vehiculo v = garitas[i].getVehiculoActual();
            if (v == null) {
                etiquetasGaritas[i].setText("Libre, esperando vehiculos");
                etiquetasGaritas[i].setBackground(LIBRE);
            } else {
                etiquetasGaritas[i].setText("Atendiendo salida de: " + v);
                etiquetasGaritas[i].setBackground(OCUPADA);
            }
        }
    }
}