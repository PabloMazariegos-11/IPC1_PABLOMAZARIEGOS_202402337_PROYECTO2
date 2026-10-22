package parque.modelo;

import parque.estructuras.ListaSimple;

public class Residente {
    private String id;
    private String nombre;
    private String casa;
    private boolean socio;
    private ListaSimple<Vehiculo> vehiculos;
        public static final int MAX_VEHICULOS = 3;

    public Residente(String id, String nombre, String casa, boolean socio) {
        this.id = id;
        this.nombre = nombre;
        this.casa = casa;
        this.socio = socio;
        this.vehiculos = new ListaSimple<>();
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCasa() { return casa; }
    public void setCasa(String casa) { this.casa = casa; }
    public boolean isSocio() { return socio; }
    public void setSocio(boolean socio) { this.socio = socio; }
    public ListaSimple<Vehiculo> getVehiculos() { return vehiculos; }

    @Override
    public boolean equals(Object otro) {
        if (!(otro instanceof Residente)) {
            return false;
        }
        return id.equals(((Residente) otro).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (casa " + casa + ", " + (socio ? "socio" : "no socio") + ")";
    }
        public boolean puedeAgregarVehiculo() {
        return vehiculos.getTamanio() < MAX_VEHICULOS;
    }

    public boolean agregarVehiculo(Vehiculo v) {
        if (!puedeAgregarVehiculo()) {
            return false;
        }
        if (buscarVehiculo(v.getPlaca()) != null) {
            return false;
        }
        vehiculos.insertarAlFinal(v);
        return true;
    }

    public Vehiculo buscarVehiculo(String placa) {
        for (int i = 0; i < vehiculos.getTamanio(); i++) {
            Vehiculo v = vehiculos.obtener(i);
            if (v.getPlaca().equals(placa)) {
                return v;
            }
        }
        return null;
    }

    public boolean eliminarVehiculo(String placa) {
        Vehiculo v = buscarVehiculo(placa);
        if (v == null) {
            return false;
        }
        return vehiculos.eliminar(v);
    }
}
