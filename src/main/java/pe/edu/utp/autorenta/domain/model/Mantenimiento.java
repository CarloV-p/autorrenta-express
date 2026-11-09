package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import java.time.LocalDate;
import pe.edu.utp.autorenta.domain.enums.EstadoMantenimiento;
import pe.edu.utp.autorenta.domain.enums.TipoMantenimiento;

// orden de mantenimiento de un vehiculo, se referencia por placa
public class Mantenimiento {
    private int idMantenimiento;
    private String placa;
    private LocalDate fecha;
    private TipoMantenimiento tipo;
    private EstadoMantenimiento estado;
    private double costo;
    private String descripcion;

    public Mantenimiento(String placa, LocalDate fecha, TipoMantenimiento tipo,
                         EstadoMantenimiento estado, double costo, String descripcion){
        this.placa = ValidacionDominio.texto(placa, "placa").toUpperCase();
        this.fecha = ValidacionDominio.noNulo(fecha, "fecha");
        this.tipo = ValidacionDominio.noNulo(tipo, "tipo");
        this.estado = ValidacionDominio.noNulo(estado, "estado");
        this.costo = ValidacionDominio.noNegativo(costo, "costo");
        this.descripcion = descripcion == null ? "" : descripcion.trim();
    }

    public Mantenimiento(int idMantenimiento, String placa, LocalDate fecha, TipoMantenimiento tipo,
                         EstadoMantenimiento estado, double costo, String descripcion){
        this(placa, fecha, tipo, estado, costo, descripcion);
        this.idMantenimiento = idMantenimiento;
    }

    public int getIdMantenimiento(){return idMantenimiento;}
    public void setIdMantenimiento(int id){this.idMantenimiento = id;}
    public String getPlaca(){return placa;}
    public LocalDate getFecha(){return fecha;}
    public TipoMantenimiento getTipo(){return tipo;}
    public EstadoMantenimiento getEstado(){return estado;}
    public void setEstado(EstadoMantenimiento estado){this.estado = ValidacionDominio.noNulo(estado, "estado");}
    public double getCosto(){return costo;}
    public void setCosto(double costo){this.costo = ValidacionDominio.noNegativo(costo, "costo");}
    public String getDescripcion(){return descripcion;}
    public void setDescripcion(String d){this.descripcion = d == null ? "" : d.trim();}
}
