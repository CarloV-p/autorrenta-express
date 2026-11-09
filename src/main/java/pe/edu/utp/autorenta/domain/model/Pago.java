package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import java.time.LocalDate;
import pe.edu.utp.autorenta.domain.enums.MetodoPago;

// pago asociado a un contrato
public class Pago {
    private int idPago;
    private final int idContrato;
    private final LocalDate fecha;
    private final double monto;
    private final MetodoPago metodo;

    public Pago(int idContrato, LocalDate fecha, double monto, MetodoPago metodo){
        if(idContrato <= 0){
            throw new pe.edu.utp.autorenta.domain.exception.DatosInvalidosException(
                    "El pago debe asociarse a un contrato ya registrado.");
        }
        this.idContrato = idContrato;
        this.fecha = ValidacionDominio.noNulo(fecha, "fecha");
        this.monto = ValidacionDominio.positivo(monto, "monto");
        this.metodo = ValidacionDominio.noNulo(metodo, "método de pago");
    }

    public Pago(int idPago, int idContrato, LocalDate fecha, double monto, MetodoPago metodo){
        this(idContrato, fecha, monto, metodo);
        this.idPago = idPago;
    }

    public int getIdPago(){return idPago;}
    public void setIdPago(int idPago){this.idPago = idPago;}
    public int getIdContrato(){return idContrato;}
    public LocalDate getFecha(){return fecha;}
    public double getMonto(){return monto;}
    public MetodoPago getMetodo(){return metodo;}
}
