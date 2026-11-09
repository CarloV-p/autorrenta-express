package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import java.time.LocalDateTime;

// alerta generada cuando un vehiculo alcanza su kilometraje limite (Observer) (modulo 6)
public class AlertaMantenimiento {
    private int idAlerta;
    private final String placa;
    private final double kilometrajeActual;
    private final double kilometrajeLimite;
    private final LocalDateTime fechaGeneracion;
    private final String mensaje;
    private boolean atendida;

    public AlertaMantenimiento(String placa, double kilometrajeActual, double kilometrajeLimite,
                               LocalDateTime fechaGeneracion, String mensaje, boolean atendida){
        this.placa = ValidacionDominio.texto(placa, "placa");
        this.kilometrajeActual = kilometrajeActual;
        this.kilometrajeLimite = kilometrajeLimite;
        this.fechaGeneracion = ValidacionDominio.noNulo(fechaGeneracion, "fecha de generación");
        this.mensaje = ValidacionDominio.texto(mensaje, "mensaje");
        this.atendida = atendida;
    }

    public AlertaMantenimiento(int idAlerta, String placa, double kilometrajeActual, double kilometrajeLimite,
                               LocalDateTime fechaGeneracion, String mensaje, boolean atendida){
        this(placa, kilometrajeActual, kilometrajeLimite, fechaGeneracion, mensaje, atendida);
        this.idAlerta = idAlerta;
    }

    // fabrica que arma la alerta a partir del vehiculo (Creator/Expert)
    public static AlertaMantenimiento para(Vehiculo v) {
        String msg = String.format("El vehículo %s (%s %s) alcanzó %.0f km; límite de mantenimiento: %.0f km.",
                v.getPlaca(), v.getMarca(), v.getModelo(), v.getKilometraje(), v.getKilometrajeProximoMantenimiento());
        return new AlertaMantenimiento(v.getPlaca(), v.getKilometraje(),
                v.getKilometrajeProximoMantenimiento(), LocalDateTime.now(), msg, false);
    }

    public int getIdAlerta(){return idAlerta;}
    public void setIdAlerta(int idAlerta){this.idAlerta = idAlerta;}
    public String getPlaca(){return placa;}
    public double getKilometrajeActual(){return kilometrajeActual;}
    public double getKilometrajeLimite(){return kilometrajeLimite;}
    public LocalDateTime getFechaGeneracion(){return fechaGeneracion;}
    public String getMensaje(){return mensaje;}
    public boolean isAtendida(){return atendida;}
    public void setAtendida(boolean atendida){this.atendida = atendida;}
}
