
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Emmanuel
 */
public class XMLUIBuilder {
   /**
    * Punto de entrada público: construye una JFrame a partir de un XML.
    * @param rutaXml Ruta relativa o absoluta del XML (p.ej. "resources/ui.xml")
    * @return JFrame ya construida (no visible)
    */
    public JFrame construirInterfaz(String rutaXml) {
    try {
        // 1) Cargar y parsear el XML (DOM)
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(rutaXml));
        doc.getDocumentElement().normalize();
        
        // 2) Validar nodo raíz
        Element root = doc.getDocumentElement();
        if (!"interfaz".equals(root.getTagName())) {
            throw new IllegalArgumentException("El nodo raíz debe ser <interfaz>.");
        }
        
        // 3) Crear ventana principal
        String titulo = root.getAttribute("titulo");
        JFrame frame = new JFrame(titulo.isEmpty() ? "Interfaz" : titulo);
        LayoutManager lm = crearLayout(root);
        if (lm != null) frame.setLayout(lm);
        
        // 4) Construir el contenido recursivamente
        construirHijos(root, frame.getContentPane(), frame.getLayout());
        
        // 5) Ajustes finales
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        return frame;
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null,
        "Error construyendo la interfaz desde XML:\n" + ex.getMessage(),
        "Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }
}
    /** Crea el LayoutManager según el atributo "layout" del elemento */
    private LayoutManager crearLayout(Element e) {
        String l = e.getAttribute("layout");
        switch (l) {
            case "FlowLayout":
            return new FlowLayout(FlowLayout.LEFT, 8, 8);
            case "BorderLayout":
            return new BorderLayout(8, 8);

            case "GridLayout":
            // Soporte opcional si en el XML pones layout="GridLayout" filas/columnas
            String filas = e.getAttribute("filas");
            String cols = e.getAttribute("columnas");
            int f = (filas.isEmpty() ? 1 : Integer.parseInt(filas));
            int c = (cols.isEmpty() ? 1 : Integer.parseInt(cols));
            return new GridLayout(f, c, 8, 8);
            default:

            // Si no hay atributo o no coincide, se usa el layout por defecto del contenedor
            return null;
        }
    }
    
    /**
    * Recorre recursivamente los hijos del elemento XML y los añade al contenedor Swing.
    * @param e Elemento XML padre
    * @param contenedor Contenedor Swing destino
    * @param layoutPadre Layout del contenedor (para tratar BorderLayout)
    */
    private void construirHijos(Element e, Container contenedor, LayoutManager layoutPadre) {
       NodeList hijos = e.getChildNodes();
       for (int i = 0; i < hijos.getLength(); i++) {
       Node n = hijos.item(i);
       if (n.getNodeType() != Node.ELEMENT_NODE) continue;
       Element hijo = (Element) n;
       Component comp = crearComponente(hijo);
       if (comp != null) {
           // Si el componente es contenedor, aplica su layout y construye recursivamente
           if (comp instanceof Container) {
               LayoutManager lmHijo = crearLayout(hijo);
               if (lmHijo != null) {
                   ((Container) comp).setLayout(lmHijo);
               }
               construirHijos(hijo, (Container) comp, ((Container) comp).getLayout());
            }
           
           // Añadir al contenedor padre respetando la "region" si el padre es BorderLayout
           if (layoutPadre instanceof BorderLayout) {
               Object constraint = traducirRegion(hijo.getAttribute("region"));
               contenedor.add(comp, constraint);
           } else {
               contenedor.add(comp);
           }
        }
        }
    }
    
    /** Crea un componente Swing según la etiqueta del XML */
    private Component crearComponente(Element e) {
        String tag = e.getTagName();
        switch (tag) {
            case "panel":
                return new JPanel();
            case "label":
                return new JLabel(e.getAttribute("texto"));
            case "button": {
                String textoBtn = e.getAttribute("texto");
                String evento = e.getAttribute("evento");
                JButton b = new JButton(textoBtn.isEmpty() ? "Botón" : textoBtn);

                // Ejemplos simples de eventos (personalizable)
                if ("guardar".equalsIgnoreCase(evento)) {
                    b.addActionListener(ev -> System.out.println("Guardando..."));
                } else if ("saludar".equalsIgnoreCase(evento)) {
                    b.addActionListener(ev -> JOptionPane.showMessageDialog(null, "¡Hola!"));
                }
                return b;
            }
            case "textfield": {
                String cols = e.getAttribute("columnas");
                int c = (cols.isEmpty() ? 15 : Integer.parseInt(cols));
                return new JTextField(c);
            }
        }

        // Etiqueta no soportada (avisar por consola)
        System.err.println("Etiqueta no soportada: <" + tag + ">");
        return null;
    }
    
    /** Traduce el atributo region a la constante de BorderLayout */
    private Object traducirRegion(String region) {
        if (region == null) return BorderLayout.CENTER;
            switch (region) {
            case "North": return BorderLayout.NORTH;
            case "South": return BorderLayout.SOUTH;
            case "East": return BorderLayout.EAST;
            case "West": return BorderLayout.WEST;
            case "Center":
            default: return BorderLayout.CENTER;
        }
    }
}
