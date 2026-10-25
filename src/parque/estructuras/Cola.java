package parque.estructuras;

public class Cola<T> {
    private ListaSimple<T> lista = new ListaSimple<>();

    public void encolar(T dato) {
        lista.insertarAlFinal(dato);
    }

    public T desencolar() {
        return lista.eliminarPrimero();
    }

    public T verFrente() {
        return lista.verPrimero();
    }

    public boolean estaVacia() {
        return lista.estaVacia();
    }

    public int getTamanio() {
        return lista.getTamanio();
    }
    
    public T ver(int posicion){
        return lista.obtener(posicion);
    }
}