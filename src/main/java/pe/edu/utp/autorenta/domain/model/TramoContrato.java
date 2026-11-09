package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import java.time.LocalDate;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;

// tramo de un contrato corporativo (no existe sin su ContratoCorporativo, uso de COMPOSICION)
public class TramoContrato {
    private int idTramo;
    private final LocalDate fechaInicioTramo;
    private final LocalDate fechaFinTramo;
    private final String destino;

    public TramoContrato(LocalDate fechaInicioTramo, LocalDate fechaFinTramo, String destino){
        this.fechaInicioTramo = ValidacionDominio.noNulo(fechaInicioTramo, "fecha inicio del tramo");
        this.fechaFinTramo = ValidacionDominio.noNulo(fechaFinTramo, "fecha fin del tramo");
        if(fechaFinTramo.isBefore(fechaInicioTramo)){
            throw new DatosInvalidosException("La fecha fin del tramo no puede ser anterior a la de inicio.");
        }
        this.destino = ValidacionDominio.texto(destino, "destino");
    }

    public TramoContrato(int idTramo, LocalDate inicio, LocalDate fin, String destino){
        this(inicio, fin, destino);
        this.idTramo = idTramo;
    }

    public int getIdTramo(){return idTramo;}
    public void setIdTramo(int idTramo){this.idTramo = idTramo;}
    public LocalDate getFechaInicioTramo(){return fechaInicioTramo;}
    public LocalDate getFechaFinTramo(){return fechaFinTramo;}
    public String getDestino(){return destino;}

    @Override
    public String toString(){return destino + " (" + fechaInicioTramo + " → " + fechaFinTramo + ")";}
}
