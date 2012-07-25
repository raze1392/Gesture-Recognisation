/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recog.main;

/**
 *
 * @author Renovatio
 */
public class RecogniserResult {
    private double score;
    private String name;
    
    public RecogniserResult (String name, double score) {
        this.score = score;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public double getScore() {
        return score;
    }
}
