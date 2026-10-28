package parque.modelo;

import parque.estructuras.ColaSegura;
import parque.estructuras.ListaDoble;
import java.io.IOException;
import parque.persistencia.Persistencia;

public class Residencial {
    private Parqueo parqueo = new Parqueo();
    private ColaSegura<Vehiculo> colaEntrada = new ColaSegura<>();
    private ColaSegura<Vehiculo> colaSalida = new ColaSegura<>();
    private HistorialEventos historial = new HistorialEventos();
    private ListaDoble<Residente> residentes = new ListaDoble<>();
    private volatile int tiempoAtencion = 1500;

    public Parqueo getParqueo() { return parqueo; }
    public ColaSegura<Vehiculo> getColaEntrada() { return colaEntrada; }
    public ColaSegura<Vehiculo> getColaSalida() { return colaSalida; }
    public HistorialEventos getHistorial() { return historial; }
    public int getTiempoAtencion() { return tiempoAtencion;}
    public void setTiempoAtencion(int milisegundos) { this.tiempoAtencion = milisegundos; }

    public synchronized Residente buscarResidente(String id) {
        return residentes.buscar(new Residente(id, id, id, false));
    }

    public synchronized boolean agregarResidente(Residente r) {
        if (residentes.buscar(r) != null) {
            return false;
        }
        residentes.insertarAlFinal(r);
        return true;
    }

    public synchronized boolean eliminarResidente(String id) {
        return residentes.eliminar(new Residente(id, id, id, false));
    }

    public synchronized ListaDoble<Residente> getResidentes() {
        return residentes;
    }

    public synchronized void cargarDatos(String rutaResidentes, String rutaVehiculos)
        throws IOException{
            residentes = Persistencia.cargar(rutaResidentes, rutaVehiculos);
        }

        public synchronized void guardarDatos(String rutaResidentes, String rutaVehiculos)
        throws IOException{
            Persistencia.guardar(residentes, rutaResidentes, rutaVehiculos);
        }

        public synchronized Vehiculo buscarVehiculoResidente(String placa) {
            for (int i = 0; i < residentes.getTamanio(); i++) {
                Vehiculo v = residentes.obtener(i).buscarVehiculo(placa);
                if (v != null){
                    return v;
                }
            }
            return null;
        }
        
        public boolean placaEnUso(String placa) {
            if (parqueo.idEspacioDe(placa) != null) {
                return true;
            }
            for (int i = 0; i < colaEntrada.getTamanio(); i++) {
                Vehiculo v = colaEntrada.ver(i);
                if ( v != null && v.getPlaca().equals(placa)) {
                    return true;
                }
            }
            return  buscarVehiculoResidente(placa) != null;
        }
}