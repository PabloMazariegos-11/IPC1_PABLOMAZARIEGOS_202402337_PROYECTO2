package parque.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import parque.estructuras.ListaDoble;
import parque.modelo.Residencial;
import parque.modelo.Residente;
import parque.modelo.Vehiculo;

public class PanelVehiculos extends JPanel implements Actualizable {
    private final Residencial residencial;
    private final JTextField txtResidente = new JTextField(8);
    private final JTextField txtPlaca = new JTextField(9);
    private final JTextField txtMarca = new JTextField(9);
    private final JTextField txtModelo = new JTextField(9);
    private final JTextField txtColor = new JTextField(8);
    private final JComboBox<String> cmbTipo =
            new JComboBox<>(new String[] {"Carro", "Moto", "Camioneta", "Camion"});
    private final JLabel info = new JLabel(" ");
    private final DefaultTableModel tablaModelo = new DefaultTableModel(
            new String[] {"Placa", "Marca", "Modelo", "Color", "Tipo", "Estado",
                "Residente", "Socio"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(tablaModelo);

    public PanelVehiculos(Residencial residencial) {
        this.residencial = residencial;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del vehiculo"));
        formulario.add(new JLabel("ID residente:"));
        formulario.add(txtResidente);
        formulario.add(new JLabel("Placa:"));
        formulario.add(txtPlaca);
        formulario.add(new JLabel("Marca:"));
        formulario.add(txtMarca);
        formulario.add(new JLabel("Modelo:"));
        formulario.add(txtModelo);
        formulario.add(new JLabel("Color:"));
        formulario.add(txtColor);
        formulario.add(new JLabel("Tipo:"));
        formulario.add(cmbTipo);

        JButton btnRegistrar = new JButton("Registrar vehiculo");
        JButton btnEliminar = new JButton("Eliminar vehiculo");
        JButton btnLimpiar = new JButton("Limpiar");
        btnRegistrar.addActionListener(e -> registrar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnRegistrar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel(new GridLayout(3, 1));
        norte.add(formulario);
        norte.add(botones);
        norte.add(info);
        add(norte, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    txtPlaca.setText((String) tablaModelo.getValueAt(fila, 0));
                    txtMarca.setText((String) tablaModelo.getValueAt(fila, 1));
                    txtModelo.setText((String) tablaModelo.getValueAt(fila, 2));
                    txtColor.setText((String) tablaModelo.getValueAt(fila, 3));
                    cmbTipo.setSelectedItem(tablaModelo.getValueAt(fila, 4));
                    txtResidente.setText((String) tablaModelo.getValueAt(fila, 6));
                }
            }
        });

        refrescar();
    }

    private void registrar() {
        String idResidente = txtResidente.getText().trim();
        String placa = txtPlaca.getText().trim();
        String marca = txtMarca.getText().trim();
        String modeloTxt = txtModelo.getText().trim();
        String color = txtColor.getText().trim();

        if (idResidente.isEmpty() || placa.isEmpty() || marca.isEmpty()
                || modeloTxt.isEmpty() || color.isEmpty()) {
            mensaje("Todos los campos son obligatorios.");
            return;
        }
        if (placa.contains("|") || marca.contains("|") || modeloTxt.contains("|")
                || color.contains("|")) {
            mensaje("No se permite el caracter | en los datos.");
            return;
        }
        Residente r = residencial.buscarResidente(idResidente);
        if (r == null) {
            mensaje("No existe un residente con el ID " + idResidente);
            return;
        }
        if (!r.puedeAgregarVehiculo()) {
            mensaje(r.getNombre() + " ya tiene " + Residente.MAX_VEHICULOS
                    + " vehiculos. No se puede registrar uno mas.");
            return;
        }
        if (duenoDe(placa) != null) {
            mensaje("Ya existe un vehiculo con la placa " + placa);
            return;
        }
        r.agregarVehiculo(new Vehiculo(placa, marca, modeloTxt, color,
                (String) cmbTipo.getSelectedItem(), idResidente));
        limpiarVehiculo();
        refrescar();
    }

    private void eliminar() {
        String placa = txtPlaca.getText().trim();
        Residente dueno = duenoDe(placa);
        if (dueno == null) {
            mensaje("No existe un vehiculo con la placa " + placa);
            return;
        }
        Vehiculo v = dueno.buscarVehiculo(placa);
        if (!"AFUERA".equals(v.getEstado())) {
            mensaje("No se puede eliminar: el vehiculo esta en el parqueo o en una cola.");
            return;
        }
        dueno.eliminarVehiculo(placa);
        limpiarVehiculo();
        refrescar();
    }

    private Residente duenoDe(String placa) {
        ListaDoble<Residente> lista = residencial.getResidentes();
        for (int i = 0; i < lista.getTamanio(); i++) {
            Residente r = lista.obtener(i);
            if (r.buscarVehiculo(placa) != null) {
                return r;
            }
        }
        return null;
    }

    private void limpiarVehiculo() {
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtColor.setText("");
        cmbTipo.setSelectedIndex(0);
        tabla.clearSelection();
    }

    private void limpiar() {
        txtResidente.setText("");
        limpiarVehiculo();
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    private void refrescar() {
        int seleccionada = tabla.getSelectedRow();
        tablaModelo.setRowCount(0);

        String filtro = txtResidente.getText().trim();
        Residente filtrado = filtro.isEmpty() ? null : residencial.buscarResidente(filtro);

        ListaDoble<Residente> lista = residencial.getResidentes();
        for (int i = 0; i < lista.getTamanio(); i++) {
            Residente r = lista.obtener(i);
            if (filtrado != null && r != filtrado) {
                continue;
            }
            for (int j = 0; j < r.getVehiculos().getTamanio(); j++) {
                Vehiculo v = r.getVehiculos().obtener(j);
                tablaModelo.addRow(new Object[] {v.getPlaca(), v.getMarca(), v.getModelo(),
                    v.getColor(), v.getTipo(), v.getEstado(), r.getId(),
                    r.isSocio() ? "Si" : "No"});
            }
        }

        if (filtrado != null) {
            info.setText(filtrado.getNombre() + " - "
                    + (filtrado.isSocio() ? "SOCIO (acceso al area A-C)"
                            : "NO SOCIO (solo area general)")
                    + " - " + filtrado.getVehiculos().getTamanio() + "/"
                    + Residente.MAX_VEHICULOS + " vehiculos");
        } else if (filtro.isEmpty()) {
            info.setText("Mostrando los vehiculos de todos los residentes. "
                    + "Escribe un ID de residente para filtrar.");
        } else {
            info.setText("No existe el residente " + filtro + ". Mostrando todos.");
        }

        if (seleccionada >= 0 && seleccionada < tablaModelo.getRowCount()) {
            tabla.setRowSelectionInterval(seleccionada, seleccionada);
        }
    }

    @Override
    public void actualizar() {
        refrescar();
    }
}