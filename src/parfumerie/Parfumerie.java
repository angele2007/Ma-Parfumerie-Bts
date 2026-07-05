/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package parfumerie;
import com.formdev.flatlaf.FlatLightLaf;
import parfumerie.vues.Authentification;

/**
 *
 * @author Angele
 */
public class Parfumerie {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FlatLightLaf.setup();
        // TODO code application logic he
        java.awt.EventQueue.invokeLater(() -> {
            new parfumerie.vues.Authentification().setVisible(true);
        });
    }
    
}
