package parque;

import parque.estructuras.ColaSegura;

public class PruebaColaSegura{
    public static void main(String[] args) throws InterruptedException {
        ColaSegura<String> cola = new ColaSegura<>();

        Thread[] garitas = new Thread[3];
        for (int g = 0; g < 3; g++){
            final int numero = g + 1;
            garitas[g] = new Thread(() -> {
                try{
                    while (true){
                        String vehiculo = cola.desencolar();
                        if (vehiculo == null){
                            System.out.println("Garita " + numero + "termina");
                            return;
                        }
                        System.out.println("Garita " + numero + "atiende" + vehiculo);
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            });
            garitas[g].start();
        }

        for (int i = 1 ; i <= 9; i++){
            cola.encolar("Auto" + i);
            System.out.println("Llega Auto" + i);
            Thread.sleep(150);
        }

        Thread.sleep(2000);
        cola.detener();
        for (int g = 0; g < 3; g++){
            garitas[g].join();
        }
        System.out.println("Fin. En cola: " + cola.getTamanio());
    }
}