package parque.modelo;

import parque.estructuras.Pila;

public class HistorialEventos {
    private Pila<Evento> pila = new Pila<>();

    public synchronized void registrar(String descripcion) { 
        pila.apilar(new Evento(descripcion));
    }

    public synchronized Evento obtener(int posicion){
        return pila.ver(posicion);
    }

    public synchronized int getTamanio(){
        return pila.getTamanio();
    }
}