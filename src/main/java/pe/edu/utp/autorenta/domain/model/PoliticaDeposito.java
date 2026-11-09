package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

/*
    parametros de deposito en garantia y de mantenimiento
    no cambia y la guarda
    el Singleton GestorCentralFlota (modulo 1)
 */
public final class PoliticaDeposito {
    private final double porcentajeDeposito;// ejemplo: 0.20 = 20 % del monto estimado
    private final double depositoMinimo;// valor mas bajo del deposito
    private final double intervaloKmMantenimiento;// cada cuantos km toca mantenimiento

    public PoliticaDeposito(double porcentajeDeposito, double depositoMinimo, double intervaloKmMantenimiento){
        if(porcentajeDeposito < 0 || porcentajeDeposito > 1){
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 1.");
        }
        this.porcentajeDeposito = porcentajeDeposito;
        this.depositoMinimo = ValidacionDominio.noNegativo(depositoMinimo, "depósito mínimo");
        this.intervaloKmMantenimiento = ValidacionDominio.positivo(intervaloKmMantenimiento, "intervalo km");
    }

    public static PoliticaDeposito porDefecto(){return new PoliticaDeposito(0.20, 200.0, 10_000.0);}

    public double calcularDeposito(double montoEstimado) {
        return Math.max(depositoMinimo, montoEstimado * porcentajeDeposito);
    }

    public double getPorcentajeDeposito(){return porcentajeDeposito;}
    public double getDepositoMinimo(){return depositoMinimo;}
    public double getIntervaloKmMantenimiento(){return intervaloKmMantenimiento;}
}
