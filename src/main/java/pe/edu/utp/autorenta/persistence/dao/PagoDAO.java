package pe.edu.utp.autorenta.persistence.dao;
// @author Valdivia Paricahua Carlo Alexander

import java.util.List;
import pe.edu.utp.autorenta.domain.model.Pago;

// un pago es un registro contable, se registra y se consulta, no se edita ni se borra (ISP)
public interface PagoDAO {
    Pago registrar(Pago pago);
    List<Pago> listarPorContrato(int idContrato);
}
