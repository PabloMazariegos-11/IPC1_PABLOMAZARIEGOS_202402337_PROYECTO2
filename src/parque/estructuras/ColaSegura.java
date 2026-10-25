package parque.estructuras;

public class ColaSegura<T> {
    private Cola<T> cola = new Cola<>();
    private boolean activa = true;

    public synchronized void encolar(T dato){
        cola.encolar(dato);
        notifyAll();
    }

    public synchronized T desencolar() throws InterruptedException {
        while (cola.estaVacia() && activa) {
            wait();
        }
        if (cola.estaVacia()){
            return null;
        }
        return cola.desencolar();
     }

     public synchronized void detener() {
        activa = false;
        notifyAll();
     }

     public synchronized T ver(int posicion) {
        return cola.ver(posicion);
     }

     public synchronized int getTamanio() {
        return cola.getTamanio();
     }

     public synchronized boolean estaVacia() {
        return cola.estaVacia();
     }
}