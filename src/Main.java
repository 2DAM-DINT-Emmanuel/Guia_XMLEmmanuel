
import javax.swing.JFrame;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Mario
 */
public class Main {
  public static void main(String[] args) {
    // Opcional: look & feel del sistema
    try {
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {}
    
    XMLUIBuilder builder = new XMLUIBuilder();
    JFrame f = builder.construirInterfaz("resources/ui.xml");
    if (f != null) {
        f.setVisible(true);
    }
  }
}
