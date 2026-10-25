package parque.estructuras;

public class Pila<T> {
    private ListaSimple<T> lista = new ListaSimple<>();

    public void apilar(T dato) {
        lista.insertarAlInicio(dato);
    }

    public T desapilar() {
        return lista.eliminarPrimero();
    }

    public T verTope() {
        return lista.verPrimero();
    }

    public boolean estaVacia() {
        return lista.estaVacia();
    }

    public int getTamanio() {
        return lista.getTamanio();
    }
    public T ver(int posicion) {
        return lista.obtener(posicion);
    }
}