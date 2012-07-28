/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recog.gesture;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import recog.core.Points;

/**
 *
 * @author Renovatio
 */
public class Gesture implements Runnable{
    
    public static ArrayList<GesturesList> gestures_list = new ArrayList<> (); 
    private static ArrayList<Points> gesture_points = new ArrayList<> ();
    private static ArrayList<Points> prev_points = new ArrayList<> ();
    private static String gesture_name;
    public static boolean parsed = false;
        
    public static void readGestures(String fromFile){
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                //Used to hold X and Y coordinates of the point.
                int X, Y;

                @Override
                public void startDocument() throws SAXException {
                    super.startDocument();
                    //System.out.print("begin Parsing doc...\n");
                    parsed = false;
                }

                @Override
                public void endDocument() throws SAXException {
                    super.endDocument();
                    //System.out.print("end Parsing doc...\n");
                    parsed = true;
                }
                
                /* If the read element is Gesture, take its name and if it is
                 * a Point, store its X and Y coordinate.
                 */
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    super.startElement(uri, localName, qName, attributes);
                    
                    //System.out.print(qName+"\n");
                    if (qName.equalsIgnoreCase("Point")){
                        X = Integer.parseInt(attributes.getValue("X"));
                        Y = Integer.parseInt(attributes.getValue("Y"));
                        //System.out.print("X*Y: "+X+"*"+Y+"\n");
                    } else if (qName.equalsIgnoreCase("Gesture")) {
                        gesture_points = new ArrayList<>();
                        gesture_name = attributes.getValue("name");
                        //System.out.print("name: "+gesture_name+"\n");
                    }
                }
                
                /* If the read element is Gesture, add a gestureslist with the name and is
                 * a Point, create a Point with it.
                 */
                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    super.endElement(uri, localName, qName);
                    
                    //System.out.print(qName+"\n");
                    if (qName.equalsIgnoreCase("Point")){
                        gesture_points.add(new Points(X, Y));
                    } else if (qName.equalsIgnoreCase("Gesture")) {
                        gestures_list.add(new GesturesList(gesture_name, gesture_points));
                    } 
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    super.characters(ch, start, length);
                }
                
            };
            
            saxParser.parse(fromFile, handler);
            
        } catch(ParserConfigurationException | SAXException | IOException e){
            
        }
    }
    

    @Override
    public void run() {
        Gesture.readGestures("gestures.xml");
    }
    
    
}
