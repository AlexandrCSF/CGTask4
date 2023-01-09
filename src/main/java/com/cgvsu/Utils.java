package com.cgvsu;

import javax.vecmath.Point2f;
import java.util.ArrayList;

public class Utils {
    public static double[] listOfPointsToArrayOfXs(final ArrayList<Point2f> points){
        double[] result = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            result[i] = points.get(i).x;
        }
        return result;
    }
    public static double[] listOfPointsToArrayOfYs(final ArrayList<Point2f> points){
        double[] result = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            result[i] = points.get(i).y;
        }
        return result;
    }
}
