/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package writing.recog.core;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author Shivam
 */
public class CoreUtilities {

    private static int max_X = 0, min_X = 0; 
    private static int max_Y = 0, min_Y = 0;
    private static int width_X = 0, width_Y = 0;
    public static ArrayList <knnResult> knn = new ArrayList<> ();
    
    public static double calculatePathLength (ArrayList<Points> point) {
        double path_len = 0;
        int num_points = point.size();
        
        if (num_points > 0) {
            for (int i=1; i<num_points; i++) {
                double dist = calculateDistance((Points)point.get(i-1), (Points)point.get(i));
                path_len += dist;
            }
            return path_len;
        }else{
            return 0;
        }
    }
    
    public static double calculateDistance(Points p1, Points p2) {
        double dx = Math.pow(((double)p2.getIntX() - p1.getIntX()), 2.0);
        double dy = Math.pow(((double)p2.getIntY() - p1.getIntY()), 2.0);
        
        return Math.sqrt(dx + dy);
    }
    
    public static Points calculateCentroid (ArrayList<Points> point) {
        int tX = 0;
        int tY = 0;
        int num_points = point.size();
        
        if (num_points > 0) {
            
            for (int i=0; i<num_points; i++) {
                tX += point.get(i).getIntX();
                tY += point.get(i).getIntY();
            }
            tX /= num_points;
            tY /= num_points;
            
            return new Points(tX, tY);
        }else{
            return new Points(0, 0);
        }
      }
    
    public static void findExtremumXY (ArrayList<Points> point) {
        int num_points = point.size();
        
        if (num_points > 0) {
            
            max_X = point.get(0).getIntX();
            max_Y = point.get(0).getIntY();
            min_X = point.get(0).getIntX();
            min_Y = point.get(0).getIntY();
            
            for (int i=1; i<num_points; i++) {
                /* Calculate maximum of X and Minimum of X using a temp var to avoid
                 * repeated methods invocation*/
                int temp = point.get(i).getIntX();
                if (temp > max_X){
                    max_X = temp;
                } else if (temp <= min_X){
                    min_X = temp;
                }
                
                /* Calculate maximum of Y and Minimum of Y */
                temp = point.get(i).getIntY();
                if (temp > max_Y){
                    max_Y = temp;
                } else if (temp <= min_Y){
                    min_Y = temp;
                }
            }
        } else {
            max_X = 0; min_X = 0; 
            max_Y = 0; min_Y = 0;
        }
        
        width_X = Math.abs(max_X - min_X);
        width_Y = Math.abs(max_Y - min_Y);
    }
    
    public static Rectangle createBound () {
        if (width_X > 0 && width_Y > 0){
            return new Rectangle(min_X, min_Y, width_X, width_Y);
        } else {
            return null;
        }
    }
    
    public static ArrayList<Points> scaleByPercent (float scale, 
                                                ArrayList<Points> point) {
        int num_points = point.size();

        if (scale <= 0 || num_points == 0 ){
            return null;
        } else {
            
            ArrayList<Points> pts = new ArrayList<> ();
            for (int i=0; i<num_points; i++) {
                
                /* Scale the point up or down by a factor of 'scale' */
                pts.add(new Points ((int)(point.get(i).getIntX()*scale), 
                                          (int)(point.get(i).getIntY()*scale)));
            }
            /*System.out.print("==========================================="+scale+"\n");
            for (int i=0; i<pts.size(); i++) {
            System.out.print("X: "+pts.get(i).getIntX()+" Y: "+pts.get(i).getIntY()+"\n");
            }*/
            return pts;
        } 
    }
    
    public static ArrayList<Points> scaleByWandH (int wX, int wY, 
                                                ArrayList<Points> point) {
        int num_points = point.size();
        
        if (wX <= 0 || wY <=0 || num_points == 0 ){
            return null;
        } else {
            float scaleX = (float)width_X / wX;
            float scaleY = (float)width_Y / wY;
            float scale = (scaleX + scaleY) / 2;
            /* System.out.print("===================Knn===================");
             System.out.print("Width: "+width_X+"*"+width_Y+"\n");
             System.out.print("ScaleX: "+scaleX+" ScaleY:"+scaleY+"\n");*/
            ArrayList<Points> pts = new ArrayList<> ();
            pts = scaleByPercent(scale, point);
            return pts;
        }
        
    }
    
    public static ArrayList<Points> relocateCentroid (Points orgCtd, Points newCtd,  
                                                ArrayList<Points> point) {
        int num_points = point.size();

        if (num_points > 0 ) {
            int displaceX = orgCtd.getIntX() - newCtd.getIntX();
            int displaceY = orgCtd.getIntY() - newCtd.getIntY();
            ArrayList<Points> pts = new ArrayList<> ();
            
            for (int i=0; i<num_points; i++) {
                
                /* Move all the points in locus with the new centroid  */
                point.add(new Points ((int)(point.get(i).getIntX()+displaceX), 
                                          (int)(point.get(i).getIntY()+displaceY)));
            }
            
            return pts;
        } else {
            return null;
        }
    }
    
    public static ArrayList<Points> resampleInput (ArrayList<Points> point, 
                                                int resample_num) {
        int num_points = point.size();
        
        if (num_points < 0 || resample_num <=10){
            return null;
        } else {
            double totalpath = calculatePathLength(point);
            System.out.print("Pathlen: "+totalpath+"\n");
            
            /* Calculate the differnce to lie between two points if resampled to N */
            double diff = totalpath / resample_num;
            System.out.print("Diff: "+diff+"\n"+resample_num);
            ArrayList<Points> pts = new ArrayList<> ();
            double temp = 0.0;

            pts.add(new Points ((int)point.get(0).getIntX(), 
                                            (int)point.get(0).getIntY()));
            for (int i=1; i<num_points; i++) {
                temp += calculateDistance((Points)point.get(i-1), (Points)point.get(i));
                
                if (temp > diff) {
                    System.out.print("temp: "+temp+"\n");
                    pts.add(new Points ((int)point.get(i).getIntX(), 
                                            (int)point.get(i).getIntY()));
                    temp = 0;
                    System.out.print("tempx: "+temp+"\n");
                }
            }
            
            return pts;
        }
    }
    
 /**********************************************************************************
  * *******************    THE KNN ALGORITHM    ***********************************
 * *******************************************************************************/

    public static void knn_algo(ArrayList<Points> inp){
        int num_points = inp.size();
        double rec_dist = 0;
        
        if (num_points > 0){
            findExtremumXY(inp);
            inp = scaleByWandH(100, 200, inp);
           
            inp = resampleInput(inp, 60);
            Points inp_centroid = calculateCentroid(inp);
            System.out.print("===================Knn===================");
            /*System.out.print("Size: "+inp.size());
            for (int i=0; i<inp.size(); i++) {
                System.out.print("X: "+inp.get(i).getIntX()+" Y: "+inp.get(i).getIntY()+"\n");
            }*/
            if (Gesture.parsed) {
                int gest = Gesture.gestures_list.size();

                if (gest > 0) {
                    for (int i=0; i<gest; i++){
                        Points gest_centroid = calculateCentroid(Gesture.gestures_list.get(i).getPoints());
                        inp = relocateCentroid(inp_centroid, gest_centroid, inp);
                        ArrayList <Points> compare = Gesture.gestures_list.get(i).getPoints();

                        for (int j=0; j<gest; j++) {
                            double dist = calculateDistance(inp.get(j), compare.get(j));
                            rec_dist += (1/dist);
                        }
                        knn.add(new knnResult(Gesture.gestures_list.get(i).getName(), rec_dist));
                    }

                }
            }
        }
    }
    
}

