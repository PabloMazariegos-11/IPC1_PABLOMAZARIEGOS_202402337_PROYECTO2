package parque.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import parque.modelo.EspacioParqueo;
import parque.modelo.Parqueo;
import parque.modelo.Vehiculo;

public class PanelParqueo extends JPanel implements Actualizable {
    private static final Color LIBRE = new Color(198, 239, 206);
    private static final Color OCUPADO = new Color(255, 160, 160);

    private final EspacioParqueo[] espacios = new EspacioParqueo[150];
    private final JLabel[] celdas = new JLabel[150];
    private final JLabel resumen = new JLabel(" ", SwingConstants.CENTER);
    private int cantidad = 0;

    public PanelParqueo(Parqueo parqueo) {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(resumen, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 1, 8, 8));
        centro.add(crearArea("Area de socios (A-C)", parqueo.getCabezaSocios(), 3, 25));
        centro.add(crearArea("Area general / visitantes (E-I)",
                parqueo.getCabezaGeneral(), 5, 15));
        add(centro, BorderLayout.CENTER);

        actualizar();
    }

    private JPanel crearArea(String titulo, EspacioParqueo cabeza, int filas, int columnas) {
        JPanel area = new JPanel(new GridLayout(filas, columnas, 2, 2));
        area.setBorder(BorderFactory.createTitledBorder(titulo));

        EspacioParqueo fila = cabeza;
        while (fila != null) {
            EspacioParqueo e = fila;
            while (e != null) {
                JLabel celda = new JLabel(e.getId(), SwingConstants.CENTER);
                celda.setOpaque(true);
                celda.setFont(new Font("SansSerif", Font.PLAIN, 9));
                celda.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                espacios[cantidad] = e;
                celdas[cantidad] = celda;
                cantidad++;
                area.add(celda);
                e = e.getSiguiente();
            }
            fila = fila.getAbajo();
        }
        return area;
    }

    @Override
    public void actualizar() {
        int ocupados = 0;
        for (int i = 0; i < cantidad; i++) {
            Vehiculo v = espacios[i].getVehiculo();
            JLabel celda = celdas[i];
            if (v == null) {
                celda.setBackground(LIBRE);
                celda.setText(espacios[i].getId());
                celda.setToolTipText(espacios[i].getId() + " - libre");
            } else {
                ocupados++;
                celda.setBackground(OCUPADO);
                celda.setText("<html><center>" + espacios[i].getId() + "<br>"
                        + v.getPlaca() + "</center></html>");
                celda.setToolTipText(espacios[i].getId() + " - " + v
                        + " [" + v.getColor() + "]");
            }
        }
        resumen.setText("Ocupados: " + ocupados + " | Libres: " + (cantidad - ocupados)
                + " | Total: " + cantidad + "   (verde = libre, rojo = ocupado)");
    }
}