package parque.modelo;

import java.time.LocalTime;

public class Evento {
    private String descripcion;
    private String hora;

    public Evento(String descripcion){
        this.descripcion = descripcion;
        this.hora = LocalTime.now().withNano(0).toString();
    }

    public String getDescripcion() { return descripcion; }
    public String getHora() { return hora; }

    @Override 
    public String toString() {
        return "[" + hora + "]" + descripcion;
    }
}

