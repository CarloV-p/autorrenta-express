package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pe.edu.utp.autorenta.domain.enums.TipoCobro;

// resultado de cotizar un alquiler, se calcula al momento
public class Cotizacion{
    private final Vehiculo vehiculo;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final TipoCobro tipoCobro;
    private final List<CoberturaSeguro> coberturas;
    private final double montoEstimado;
    private final double depositoGarantia;

    public Cotizacion(Vehiculo vehiculo, LocalDate fechaInicio, LocalDate fechaFin, TipoCobro tipoCobro,
                      List<CoberturaSeguro> coberturas, double montoEstimado, double depositoGarantia){
        this.vehiculo = ValidacionDominio.noNulo(vehiculo, "vehículo");
        this.fechaInicio = ValidacionDominio.noNulo(fechaInicio, "fecha inicio");
        this.fechaFin = ValidacionDominio.noNulo(fechaFin, "fecha fin");
        this.tipoCobro = ValidacionDominio.noNulo(tipoCobro, "tipo de cobro");
        this.coberturas = new ArrayList<>(coberturas == null ? List.of() : coberturas);
        this.montoEstimado = montoEstimado;
        this.depositoGarantia = depositoGarantia;
    }

    public Vehiculo getVehiculo(){return vehiculo;}
    public LocalDate getFechaInicio(){return fechaInicio;}
    public LocalDate getFechaFin(){return fechaFin;}
    public TipoCobro getTipoCobro(){return tipoCobro;}
    public List<CoberturaSeguro> getCoberturas(){return Collections.unmodifiableList(coberturas);}
    public double getMontoEstimado(){return montoEstimado;}
    public double getDepositoGarantia(){return depositoGarantia;}
}
