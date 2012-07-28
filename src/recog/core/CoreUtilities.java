/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recog.core;

import java.awt.Rectangle;
import java.util.ArrayList;
import recog.gesture.Gesture;
import recog.gesture.GesturesList;
import recog.main.RecogniserResult;

/**
 *
 * @author Shivam
 */
public class CoreUtilities {

    private static int max_X = 0, min_X = 0; 
    private static int max_Y = 0, min_Y = 0;
    private static int width_X = 0, width_Y = 0;
    public static int RESAMPLE = 60;
    private static double GOLDEN_RATIO = (-1 + Math.sqrt(5) ) / 2;
    private static boolean processed = false;
    public static ArrayList <RecogniserResult> result;
    
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
    
    public static ArrayList<Points> relocateCentroid (Points newCtd,  
                                                ArrayList<Points> point) {
        int num_points = point.size();
        if (num_points > 0 ) {
            Points cent = calculateCentroid(point);
            int cX = cent.getIntX();
            int cY = cent.getIntY();
            ArrayList<Points> pts = new ArrayList<> ();
            
            for (int i=0; i<num_points; i++) {
                /* Move all the points in locus with the new centroid  */
                pts.add(new Points ((int)(point.get(i).getIntX()+ newCtd.getIntX() - cX), 
                                          (int)(point.get(i).getIntY()+ newCtd.getIntY() - cY)));
            }
            
            return pts;
        } else {
            return null;
        }
    }
    
    /*
     * Resamples no. of points to N equidistant points based on interpolation
     */
    public static ArrayList<Points> resampleInput (ArrayList<Points> input, 
                                                int resample_num) {
        
        double diff = calculatePathLength(input) / (resample_num-1);
        ArrayList<Points> src = new ArrayList (input);
        ArrayList<Points> dest = new ArrayList<> ();
        double t_dist = 0.0;
        dest.add(src.get(0));

        for (int i=1; i<src.size(); i++) {
            Points p1 = src.get(i-1);
            Points p2 = src.get(i);
            double d = calculateDistance(p1, p2);
            if ((t_dist+d) >= diff) {
                /*
                int X = (int)Math.round((p1.getIntX() + ((diff - t_dist)/d)*(p2.getIntX()-p1.getIntX())));
                int Y = (int)Math.round((p1.getIntY() + ((diff - t_dist)/d)*(p2.getIntY()-p1.getIntY())));
                */
                double ang = indicativeAngleWrtPoints(p1, p2);
                int X = (int)(p1.getIntX() + ((diff - t_dist)*Math.cos(Math.toRadians(ang))));
                int Y = (int)(p1.getIntY() + ((diff - t_dist)*Math.sin(Math.toRadians(ang))));
                dest.add(new Points (X, Y));
                src.add(i, new Points(X, Y));
                t_dist = 0.0;
                //System.out.print("i: "+point.get(i).getIntX()+"*"+point.get(i).getIntY()+"\n");
            } else {
                t_dist += d;
            }
        }
        if (dest.size() == resample_num - 1)
            {
                dest.add(src.get(src.size()-1));
            }
        
        return dest;
    }
    
    // Rotate a point 'p' around a point 'c' by the given radians.
    // Rotation (around the origin) amounts to a 2x2 matrix of the form:
    //
    //		[ cos A		-sin A	] [ p.x ]
    //		[ sin A		cos A	] [ p.y ]
    //
    // Note that the C# Math coordinate system has +x-axis stright right and
    // +y-axis straight down. Rotation is clockwise such that from +x-axis to
    // +y-axis is +90 degrees, from +x-axis to -x-axis is +180 degrees, and 
    // from +x-axis to -y-axis is -90 degrees.
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
               int Y = (int)((point.get(i).getIntX()-cX)*Math.sin(rad) + 
                                (point.get(i).getIntY()-cY)*Math.cos(rad) + cY );
               pts.add(new Points(X, Y));
            }
            
            return pts;     
        } else {
            return null;
        }
    }
    
    public static ArrayList <Points> rotateToZero(ArrayList<Points> input) {
        double zeroAng = indicativeAngleWrtCentroid(input);
        ArrayList<Points> pts = rotateByAngle(input, zeroAng);
        return pts;        
    }
    
    public static double indicativeAngleWrtCentroid(ArrayList<Points> input) {
        Points cent = calculateCentroid(input);
        Points r = input.get(0);
        double zeroAng = indicativeAngleWrtPoints(r, cent);
        return zeroAng;
    }
    
    // determines the angle, in radians, between two points. the angle is defined 
    // by the circle centered on the start point with a radius to the end point, 
    // where 0 radians is straight right from start (+x-axis) and PI/2 radians is
    // straight down (+y-axis).
    public static double indicativeAngleWrtPoints(Points p1, Points p2) {
        double ang = 0.0;
        if (p1.getIntX() != p2.getIntX()) {
            ang = Math.atan2(p2.getIntY() - p1.getIntY(), p2.getIntX() - p1.getIntX());
        } 
        else { // pure vertical movement
            if (p2.getIntY() < p1.getIntY()){
                ang = -Math.PI / 2.0; // -90 degrees is straight up
            }
            else if (p2.getIntY() > p1.getIntY()){
                ang = Math.PI / 2.0; // 90 degrees is straight down
            }
        }
        ang = Math.toDegrees(ang);
        return ang;
    }
    
    public static double pathDistance(ArrayList<Points> input, ArrayList<Points> gesture) {
        int num_points = input.size();
        double dist = 0;
        int c = Math.min(input.size(), gesture.size());
        for (int i=0; i<c; i++){
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
        input = scaleByWandH(250, 250, input);
        input = relocateCentroid(new Points(0, 0), input);
        if (!processed) {
            int n_temp = Gesture.gestures_list.size();
            for (int i=0; i<n_temp; i++) {
                String name = Gesture.gestures_list.get(i).getName();
                ArrayList <Points> compare = Gesture.gestures_list.get(i).getPoints();
                compare = resampleInput(compare, RESAMPLE);
                compare = rotateToZero(compare);
                findExtremumXY(compare);
                compare = scaleByWandH(250, 250, compare);
                compare = relocateCentroid(new Points(0, 0), compare);
                Gesture.gestures_list.set(i, new GesturesList(name, compare));
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
        result = new ArrayList<>();
        int l = input.size();
        double b = Math.PI * 10000;
        int n_temp = Gesture.gestures_list.size();
        String temp_name = "";
        double score = 0.0;
        for (int i=0; i<n_temp; i++){
            double d = bestAngleDistance(input, Gesture.gestures_list.get(i).getPoints(),
                                                minAng, maxAng, angDel);
            if (d < b){
                b = d;
                temp_name = Gesture.gestures_list.get(i).getName();
            }
            score = 1 - (b / (.5*Math.sqrt((250*250) + (250*250))));
        }
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
                        compare = relocateCentroid(new Points(0, 0), compare);
                        inp = relocateCentroid(new Points(0, 0), inp);
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

