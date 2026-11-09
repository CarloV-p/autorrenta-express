package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pe.edu.utp.autorenta.domain.enums.EstadoContrato;
import pe.edu.utp.autorenta.domain.enums.TipoCobro;
import pe.edu.utp.autorenta.domain.enums.TipoContrato;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;

/*
    contrato de alquiler base
    contratoCorporativo lo extiende 
    LSP, debe poder usarse en cualquier lugar donde se espere un ContratoAlquiler.
 */
public class ContratoAlquiler {
    private int idContrato;// 0 = aun no persistido
    private Cliente cliente;
    private Vehiculo vehiculo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoContrato estadoContrato = EstadoContrato.ACTIVO;
    private TipoCobro tipoCobro = TipoCobro.POR_DIA;
    private double montoTotal;
    private double depositoGarantia;
    private double kilometrajePactado;// 0 = sin limite pactado
    private double kilometrajeInicial;
    private Double kilometrajeFinal;// null hasta la devolucion
    private final List<Conductor> conductores = new ArrayList<>();
    private final List<CoberturaSeguro> coberturas = new ArrayList<>();

    public ContratoAlquiler(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio, LocalDate fechaFin){
        this.cliente = ValidacionDominio.noNulo(cliente, "cliente");
        this.vehiculo = ValidacionDominio.noNulo(vehiculo, "vehículo");
        this.kilometrajeInicial = vehiculo.getKilometraje();
        setFechas(fechaInicio, fechaFin);
    }

    // polimorfismo, las subclases redefinen el tipo

    public TipoContrato getTipoContrato(){return TipoContrato.SIMPLE;}

    // comportamiento 
    // dias de alquiler (minimo 1)
    // lo usan Strategy y Decorator para calcular tarifas
    public int getDiasAlquiler(){
        return (int) Math.max(1, ChronoUnit.DAYS.between(fechaInicio, fechaFin));
    }

    public double getKilometrosRecorridos(){
        return kilometrajeFinal == null ? 0 : Math.max(0, kilometrajeFinal - kilometrajeInicial);
    }

    public void agregarConductor(Conductor conductor){
        ValidacionDominio.noNulo(conductor, "conductor");
        boolean repetido = conductores.stream().anyMatch(c -> c.getDni().equals(conductor.getDni()));
        if(!repetido){
            conductores.add(conductor);
        }
    }

    // evita duplicar una cobertura del mismo tipo en un contrato
    public void agregarCobertura(CoberturaSeguro cobertura){
        ValidacionDominio.noNulo(cobertura, "cobertura");
        boolean repetida = coberturas.stream().anyMatch(c -> c.getTipo() == cobertura.getTipo());
        if(!repetida){
            coberturas.add(cobertura);
        }
    }

    public void finalizar(double kilometrajeDevolucion){
        if(estadoContrato != EstadoContrato.ACTIVO){
            throw new DatosInvalidosException("Solo un contrato ACTIVO puede finalizarse.");
        }
        if(kilometrajeDevolucion < kilometrajeInicial){
            throw new DatosInvalidosException("El kilometraje de devolución no puede ser menor al inicial.");
        }
        this.kilometrajeFinal = kilometrajeDevolucion;
        this.estadoContrato = EstadoContrato.FINALIZADO;
    }

    public void cancelar(){
        if(estadoContrato != EstadoContrato.ACTIVO){
            throw new DatosInvalidosException("Solo un contrato ACTIVO puede cancelarse.");
        }
        this.estadoContrato = EstadoContrato.CANCELADO;
    }

    // getters y setters
    public int getIdContrato(){return idContrato;}
    public void setIdContrato(int idContrato) { this.idContrato = idContrato; }

    public Cliente getCliente(){return cliente;}
    public void setCliente(Cliente cliente){this.cliente = ValidacionDominio.noNulo(cliente, "cliente");}

    public Vehiculo getVehiculo(){return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo){this.vehiculo = ValidacionDominio.noNulo(vehiculo, "vehículo");}

    public LocalDate getFechaInicio(){return fechaInicio;}
    public LocalDate getFechaFin(){return fechaFin;}

    public void setFechas(LocalDate inicio, LocalDate fin){
        ValidacionDominio.noNulo(inicio, "fecha de inicio");
        ValidacionDominio.noNulo(fin, "fecha de fin");
        if(fin.isBefore(inicio)){
            throw new DatosInvalidosException("La fecha de fin no puede ser anterior a la de inicio.");
        }
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }

    public EstadoContrato getEstadoContrato(){return estadoContrato;}
    public void setEstadoContrato(EstadoContrato e){this.estadoContrato = ValidacionDominio.noNulo(e, "estado");}

    public TipoCobro getTipoCobro(){return tipoCobro;}
    public void setTipoCobro(TipoCobro t){this.tipoCobro = ValidacionDominio.noNulo(t, "tipo de cobro");}

    public double getMontoTotal(){return montoTotal;}
    public void setMontoTotal(double m){this.montoTotal = ValidacionDominio.noNegativo(m, "monto total");}

    public double getDepositoGarantia(){return depositoGarantia;}
    public void setDepositoGarantia(double d){this.depositoGarantia = ValidacionDominio.noNegativo(d, "depósito");}

    public double getKilometrajePactado(){return kilometrajePactado;}
    public void setKilometrajePactado(double k){this.kilometrajePactado = ValidacionDominio.noNegativo(k, "km pactado");}

    public double getKilometrajeInicial(){return kilometrajeInicial;}
    public void setKilometrajeInicial(double k){this.kilometrajeInicial = ValidacionDominio.noNegativo(k, "km inicial");}

    public Double getKilometrajeFinal(){return kilometrajeFinal;}
    public void setKilometrajeFinal(Double k){this.kilometrajeFinal = k;}

    public List<Conductor> getConductores(){return Collections.unmodifiableList(conductores);}
    public List<CoberturaSeguro> getCoberturas(){return Collections.unmodifiableList(coberturas);}

    @Override
    public String toString(){
        return "Contrato #" + idContrato + " [" + getTipoContrato() + "] " + vehiculo.getPlaca()
                + " " + fechaInicio + " → " + fechaFin;
    }
}
