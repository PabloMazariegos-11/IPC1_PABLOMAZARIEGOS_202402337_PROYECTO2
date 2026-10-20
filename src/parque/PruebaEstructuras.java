package parque;

import parque.estructuras.ListaSimple;

public class PruebaEstructuras {
    public static void main(String[] args) {
        ListaSimple<String> lista = new ListaSimple<>();
        System.out.println("¿Vacía? " + lista.estaVacia());
        lista.insertarAlFinal("A");
        lista.insertarAlFinal("B");
        lista.insertarAlFinal("C");
        System.out.println("¿Vacía? " + lista.estaVacia());
        System.out.println("Tamaño: " + lista.getTamanio());

         lista.imprimir();
        System.out.println("Contiene B: " + lista.contiene("B"));
        lista.eliminar("B");
        lista.imprimir();
        lista.eliminar("A");
        lista.imprimir();
        System.out.println("Tamaño: " + lista.getTamanio());
        System.out.println("Obtener 0: " + lista.obtener(0));
    }
}