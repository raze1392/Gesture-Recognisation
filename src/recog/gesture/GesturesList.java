/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recog.gesture;

import java.util.ArrayList;
import recog.core.Points;

/**
 *
 * @author Renovatio
 */
public class GesturesList {
    
    private ArrayList<Points> gesture_points;
    private String gesture_name;
    
    public GesturesList (String gesture_name, ArrayList<Points> gesture_points) {
        this.gesture_name = gesture_name;
        this.gesture_points = gesture_points;
    }
    
    public String getName(){
        return gesture_name;
    }
    
    public ArrayList<Points> getPoints(){
        return gesture_points;
    }
}
