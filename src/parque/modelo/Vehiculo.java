package parque.modelo;

public class Vehiculo{
    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private String tipo;
    private String estado;
    private String idResidente;
    
    public Vehiculo(String placa, String marca, String modelo, String color, String tipo, String idResidente){
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.tipo = tipo;
        this.idResidente = idResidente;
        this.estado = "AFUERA";
   }

   public String getPlaca() {return placa; }
   public String getMarca() {return marca; }
   public String getModelo() {return modelo; }
   public String getColor() {return color; }
   public String getTipo() {return tipo; }
    public String getEstado() {return estado; }
   public void setEstado(String estado) {this.estado = estado; }
   public String getIdResidente() {return idResidente;}

   public boolean esVisitante(){
    return idResidente == null;
   } 

   @Override 
   public boolean equals(Object otro){
    if (!(otro instanceof Vehiculo)){
        return false;
    }
    return placa.equals(((Vehiculo) otro).placa);
   }

   @Override 
   public int hashCode() {
    return placa.hashCode();
   }

   @Override 
   public String toString(){
    return placa + " (" + marca + " " + modelo + ")";  
   }
}