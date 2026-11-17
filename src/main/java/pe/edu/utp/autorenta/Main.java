
package pe.edu.utp.autorenta;

/**
 * @author Valdivia Paricahua Carlo Alexander
 * @author Chirme Ccolque Edwards Alex
 * @author Romero Pequeña Marlee Lucero
 * @author Burgos Huanca Luis Joseph
 * @author Chamorro Casani Oscar Gonzalo
 */

import pe.edu.utp.autorenta.vista.swing.MainFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MainFrame ventana = new MainFrame("Administrador");
            ventana.setVisible(true);
        });

    }
}
