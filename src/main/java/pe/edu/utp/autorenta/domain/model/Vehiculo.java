package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import pe.edu.utp.autorenta.domain.enums.EstadoVehiculo;
import pe.edu.utp.autorenta.domain.enums.TipoVehiculo;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.exception.VehiculoNoDisponibleException;

// unidad de la flota
// la placa es su identificador natural
public class Vehiculo {
    private final String placa;
    private String marca;
    private String modelo;
    private TipoVehiculo tipo;
    private EstadoVehiculo estado;
    private double kilometraje;
    private double precioBasePorDia;
    private double kilometrajeProximoMantenimiento;

    public Vehiculo(String placa, String marca, String modelo, TipoVehiculo tipo,
                    EstadoVehiculo estado, double kilometraje, double precioBasePorDia,
                    double kilometrajeProximoMantenimiento){
        this.placa = ValidacionDominio.texto(placa, "placa").toUpperCase();
        setMarca(marca);
        setModelo(modelo);
        setTipo(tipo);
        setEstado(estado);
        this.kilometraje = ValidacionDominio.noNegativo(kilometraje, "kilometraje");
        setPrecioBasePorDia(precioBasePorDia);
        this.kilometrajeProximoMantenimiento = ValidacionDominio.positivo(kilometrajeProximoMantenimiento, "km próximo mantenimiento");
    }

    // comportamiento
    public boolean estaDisponible(){return estado == EstadoVehiculo.DISPONIBLE;}

    // el Observer (modulo 6) consulta este metodo para decidir si lanza la alerta
    public boolean requiereMantenimiento(){return kilometraje >= kilometrajeProximoMantenimiento;}

    // cambia a ALQUILADO, lanza VehiculoNoDisponibleException si no estaba disponible
    public void marcarAlquilado(){
        if(!estaDisponible()){
            throw new VehiculoNoDisponibleException(placa);
        }
        this.estado = EstadoVehiculo.ALQUILADO;
    }

    public void marcarDisponible(){this.estado = EstadoVehiculo.DISPONIBLE;}

    public void enviarAMantenimiento(){this.estado = EstadoVehiculo.EN_MANTENIMIENTO;}

    // el medidor de kilometraje nunca retrocede
    // devuelve true si tras actualizar se alcanzo el limite
    public boolean registrarKilometraje(double nuevoKilometraje){
        if(nuevoKilometraje < kilometraje){
            throw new DatosInvalidosException("El kilometraje nuevo (" + nuevoKilometraje
                    + ") no puede ser menor al actual (" + kilometraje + ").");
        }
        this.kilometraje = nuevoKilometraje;
        return requiereMantenimiento();
    }

    // programa el siguiente mantenimiento sumando "intervaloKm" al kilometraje actual
    public void programarSiguienteMantenimiento(double intervaloKm){
        this.kilometrajeProximoMantenimiento = kilometraje + ValidacionDominio.positivo(intervaloKm, "intervalo");
    }

    // getters y setters
    public String getPlaca(){return placa;}

    public String getMarca(){return marca;}
    public void setMarca(String marca){this.marca = ValidacionDominio.texto(marca, "marca");}

    public String getModelo(){return modelo;}
    public void setModelo(String modelo){this.modelo = ValidacionDominio.texto(modelo, "modelo");}

    public TipoVehiculo getTipo(){return tipo;}
    public void setTipo(TipoVehiculo tipo){this.tipo = ValidacionDominio.noNulo(tipo, "tipo");}

    public EstadoVehiculo getEstado(){return estado;}
    public void setEstado(EstadoVehiculo estado){this.estado = ValidacionDominio.noNulo(estado, "estado");}

    public double getKilometraje(){return kilometraje;}

    public double getPrecioBasePorDia(){return precioBasePorDia;}
    public void setPrecioBasePorDia(double p){this.precioBasePorDia = ValidacionDominio.positivo(p, "precio base por día");}

    public double getKilometrajeProximoMantenimiento(){return kilometrajeProximoMantenimiento;}
    public void setKilometrajeProximoMantenimiento(double km){
        this.kilometrajeProximoMantenimiento = ValidacionDominio.positivo(km, "km próximo mantenimiento");
    }

    @Override
    public String toString(){return placa + " - " + marca + " " + modelo + " [" + tipo + "]";}
}
