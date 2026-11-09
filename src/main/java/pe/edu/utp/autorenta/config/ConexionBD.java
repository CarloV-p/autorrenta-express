package pe.edu.utp.autorenta.config;
// @author Valdivia Paricahua Carlo Alexander

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import pe.edu.utp.autorenta.domain.exception.PersistenciaException;

/*
    fabrica de conexiones SQLite y creador del esquema
    se crea una sola vez al correr el pryecto y lo usa los DAO
*/
public class ConexionBD {

    public static final String URL_POR_DEFECTO = "jdbc:sqlite:data/autorenta.db";
    private static final String RECURSO_ESQUEMA = "/db/schema.sql";

    private final String url;

    public ConexionBD(){this(URL_POR_DEFECTO);}

    public ConexionBD(String url){this.url = url;}

    /*abre una conexión nueva con claves foráneas activadas. Quien la pide debe cerrarla. */
    public Connection obtenerConexion(){
        try{
            Connection c = DriverManager.getConnection(url);
            try (Statement st = c.createStatement()){
                st.execute("PRAGMA foreign_keys = ON");
            }
            return c;
        }catch(SQLException e){
            throw new PersistenciaException("No se pudo conectar a la base de datos: " + url, e);
        }
    }

    // crea carpeta, tablas y datos iniciales, se puede llamar en cada arranque
    public void inicializarEsquema(){
        crearCarpetaSiHaceFalta();
        String script = leerEsquema();
        try(Connection c = obtenerConexion(); Statement st = c.createStatement()){
            c.setAutoCommit(false);
            for(String sentencia : script.split(";")){
                if(!sentencia.isBlank()) {
                    st.execute(sentencia.trim());
                }
            }
            c.commit();
        }catch(SQLException e){
            throw new PersistenciaException("Error al inicializar el esquema de la base de datos.", e);
        }
    }

    private void crearCarpetaSiHaceFalta(){
        String prefijo = "jdbc:sqlite:";
        if(url.startsWith(prefijo) && !url.contains(":memory:")){
            Path padre = Paths.get(url.substring(prefijo.length())).toAbsolutePath().getParent();
            try{
                if(padre != null){
                    Files.createDirectories(padre);
                }
            }catch(IOException e){
                throw new PersistenciaException("No se pudo crear la carpeta de la base de datos.", e);
            }
        }
    }

    private String leerEsquema(){
        try(InputStream in = ConexionBD.class.getResourceAsStream(RECURSO_ESQUEMA)){
            if(in == null){
                throw new PersistenciaException("No se encontró " + RECURSO_ESQUEMA + " en resources.", null);
            }
            try(BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))){
                return r.lines().filter(l -> !l.trim().startsWith("--")).collect(Collectors.joining("\n"));
            }
        }catch(IOException e){
            throw new PersistenciaException("No se pudo leer el esquema SQL.", e);
        }
    }

    public String getUrl(){return url;}
}
