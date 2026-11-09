package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

// arrendatario del servicio (persona natural o representante de una empresa)
public class Cliente {
    private int idCliente;// 0 = aun no persistido
    private String nombre;
    private String dni;
    private String telefono;

    public Cliente(String nombre, String dni, String telefono){
        setNombre(nombre);
        setDni(dni);
        setTelefono(telefono);
    }

    public Cliente(int idCliente, String nombre, String dni, String telefono){
        this(nombre, dni, telefono);
        this.idCliente = idCliente;
    }

    public int getIdCliente(){return idCliente;}
    public void setIdCliente(int idCliente){this.idCliente = idCliente;}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = ValidacionDominio.texto(nombre, "nombre");}

    public String getDni(){return dni;}
    public void setDni(String dni){this.dni = ValidacionDominio.dni(dni);}

    public String getTelefono(){return telefono;}
    public void setTelefono(String telefono){this.telefono = telefono == null ? "" : telefono.trim();}

    @Override
    public String toString(){return nombre + " (DNI " + dni + ")";}
}
