package parque.modelo;

public class Parqueo {
    private EspacioParqueo cabezaSocios;   // A1
    private EspacioParqueo cabezaGeneral;  // E1
    private int total;

    public Parqueo() {
        construir();
    }

    private void construir() {
        String letras = "ABCEFGHI";
        EspacioParqueo cabezaFilaAnterior = null;

        for (int f = 0; f < letras.length(); f++) {
            char letra = letras.charAt(f);
            boolean areaSocios = letra <= 'C';
            int cantidad = areaSocios ? 25 : 15;
            String tipo = areaSocios ? "SOCIO" : "GENERAL";

            EspacioParqueo cabezaFila = null;
            EspacioParqueo ultimo = null;

            // nodo de la fila anterior en la misma columna (solo dentro de la misma área)
            EspacioParqueo encima = null;
            if (cabezaFilaAnterior != null
                    && cabezaFilaAnterior.getTipoEspacio().equals(tipo)) {
                encima = cabezaFilaAnterior;
            }

            for (int c = 1; c <= cantidad; c++) {
                EspacioParqueo nuevo = new EspacioParqueo(letra + "" + c, tipo);
                total++;

                if (cabezaFila == null) {
                    cabezaFila = nuevo;
                } else {
                    ultimo.setSiguiente(nuevo);
                }

                if (encima != null) {
                    nuevo.setArriba(encima);
                    encima.setAbajo(nuevo);
                    encima = encima.getSiguiente();
                }
                ultimo = nuevo;
            }

            if (areaSocios && cabezaSocios == null) {
                cabezaSocios = cabezaFila;
            }
            if (!areaSocios && cabezaGeneral == null) {
                cabezaGeneral = cabezaFila;
            }
            cabezaFilaAnterior = cabezaFila;
        }
    }

    public EspacioParqueo getCabezaSocios() { return cabezaSocios; }
    public EspacioParqueo getCabezaGeneral() { return cabezaGeneral; }
    public int getTotal() { return total; }

    public int contarLibres(String tipo) {
        EspacioParqueo fila = tipo.equals("SOCIO") ? cabezaSocios : cabezaGeneral;
        int libres = 0;
        while (fila != null) {
            EspacioParqueo e = fila;
            while (e != null) {
                if (e.estaLibre()) {
                    libres++;
                }
                e = e.getSiguiente();
            }
            fila = fila.getAbajo();
        }
        return libres;
    }

    public void imprimirMapa() {
        EspacioParqueo[] cabezas = { cabezaSocios, cabezaGeneral };
        for (int i = 0; i < cabezas.length; i++) {
            EspacioParqueo fila = cabezas[i];
            while (fila != null) {
                EspacioParqueo e = fila;
                while (e != null) {
                    System.out.print(String.format("%-5s", e.getId() + (e.isOcupado() ? "*" : "")));
                    e = e.getSiguiente();
                }
                System.out.println();
                fila = fila.getAbajo();
            }
            System.out.println();
        }
    }
    private EspacioParqueo buscarLibre(EspacioParqueo cabezaArea) {
        EspacioParqueo fila = cabezaArea;
        while (fila != null) {
            EspacioParqueo e = fila;
            while (e != null) {
                if (e.estaLibre()) {
                    return e;
                }
                e = e.getSiguiente();
            }
            fila = fila.getAbajo();
        }
        return null;
    }

    private EspacioParqueo buscarPorPlaca(EspacioParqueo cabezaArea, String placa) {
        EspacioParqueo fila = cabezaArea;
        while (fila != null) {
            EspacioParqueo e = fila;
            while (e != null) {
                if (e.isOcupado() && e.getVehiculo().getPlaca().equals(placa)) {
                    return e;
                }
                e = e.getSiguiente();
            }
            fila = fila.getAbajo();
        }
        return null;
    }

    public synchronized EspacioParqueo asignar(Vehiculo v, boolean esSocio) {
        EspacioParqueo espacio = null;
        if (esSocio) {
            espacio = buscarLibre(cabezaSocios);
        }
        if (espacio == null) {
            espacio = buscarLibre(cabezaGeneral);
        }
        if (espacio != null) {
            espacio.ocupar(v);
        }
        return espacio;
    }

    public synchronized Vehiculo liberar(String placa) {
        EspacioParqueo espacio = buscarPorPlaca(cabezaSocios, placa);
        if (espacio == null) {
            espacio = buscarPorPlaca(cabezaGeneral, placa);
        }
        if (espacio == null) {
            return null;
        }
        return espacio.liberar();
    }

        public synchronized String idEspacioDe(String placa) {
        EspacioParqueo espacio = buscarPorPlaca(cabezaSocios, placa);
        if (espacio == null) {
            espacio = buscarPorPlaca(cabezaGeneral, placa);
        }
        return espacio == null ? null : espacio.getId();
    }
}
