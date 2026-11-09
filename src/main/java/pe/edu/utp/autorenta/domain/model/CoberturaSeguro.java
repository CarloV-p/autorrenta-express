package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import pe.edu.utp.autorenta.domain.enums.TipoCobertura;

/*
    cobertura o extra del catálogo. Existe independientemente de los contratos
    (relación de AGREGACIÓN con ContratoAlquiler, como indica el diagrama del informe).
    el costo es POR DÍA de alquiler.
 */
public class CoberturaSeguro {
    private int idCobertura;
    private TipoCobertura tipo;
    private String nombre;
    private double costoPorDia;

    public CoberturaSeguro(TipoCobertura tipo, String nombre, double costoPorDia){
        this.tipo = ValidacionDominio.noNulo(tipo, "tipo");
        this.nombre = ValidacionDominio.texto(nombre, "nombre");
        this.costoPorDia = ValidacionDominio.noNegativo(costoPorDia, "costo por día");
    }

    public CoberturaSeguro(int idCobertura, TipoCobertura tipo, String nombre, double costoPorDia){
        this(tipo, nombre, costoPorDia);
        this.idCobertura = idCobertura;
    }

    // costo total de la cobertura para un numero de dias
    public double calcularCosto(int dias){return costoPorDia * dias;}

    public int getIdCobertura(){return idCobertura;}
    public void setIdCobertura(int idCobertura){this.idCobertura = idCobertura;}

    public TipoCobertura getTipo(){return tipo;}
    public String getNombre(){return nombre;}
    public double getCostoPorDia(){return costoPorDia;}

    public void setNombre(String nombre){this.nombre = ValidacionDominio.texto(nombre, "nombre");}
    public void setCostoPorDia(double c){this.costoPorDia = ValidacionDominio.noNegativo(c, "costo por día");}

    @Override
    public String toString(){return nombre + " (S/ " + costoPorDia + "/día)";}
}
