package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pe.edu.utp.autorenta.domain.enums.TipoContrato;

/*
    contrato corporativo multitramo 
    lo construye paso a paso el Builder (modulo 3)
    hereda de ContratoAlquiler y agrega empresa, limite mensual y tramos.
 */
public class ContratoCorporativo extends ContratoAlquiler{
    private String razonSocialEmpresa;
    private String ruc;
    private double limiteKilometrajeMensual;
    private final List<TramoContrato> tramos = new ArrayList<>();

    public ContratoCorporativo(Cliente cliente, Vehiculo vehiculo, LocalDate inicio, LocalDate fin,
                               String razonSocialEmpresa){
        super(cliente, vehiculo, inicio, fin);
        setRazonSocialEmpresa(razonSocialEmpresa);
    }

    @Override
    public TipoContrato getTipoContrato(){return TipoContrato.CORPORATIVO;}

    public void agregarTramo(TramoContrato tramo){
        ValidacionDominio.noNulo(tramo, "tramo");
        tramos.add(tramo);
    }

    public List<TramoContrato> getTramos(){return Collections.unmodifiableList(tramos);}

    public String getRazonSocialEmpresa(){return razonSocialEmpresa;}
    public void setRazonSocialEmpresa(String r) {
        this.razonSocialEmpresa = ValidacionDominio.texto(r, "razón social");
    }

    public String getRuc(){return ruc;}
    public void setRuc(String ruc){this.ruc = ruc == null ? "" : ruc.trim();}

    public double getLimiteKilometrajeMensual() { return limiteKilometrajeMensual;}
    public void setLimiteKilometrajeMensual(double l){
        this.limiteKilometrajeMensual = ValidacionDominio.noNegativo(l, "límite mensual");
    }
}
