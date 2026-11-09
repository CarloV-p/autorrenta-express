package pe.edu.utp.autorenta.util;
// @author Valdivia Paricahua Carlo Alexander

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// utilidad para hashear contraseñas (en hexadecimal)
public final class HashUtil {

    private HashUtil(){}

    public static String sha256(String texto){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(byte b : hash){
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }catch(NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
