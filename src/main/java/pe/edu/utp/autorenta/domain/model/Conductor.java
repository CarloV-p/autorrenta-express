package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import java.time.LocalDate;

// conductor autorizado a manejar un vehiculo alquilado
public class Conductor {
    private int idConductor;
    private String dni;
    private String nombre;
    private String nroLicencia;
    private LocalDate fechaVencimientoLicencia;// puede ser null si aun no se registra

    public Conductor(String dni, String nombre, String nroLicencia, LocalDate fechaVencimientoLicencia){
        setDni(dni);
        setNombre(nombre);
        setNroLicencia(nroLicencia);
        this.fechaVencimientoLicencia = fechaVencimientoLicencia;
    }

    public Conductor(int idConductor, String dni, String nombre, String nroLicencia,
                     LocalDate fechaVencimientoLicencia){
        this(dni, nombre, nroLicencia, fechaVencimientoLicencia);
        this.idConductor = idConductor;
    }

    // el conductor sabe si su propia licencia esta vigente
    public boolean licenciaVigente(LocalDate hoy){
        return fechaVencimientoLicencia != null && !fechaVencimientoLicencia.isBefore(hoy);
    }

    public int getIdConductor(){return idConductor;}
    public void setIdConductor(int idConductor) { this.idConductor = idConductor;}

    public String getDni(){return dni;}
    public void setDni(String dni) { this.dni = ValidacionDominio.dni(dni);}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = ValidacionDominio.texto(nombre, "nombre");}

    public String getNroLicencia(){return nroLicencia;}
    public void setNroLicencia(String nroLicencia){
        this.nroLicencia = ValidacionDominio.texto(nroLicencia, "número de licencia");
    }

    public LocalDate getFechaVencimientoLicencia(){return fechaVencimientoLicencia;}
    public void setFechaVencimientoLicencia(LocalDate f){this.fechaVencimientoLicencia = f;}

    @Override
    public String toString(){return nombre + " (Lic. " + nroLicencia + ")";}
}
