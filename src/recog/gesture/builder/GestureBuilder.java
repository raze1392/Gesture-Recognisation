/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recog.gesture.builder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import recog.core.CoreUtilities;
import recog.core.Point;

/**
 *
 * @author Renovatio
 */
public class GestureBuilder {
    
    private String file;
    
    public GestureBuilder(String file) {
        this.file = file;
    }
   
    private boolean record(String name, String category, ArrayList <Point> input) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            
            Element root = doc.getDocumentElement();
            Element gesture = doc.createElement("Gesture");
            int l = input.size();
            
            for (int i=0; i<l; i++) {
                Element point = doc.createElement("Point");
                int X = input.get(i).getIntX();
                int Y = input.get(i).getIntY();
                point.setAttribute("X", Integer.toString(X));
                point.setAttribute("Y", Integer.toString(Y));
                gesture.appendChild(point);
            }
            
            gesture.setAttribute("category", category);
            gesture.setAttribute("name", name);
            root.appendChild(gesture);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            String s = writer.toString();

            FileWriter fileWriter = new FileWriter(file);
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write(s);
                bufferedWriter.flush();
            } catch (Exception ex) {
                System.out.print("File Writer failed\n");
                return false;
            }
            return true;
        } catch(ParserConfigurationException | SAXException | IOException | TransformerException e){
        System.out.print("recording Gesture failed\n");}
        return false;
    }
    
    public boolean reordGesture(String name, String category,  ArrayList <Point> input) {
        input = CoreUtilities.resampleInput(input, CoreUtilities.RESAMPLE);
        boolean recorded = record(name, category, input);
        return recorded;                
    }
    
}
