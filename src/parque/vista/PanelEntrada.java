package parque.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import parque.hilos.GaritaEntrada;
import parque.modelo.Residencial;
import parque.modelo.Vehiculo;

public class PanelEntrada extends JPanel implements Actualizable {
    private static final Color LIBRE = new Color(235, 235, 235);
    private static final Color OCUPADA = new Color(255, 236, 170);

    private final Residencial residencial;
    private final GaritaEntrada[] garitas;
    private final JLabel[] etiquetasGaritas;

    private final JTextField txtPlacaResidente = new JTextField(9);
    private final JTextField txtPlaca = new JTextField(9);
    private final JTextField txtMarca = new JTextField(9);
    private final JTextField txtModelo = new JTextField(9);
    private final JTextField txtColor = new JTextField(8);
    private final JComboBox<String> cmbTipo =
            new JComboBox<>(new String[] {"Carro", "Moto", "Camioneta", "Camion"});
    private final JLabel lblCola = new JLabel(" ");
    private final DefaultTableModel tablaModelo = new DefaultTableModel(
            new String[] {"#", "Placa", "Vehiculo", "Tipo", "Origen"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(tablaModelo);

    public PanelEntrada(Residencial residencial, GaritaEntrada[] garitas) {
        this.residencial = residencial;
        this.garitas = garitas;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel filaResidente = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaResidente.setBorder(BorderFactory.createTitledBorder(
                "Vehiculo de un residente"));
        JButton btnResidente = new JButton("Enviar a la cola de entrada");
        btnResidente.addActionListener(e -> encolarResidente());
        filaResidente.add(new JLabel("Placa registrada:"));
        filaResidente.add(txtPlacaResidente);
        filaResidente.add(btnResidente);

        JPanel filaVisitante = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaVisitante.setBorder(BorderFactory.createTitledBorder(
                "Visitante (dato temporal, no se guarda en archivos)"));
        JButton btnVisitante = new JButton("Registrar visitante y enviar a la cola");
        btnVisitante.addActionListener(e -> registrarVisitante());
        filaVisitante.add(new JLabel("Placa:"));
        filaVisitante.add(txtPlaca);
        filaVisitante.add(new JLabel("Marca:"));
        filaVisitante.add(txtMarca);
        filaVisitante.add(new JLabel("Modelo:"));
        filaVisitante.add(txtModelo);
        filaVisitante.add(new JLabel("Color:"));
        filaVisitante.add(txtColor);
        filaVisitante.add(cmbTipo);
        filaVisitante.add(btnVisitante);

        JPanel norte = new JPanel(new GridLayout(2, 1));
        norte.add(filaResidente);
        norte.add(filaVisitante);
        add(norte, BorderLayout.NORTH);

        JPanel colaPanel = new JPanel(new BorderLayout(4, 4));
        colaPanel.setBorder(BorderFactory.createTitledBorder(
                "Cola de entrada (el primero de la lista es el siguiente en ser atendido)"));
        colaPanel.add(lblCola, BorderLayout.NORTH);
        colaPanel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel garitasPanel = new JPanel(new GridLayout(garitas.length, 1, 8, 8));
        garitasPanel.setBorder(BorderFactory.createTitledBorder("Garitas de entrada"));
        etiquetasGaritas = new JLabel[garitas.length];
        for (int i = 0; i < garitas.length; i++) {
            JLabel etiqueta = new JLabel(" ", SwingConstants.CENTER);
            etiqueta.setOpaque(true);
            etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 14));
            etiqueta.setBorder(BorderFactory.createTitledBorder(
                    "Garita E" + garitas[i].getNumero()));
            etiquetasGaritas[i] = etiqueta;
            garitasPanel.add(etiqueta);
        }

        JPanel centro = new JPanel(new GridLayout(1, 2, 8, 8));
        centro.add(colaPanel);
        centro.add(garitasPanel);
        add(centro, BorderLayout.CENTER);

        actualizar();
    }

    private void encolarResidente() {
        String placa = txtPlacaResidente.getText().trim();
        if (placa.isEmpty()) {
            mensaje("Escribe la placa del vehiculo.");
            return;
        }
        Vehiculo v = residencial.buscarVehiculoResidente(placa);
        if (v == null) {
            mensaje("No hay ningun vehiculo de residente con la placa " + placa);
            return;
        }
        if (!"AFUERA".equals(v.getEstado())) {
            mensaje("El vehiculo " + placa + " ya esta en la cola o estacionado.");
            return;
        }
        enviar(v);
        txtPlacaResidente.setText("");
    }

    private void registrarVisitante() {
        String placa = txtPlaca.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String color = txtColor.getText().trim();
        if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || color.isEmpty()) {
            mensaje("Todos los campos del visitante son obligatorios.");
            return;
        }
        if (residencial.placaEnUso(placa)) {
            mensaje("La placa " + placa + " ya esta registrada, en cola o estacionada.");
            return;
        }
        Vehiculo v = new Vehiculo(placa, marca, modelo, color,
                (String) cmbTipo.getSelectedItem(), null);
        residencial.getHistorial().registrar("Visitante registrado: " + placa);
        enviar(v);
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtColor.setText("");
        cmbTipo.setSelectedIndex(0);
    }

    private void enviar(Vehiculo v) {
        v.setEstado("EN_COLA");
        residencial.getColaEntrada().encolar(v);
        residencial.getHistorial().registrar(
                "Vehiculo " + v.getPlaca() + " llega a la cola de entrada");
        actualizar();
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    @Override
    public void actualizar() {
        tablaModelo.setRowCount(0);
        int n = residencial.getColaEntrada().getTamanio();
        for (int i = 0; i < n; i++) {
            Vehiculo v = residencial.getColaEntrada().ver(i);
            if (v == null) {
                break;
            }
            tablaModelo.addRow(new Object[] {i + 1, v.getPlaca(),
                v.getMarca() + " " + v.getModelo(), v.getTipo(),
                v.esVisitante() ? "Visitante" : "Residente " + v.getIdResidente()});
        }
        lblCola.setText("Vehiculos en cola: " + n);

        for (int i = 0; i < garitas.length; i++) {
            Vehiculo v = garitas[i].getVehiculoActual();
            if (v == null) {
                etiquetasGaritas[i].setText("Libre, esperando vehiculos");
                etiquetasGaritas[i].setBackground(LIBRE);
            } else {
                etiquetasGaritas[i].setText("Atendiendo: " + v + " - "
                        + (v.esVisitante() ? "visitante" : "residente " + v.getIdResidente()));
                etiquetasGaritas[i].setBackground(OCUPADA);
            }
        }
    }
}