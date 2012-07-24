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
    private static int RESAMPLE = 60;
    private static double GOLDEN_RATIO = (-1 + Math.sqrt(5) ) / 2;
    private static boolean processed = false;
    public static ArrayList <RecogniserResult> result = new ArrayList<> ();
    
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
        double dx = (double)Math.pow(Math.abs(p2.getIntX() - p1.getIntX()), 2);
        double dy = (double)Math.pow(Math.abs(p2.getIntY() - p1.getIntY()), 2);
        double sq = dx + dy;
        return Math.sqrt(sq);
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
                } else if (temp < min_X){
                    min_X = temp;
                }
                
                /* Calculate maximum of Y and Minimum of Y */
                temp = point.get(i).getIntY();
                if (temp > max_Y){
                    max_Y = temp;
                } else if (temp < min_Y){
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
            float scaleX = (float)wX / width_X;
            float scaleY = (float)wY / width_Y;
            /* System.out.print("===================Knn===================");
             System.out.print("Width: "+width_X+"*"+width_Y+"\n");
             System.out.print("ScaleX: "+scaleX+" ScaleY:"+scaleY+"\n");*/
            ArrayList<Points> pts = new ArrayList<> ();
            for (int i=0; i<num_points; i++) {
                /* Scale the point up or down by a factor of 'scaleX * scaleY' */
                pts.add(new Points ((int)(point.get(i).getIntX()*scaleX), 
                                          (int)(point.get(i).getIntY()*scaleY)));
            }
            return pts;
        }
        
    }
    
    public static ArrayList<Points> relocateCentroid (Points orgCtd, Points newCtd,  
                                                ArrayList<Points> point) {
        int num_points = point.size();
        if (num_points > 0 ) {
            int displaceX = newCtd.getIntX() - orgCtd.getIntX();
            int displaceY = newCtd.getIntY() - orgCtd.getIntY();
            ArrayList<Points> pts = new ArrayList<> ();
            
            for (int i=0; i<num_points; i++) {
                /* Move all the points in locus with the new centroid  */
                pts.add(new Points ((int)(point.get(i).getIntX()+displaceX), 
                                          (int)(point.get(i).getIntY()+displaceY)));
            }
            
            return pts;
        } else {
            return null;
        }
    }
    
    /*
     * Resamples no. of points to N equidistant points based on interpolation
     */
    public static ArrayList<Points> resampleInput (ArrayList<Points> point, 
                                                int resample_num) {
        int num_points = point.size();
        if (num_points < 0 || resample_num <=32){
            return null;
        } else {
            double diff = calculatePathLength(point) / (resample_num-1);
            ArrayList<Points> pts = new ArrayList<> ();
            double t_dist = 0.0;
            pts.add(new Points ((int)point.get(0).getIntX(), 
                                            (int)point.get(0).getIntY()));
            
            for (int i=1; i<point.size(); i++) {
                double d = calculateDistance((Points)point.get(i-1), (Points)point.get(i));
                if ((t_dist+d) >= diff) {
                    int X = (int)(point.get(i-1).getIntX() + 
                            ((diff - t_dist)/d)*(point.get(i).getIntX()-point.get(i-1).getIntX()));
                    int Y = (int)(point.get(i-1).getIntY() + 
                            ((diff - t_dist)/d)*(point.get(i).getIntY()-point.get(i-1).getIntY()));
                    
                    pts.add(new Points (X, Y));
                    t_dist = 0.0;
                    point.add(i, new Points(X, Y));
                    //System.out.print("i: "+point.get(i).getIntX()+"*"+point.get(i).getIntY()+"\n");
                } else {
                    t_dist += d;
                }
            }
            return pts;
        }
    }
    
    public static ArrayList <Points> rotateByAngle(ArrayList<Points> point, double angle) {
        
        int num_points = point.size();
        if (num_points > 0) {
            double rad = Math.toRadians(angle);
            ArrayList<Points> pts = new ArrayList<> ();
            Points cent = calculateCentroid(point);
            int cX = cent.getIntX();
            int cY = cent.getIntY();
            for (int i=0; i<num_points; i++){
               int X = (int)((point.get(i).getIntX()-cX)*Math.cos(rad) - 
                                (point.get(i).getIntY()-cY)*Math.sin(rad) + cX ); 
               int Y = (int)((point.get(i).getIntX()-cX)*Math.sin(rad) - 
                                (point.get(i).getIntY()-cY)*Math.cos(rad) + cY );
               pts.add(new Points(X, Y));
            }
            
            return pts;     
        } else {
            return null;
        }
    }
    
    public static ArrayList <Points> rotateToZero(ArrayList<Points> point) {
        int num_points = point.size();
        ArrayList<Points> pts = new ArrayList<> ();
        Points cent = calculateCentroid(point);
        int cX = cent.getIntX();
        int cY = cent.getIntY();
        double zeroAng = Math.atan2(cY - point.get(0).getIntY(), cX - point.get(0).getIntX());
        zeroAng = Math.toDegrees(zeroAng);
        pts = rotateByAngle(point, zeroAng);
        return pts;        
    }
    
    public static double pathDistance(ArrayList<Points> input, ArrayList<Points> gesture) {
        int num_points = input.size();
        double dist = 0;
        for (int i=0; i<num_points; i++){
            dist += calculateDistance(input.get(i), gesture.get(i));
        }
        double avg = dist / RESAMPLE;
        return avg;
    }
    
    public static double pathByAngle(ArrayList<Points> input, ArrayList<Points> gesture, double angle) {
        input = rotateByAngle(input, angle);
        double path = pathDistance(input, gesture);
        return path;
    }
    
    public static double bestAngleDistance(ArrayList<Points> input, ArrayList<Points> gesture, 
                                                        double angA ,double angB, double angDelta) {
        double angX1 = GOLDEN_RATIO*angA + (1-GOLDEN_RATIO)*angB;
        double f1 = pathByAngle(input, gesture, angX1);
        double angX2 = (1-GOLDEN_RATIO)*angA + GOLDEN_RATIO*angB;
        double f2 = pathByAngle(input, gesture, angX2);
        
        while (Math.abs(angA - angB) > angDelta) {
            if (f1 < f2){
                angB = angX2;
                angX2 = angX1;
                f2 = f1;
                angX1 = GOLDEN_RATIO*angA + (1-GOLDEN_RATIO)*angB;
                f1 = pathByAngle(input, gesture, angX1);
            } else {
                angA = angX1;
                angX1 = angX2;
                f1 = f2;
                angX2 = (1-GOLDEN_RATIO)*angA + GOLDEN_RATIO*angB;
                f2 = pathByAngle(input, gesture, angX2);
            }
        }
        
        return Math.min(f1, f2);
    }
    
    public static ArrayList<Points> preProcessing (ArrayList<Points> input, Gesture temp) {
        
        input = resampleInput(input, RESAMPLE);
        input = rotateToZero(input);
        findExtremumXY(input);
        input = scaleByWandH(100, 100, input);
        Points centroid = calculateCentroid(input);
        input = relocateCentroid(centroid, new Points(0, 0), input);
         
        if (!processed) {
            int n_temp = Gesture.gestures_list.size();
            for (int i=0; i<n_temp; i++) {
                ArrayList <Points> compare = Gesture.gestures_list.get(i).getPoints();
                compare = resampleInput(compare, RESAMPLE);
                compare = rotateToZero(compare);
                findExtremumXY(compare);
                compare = scaleByWandH(100, 100, compare);
                centroid = calculateCentroid(compare);
                compare = relocateCentroid(centroid, new Points(0, 0), compare);
                Gesture.gestures_list.set(i, new GesturesList(Gesture.gestures_list.get(i).getName(), compare));
            }
            processed = true;
        }
        return input;
    }
    
  /******************************************************************************
   ********************    GESTURE RECOGNISER ALGO    ***************************
   ******************   BASED ON GOLDEN SECTION SEARCH    ***********************
   ******************************************************************************/ 
    
    public static void gssAlgo (ArrayList<Points> input, Gesture templates,
                                                double minAng, double maxAng, double angDel) {
        input = preProcessing(input, templates);
        double b = Math.PI * 10000;
        int n_temp = Gesture.gestures_list.size();
        String temp_name = "";
        for (int i=0; i<n_temp; i++){
            double d = bestAngleDistance(input, Gesture.gestures_list.get(i).getPoints(),
                                                minAng, maxAng, angDel);
            if (d < b){
                b = d;
                temp_name = Gesture.gestures_list.get(i).getName();
            }
        }
        double score = (1 - b) / (.5*Math.sqrt((100*100) + (100*100)));
        System.out.print("b: "+b);
        System.out.print("bv: "+.5*Math.sqrt((100*100) + (100*100)));
        result.add(new RecogniserResult(temp_name, score));
    }
    
 /*******************************************************************************
  ************************    THE KNN ALGORITHM    ******************************
  ******************************************************************************/   

    public static void knnAlgo(ArrayList<Points> inp){
        int num_points = inp.size();
        double rec_dist = 0;
        
        if (num_points > 0){
            findExtremumXY(inp);
            /*System.out.print("Mx: "+max_X+"*"+min_X+"\n");
            System.out.print("My: "+max_Y+"*"+min_Y+"\n");
            System.out.print("X: "+width_X+"*"+width_Y+"\n");*/
            inp = scaleByWandH(100, 200, inp);
            inp = resampleInput(inp, 60);
            Points inp_centroid = calculateCentroid(inp);
            
            if (Gesture.parsed) {
                int gest = Gesture.gestures_list.size();

                if (gest > 0) {
                    
                    for (int i=0; i<gest; i++){
                        
                        ArrayList <Points> compare = Gesture.gestures_list.get(i).getPoints();
                        compare = resampleInput(compare, 60);
                        Points gest_centroid = calculateCentroid(compare);
                        compare = relocateCentroid(gest_centroid, new Points(0, 0), compare);
                        inp = relocateCentroid(inp_centroid, new Points(0, 0), inp);
                        /*for (int l=0; l<60; l++){
                            System.out.print("Input: "+inp.get(l).getIntX()+"*"+inp.get(l).getIntY()+"\n");
                            System.out.print("Compare: "+compare.get(l).getIntX()+"*"+compare.get(l).getIntY()+"\n");
                        }*/
                        for (int j=0; j<60; j++) {
                            for (int k=0; k<60; k++) {
                                double dist = calculateDistance((Points)inp.get(k), (Points)compare.get(j));
                                System.out.print("Dist: "+dist+"\n");
                                rec_dist += (1/dist);
                            }
                        }
                        result.add(new RecogniserResult(Gesture.gestures_list.get(i).getName(), rec_dist));
                    }

                }
            }
        }
    }
    
}

