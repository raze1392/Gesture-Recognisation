Gesture Recognition / Devnagiri

Devnagiri is an open source gesture recognition built on Java. It works on Gss and Knn algorithm to find the best match of the input provided by the user.

It has a gestures.xml file which contains a list of all the gestures that has been defined. The best thing about Devnagiri is that you can also add custom gestures to the list.

This build is divided into three packages.
* recog.core -> Contains all the core utilities required for processing and matching the input.
* recog.gesture -> Utilities to parse and edit gestures.xml file.
* recog.main -> It is the main class that takes the input and call functions to process it. It also holds the result of the Algorithm.

Files List :

CoreUtilities.java
This file contains all the methods that are required to process the input. It also contains the GSS and KNN algorithm. All the methods inside this file are static thus, no instantiation is needed.

Gesture.java
This file is responsible for parsing gestures.xml file and create a list of all gestures included within.

GestureBuilder.java
This file edits the gestures.xml by adding to it the custom gesture.

RecognizerResult.java
The purpose of this file is to hold all the best matches for a particular gesture.

Point.java
A class file to hold a point coordinate i.e., X and Y coordinate of the input.

GesturesList.java
This file holds the list of all the gestures parsed by Gestures.java. It creates a GestureList object that contains the name of the gestures and all the points of the coordinate.

List of Methods in CoreUtilities:

    public static double calculatePathLength (ArrayList<Point> point)

    public static double calculateDistance(Point p1, Point p2) 

    public static Point calculateCentroid (ArrayList<Point> point) 
    
    public static void findExtremumXY (ArrayList<Point> point) 
    
    public static Rectangle createBound () 
    
    public static ArrayList<Point> scaleByPercent (float scale, ArrayList<Point> point) 
    
    public static ArrayList<Point> scaleByWandH (int wX, int wY, ArrayList<Point> point) 
    
    public static ArrayList<Point> relocateCentroid (Point newCtd, ArrayList<Point> point) 
    
    public static ArrayList<Point> resampleInput (ArrayList<Point> input, int resample_num) 
          Resamples no. of points to N equidistant points based on interpolation

    public static ArrayList <Point> rotateByAngle(ArrayList<Point> point, double angle) 
          Rotate a point 'p' around a point 'c' by the given radians. Rotation (around the origin) amounts to a 2x2 matrix of the form:
                [ cos A		-sin A	] [ p.x ]
                [ sin A		cos A	] [ p.y ]
          Note that the C# Math coordinate system has +x-axis stright right and +y-axis straight down. Rotation is clockwise such that from +x-axis to +y-axis is +90 degrees, from +x-axis to -x-axis is +180 degrees, and from +x-axis to -y-axis is -90 degrees.
    
    public static ArrayList <Point> rotateToZero(ArrayList<Point> input) 
          rotate every point in the input to an angle that makes the angle between 1st point and the centroid 0 degrees.

    public static double indicativeAngleWrtCentroid(ArrayList<Point> input) 
          calculate the angle between the centroid and the first point of the gesture.
    
    public static double indicativeAngleWrtPoint(Point p1, Point p2) 
          determines the angle, in radians, between two points. the angle is defined by the circle centered on the start point with a radius to the end point, where 0 radians is straight right from start (+x-axis) and PI/2 radians is straight down (+y-axis).

    public static double pathDistance(ArrayList<Point> input, ArrayList<Point> gesture) 
    
    public static double pathByAngle(ArrayList<Point> input, ArrayList<Point> gesture, double angle) 
    
    public static double bestAngleDistance(ArrayList<Point> input, ArrayList<Point> gesture, 
                                                        double angA ,double angB, double angDelta)

    public static ArrayList<Point> preProcessing (ArrayList<Point> input, Gesture temp)
        

More detail about it soon. Till then Fork it, hack it, and if you think others would benefit, issue a pull request on this repo.

Happy Coding!