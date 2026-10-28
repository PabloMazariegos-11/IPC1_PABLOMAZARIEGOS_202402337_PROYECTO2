package parque.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

public class PanelResidentes extends JPanel implements Actualizable {
    private final Residencial residencial;
    private final JTextField txtId = new JTextField(10);
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtCasa = new JTextField(8);
    private final JCheckBox chkSocio = new JCheckBox("Es socio");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID", "Nombre", "Casa", "Socio", "Vehiculos"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public PanelResidentes(Residencial residencial) {
        this.residencial = residencial;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del residente"));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Casa/Lote:"));
        formulario.add(txtCasa);
        formulario.add(chkSocio);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar (ID o nombre)");
        JButton btnLimpiar = new JButton("Limpiar");
        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnBuscar.addActionListener(e -> buscar());
        btnLimpiar.addActionListener(e -> limpiar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnRegistrar);
        botones.add(btnModificar);
        botones.add(btnEliminar);
        botones.add(btnBuscar);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel(new GridLayout(2, 1));
        norte.add(formulario);
        norte.add(botones);
        add(norte, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    String id = (String) modelo.getValueAt(fila, 0);
                    Residente r = residencial.buscarResidente(id);
                    if (r != null) {
                        cargarFormulario(r);
                    }
                }
            }
        });

        refrescar();
    }

    private void registrar() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        String casa = txtCasa.getText().trim();
        String error = validar(id, nombre, casa);
        if (error != null) {
            mensaje(error);
            return;
        }
        if (!residencial.agregarResidente(new Residente(id, nombre, casa, chkSocio.isSelected()))) {
            mensaje("Ya existe un residente con el ID " + id);
            return;
        }
        limpiar();
        refrescar();
    }

    private void modificar() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        String casa = txtCasa.getText().trim();
        String error = validar(id, nombre, casa);
        if (error != null) {
            mensaje(error);
            return;
        }
        Residente r = residencial.buscarResidente(id);
        if (r == null) {
            mensaje("No existe un residente con el ID " + id);
            return;
        }
        r.setNombre(nombre);
        r.setCasa(casa);
        r.setSocio(chkSocio.isSelected());
        refrescar();
    }

    private void eliminar() {
        String id = txtId.getText().trim();
        Residente r = residencial.buscarResidente(id);
        if (r == null) {
            mensaje("No existe un residente con el ID " + id);
            return;
        }
        for (int i = 0; i < r.getVehiculos().getTamanio(); i++) {
            Vehiculo v = r.getVehiculos().obtener(i);
            if (!"AFUERA".equals(v.getEstado())) {
                mensaje("No se puede eliminar: el vehiculo " + v.getPlaca()
                        + " esta en el parqueo o en una cola.");
                return;
            }
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "Eliminar a " + r.getNombre() + " y todos sus vehiculos?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        residencial.eliminarResidente(id);
        limpiar();
        refrescar();
    }

    private void buscar() {
        String texto = txtId.getText().trim();
        if (texto.isEmpty()) {
            texto = txtNombre.getText().trim();
        }
        if (texto.isEmpty()) {
            mensaje("Escribe un ID o un nombre para buscar.");
            return;
        }
        Residente r = encontrar(texto);
        if (r == null) {
            mensaje("No se encontro ningun residente con: " + texto);
            return;
        }
        cargarFormulario(r);
    }

    private Residente encontrar(String texto) {
        Residente r = residencial.buscarResidente(texto);
        if (r != null) {
            return r;
        }
        ListaDoble<Residente> lista = residencial.getResidentes();
        for (int i = 0; i < lista.getTamanio(); i++) {
            Residente candidato = lista.obtener(i);
            if (candidato.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                return candidato;
            }
        }
        return null;
    }

    private String validar(String id, String nombre, String casa) {
        if (id.isEmpty() || nombre.isEmpty() || casa.isEmpty()) {
            return "Todos los campos son obligatorios.";
        }
        if (id.contains("|") || nombre.contains("|") || casa.contains("|")) {
            return "No se permite el caracter | en los datos.";
        }
        return null;
    }

    private void cargarFormulario(Residente r) {
        txtId.setText(r.getId());
        txtNombre.setText(r.getNombre());
        txtCasa.setText(r.getCasa());
        chkSocio.setSelected(r.isSocio());
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtCasa.setText("");
        chkSocio.setSelected(false);
        tabla.clearSelection();
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    private void refrescar() {
        int seleccionada = tabla.getSelectedRow();
        modelo.setRowCount(0);
        ListaDoble<Residente> lista = residencial.getResidentes();
        for (int i = 0; i < lista.getTamanio(); i++) {
            Residente r = lista.obtener(i);
            modelo.addRow(new Object[] {r.getId(), r.getNombre(), r.getCasa(),
                r.isSocio() ? "Si" : "No", r.getVehiculos().getTamanio()});
        }
        if (seleccionada >= 0 && seleccionada < modelo.getRowCount()) {
            tabla.setRowSelectionInterval(seleccionada, seleccionada);
        }
    }

    @Override
    public void actualizar() {
        refrescar();
    }
}