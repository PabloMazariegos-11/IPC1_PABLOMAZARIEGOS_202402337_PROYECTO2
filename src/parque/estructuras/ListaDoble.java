package parque.estructuras;

public class ListaDoble<T> {
    private NodoDoble<T> cabeza;
    private NodoDoble<T> cola;
    private int tamanio;

    public void insertarAlFinal(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            nuevo.setAnterior(cola);
            cola = nuevo;
        }
        tamanio++;
    }

    public boolean eliminar(T dato) {
        NodoDoble<T> actual = cabeza;
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                if (actual.getAnterior() == null) {
                    cabeza = actual.getSiguiente();
                } else {
                    actual.getAnterior().setSiguiente(actual.getSiguiente());
                }
                if (actual.getSiguiente() == null) {
                    cola = actual.getAnterior();
                } else {
                    actual.getSiguiente().setAnterior(actual.getAnterior());
                }
                tamanio--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public int getTamanio() { return tamanio; }

    public boolean estaVacia() { return tamanio == 0; }

    public void imprimir() {
        NodoDoble<T> actual = cabeza;
        while (actual != null) {
            System.out.print(actual.getDato() + "<->");
            actual = actual.getSiguiente();
        }
        System.out.println("null");
    }

    public void imprimirAlReves() {
        NodoDoble<T> actual = cola;
        while (actual != null) {
            System.out.print(actual.getDato() + "<->");
            actual = actual.getAnterior();
        }
        System.out.println("null");
    }
}