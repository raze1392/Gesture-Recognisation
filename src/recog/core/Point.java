/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recog.core;


/**
 *
 * @author Shivam
 */
public class Point {
    
    private int X;
    private int Y;
    
    public Point (int X, int Y) {
        this.X = X;
        this.Y = Y;
    }
    
    public Point (Point p) {
        X = p.getIntX();
        Y = p.getIntY();
    }
    
    public int getIntX () {
        return X;
    }
    
    public int getIntY () {
        return Y;
    }
    
}
