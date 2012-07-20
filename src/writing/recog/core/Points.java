/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package writing.recog.core;

import java.awt.Point;

/**
 *
 * @author Shivam
 */
public class Points extends Point {
    
    private int X;
    private int Y;
    
    public Points (int X, int Y) {
        this.X = X;
        this.Y = Y;
    }
    
    public Points (Points p) {
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
