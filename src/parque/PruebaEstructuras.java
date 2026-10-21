package parque;

import parque.estructuras.ListaSimple;
import parque.estructuras.ListaDoble;
import parque.estructuras.Cola;
import parque.estructuras.Pila;

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

                System.out.println("--- ListaDoble ---");
        ListaDoble<String> doble = new ListaDoble<>();
        doble.insertarAlFinal("X");
        doble.insertarAlFinal("Y");
        doble.insertarAlFinal("Z");
        doble.imprimir();
        doble.imprimirAlReves();
        doble.eliminar("Y");
        doble.imprimir();
        doble.imprimirAlReves();

        System.out.println("--- Cola ---");
        Cola<String> cola = new Cola<>();
        cola.encolar("1");
        cola.encolar("2");
        cola.encolar("3");
        System.out.println("Sale: " + cola.desencolar());
        System.out.println("Frente: " + cola.verFrente());

        System.out.println("--- Pila ---");
        Pila<String> pila = new Pila<>();
        pila.apilar("1");
        pila.apilar("2");
        pila.apilar("3");
        System.out.println("Sale: " + pila.desapilar());
        System.out.println("Tope: " + pila.verTope());
    }
}