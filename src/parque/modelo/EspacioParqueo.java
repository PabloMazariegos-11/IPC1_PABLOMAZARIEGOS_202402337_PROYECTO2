package parque.modelo;

public class EspacioParqueo {
    private String id;
    private boolean ocupado;
    private Vehiculo vehiculo;
    private String tipoEspacio;

    private EspacioParqueo siguiente;
    private EspacioParqueo arriba;
    private EspacioParqueo abajo;

    public EspacioParqueo(String id, String tipoEspacio) {
        this.id = id;
        this.tipoEspacio = tipoEspacio;
        this.ocupado = false;
        this.vehiculo = null;
    }

    public boolean estaLibre() {
        return !ocupado;
    }

    public void ocupar(Vehiculo v) {
        this.vehiculo = v;
        this.ocupado = true;
        v.setEstado("ESTACIONADO");
    }

    public Vehiculo liberar() {
        Vehiculo saliente = this.vehiculo;
        this.vehiculo = null;
        this.ocupado = false;
        if (saliente != null) {
            saliente.setEstado("AFUERA");
        }
        return saliente;
    }

    public String getId() { return id; }
    public boolean isOcupado() { return ocupado; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public String getTipoEspacio() { return tipoEspacio; }

    public EspacioParqueo getSiguiente() { return siguiente; }
    public void setSiguiente(EspacioParqueo siguiente) { this.siguiente = siguiente; }
    public EspacioParqueo getArriba() { return arriba; }
    public void setArriba(EspacioParqueo arriba) { this.arriba = arriba; }
    public EspacioParqueo getAbajo() { return abajo; }
    public void setAbajo(EspacioParqueo abajo) { this.abajo = abajo; }
}