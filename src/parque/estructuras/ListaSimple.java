package parque.estructuras;

public class ListaSimple<T> {
    private NodoSimple<T> cabeza;
    private int tamanio;

    public ListaSimple() {
        cabeza = null;
        tamanio = 0;
    }

    public void insertarAlFinal(T dato) {
        NodoSimple<T> nuevo = new NodoSimple<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else{
            NodoSimple<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }
    public boolean estaVacia(){
        return tamanio == 0;
    }

    public int  getTamanio() {
        return tamanio;
    }
    public T obtener(int indice) {
        if  (indice < 0 || indice >=tamanio){
            return null;
        }
        NodoSimple<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    public boolean contiene(T dato){
        NodoSimple<T> actual = cabeza;
        while (actual != null){
            if (actual.getDato().equals(dato)){
                return true;
            }
            actual = actual.getSiguiente();
        } 
        return false;
    }

    public boolean eliminar(T dato){
        if (cabeza == null){
            return false;
        }
        if (cabeza.getDato().equals(dato)) {
            cabeza = cabeza.getSiguiente();
            tamanio--;
            return true;
        }
        NodoSimple<T> actual = cabeza;
        while (actual.getSiguiente() !=null){
            if (actual.getSiguiente().getDato().equals(dato)){
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamanio--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public void imprimir(){
        NodoSimple<T> actual = cabeza;
        while (actual != null){
            System.out.print(actual.getDato() +"->");
            actual = actual.getSiguiente();
        }
        System.out.println("null");
    }
        public void insertarAlInicio(T dato) {
        NodoSimple<T> nuevo = new NodoSimple<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        tamanio++;
    }

    public T eliminarPrimero() {
        if (cabeza == null) {
            return null;
        }
        T dato = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        tamanio--;
        return dato;
    }

    public T verPrimero() {
        return cabeza == null ? null : cabeza.getDato();
    }
}