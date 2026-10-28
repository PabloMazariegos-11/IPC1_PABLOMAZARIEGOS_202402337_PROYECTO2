package parque.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import parque.modelo.Evento;
import parque.modelo.Residencial;

public class PanelEventos extends JPanel implements Actualizable {
    private final Residencial residencial;
    private final JComboBox<String> cmbCantidad = new JComboBox<>(
            new String[] {"Ultimos 10", "Ultimos 25", "Ultimos 50", "Todos"});
    private final JLabel lblTotal = new JLabel(" ");
    private final DefaultTableModel tablaModelo = new DefaultTableModel(
            new String[] {"#", "Hora", "Evento"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(tablaModelo);
    private int tamanioMostrado = -1;
    private int limiteMostrado = -1;

    public PanelEventos(Residencial residencial) {
        this.residencial = residencial;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        norte.add(new JLabel("Mostrar:"));
        norte.add(cmbCantidad);
        norte.add(lblTotal);
        add(norte, BorderLayout.NORTH);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(700);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        cmbCantidad.addActionListener(e -> actualizar());
        actualizar();
    }

    private int limite() {
        switch (cmbCantidad.getSelectedIndex()) {
            case 0:
                return 10;
            case 1:
                return 25;
            case 2:
                return 50;
            default:
                return Integer.MAX_VALUE;
        }
    }

    @Override
    public void actualizar() {
        int total = residencial.getHistorial().getTamanio();
        int limite = limite();
        lblTotal.setText("Total de eventos registrados: " + total
                + "   (el mas reciente aparece primero)");

        if (total == tamanioMostrado && limite == limiteMostrado) {
            return;
        }

        tablaModelo.setRowCount(0);
        int cantidad = Math.min(total, limite);
        for (int i = 0; i < cantidad; i++) {
            Evento e = residencial.getHistorial().obtener(i);
            if (e == null) {
                break;
            }
            tablaModelo.addRow(new Object[] {total - i, e.getHora(), e.getDescripcion()});
        }
        tamanioMostrado = total;
        limiteMostrado = limite;
    }
}