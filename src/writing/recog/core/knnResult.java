/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package writing.recog.core;

/**
 *
 * @author Renovatio
 */
public class knnResult {
    private double distance;
    private String name;
    
    public knnResult (String name, double distance) {
        this.distance = distance;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public double getKnnDistance() {
        return distance;
    }
}
