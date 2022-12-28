package com.cgvsu;

import com.cgvsu.rasterization.MyPoint3D;
import com.cgvsu.render_engine.Camera;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
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
    public static Vector3d getNormal(MyPoint3D p1, MyPoint3D p2, MyPoint3D p3) {
        Vector3d p1p2 = new Vector3d(p2.getX() - p1.getX(), p2.getY() - p1.getY(), p2.getZ() - p1.getZ());
        Vector3d p1p3 = new Vector3d(p3.getX() - p1.getX(), p3.getY() - p1.getY(), p3.getZ() - p1.getZ());
        double A = p1p2.y * p1p3.z - p1p2.z * p1p3.y;
        double B = -(p1p2.x * p1p3.z - p1p2.z * p1p3.x);
        double C = p1p2.x * p1p3.y - p1p2.y * p1p3.x;
        //double normalLength = Math.sqrt(A * A + B * B + C * C);
        //return new Vector3d(A/normalLength, B/normalLength, C/normalLength);
        return new Vector3d(A, B, C);
    }

    public static double getZ(MyPoint3D p1, MyPoint3D p2, MyPoint3D p3, double x, double y) {
        Vector3d normal = getNormal(p1, p2, p3);
        return (-normal.x * (x - p1.getX()) - normal.y * (y - p1.getY())) / normal.z + p1.getZ();
    }

    public static double getCosLight(Camera camera, MyPoint3D p1, MyPoint3D p2, MyPoint3D p3) {
        Vector3d normal = getNormal(p1, p2, p3);
        double cosLight = Math.abs(
                (camera.getPosition().x * normal.x + camera.getPosition().y * normal.y + camera.getPosition().z * normal.z) /
                        ((Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z)) *
                                (Math.sqrt(camera.getPosition().x * camera.getPosition().x
                                        + camera.getPosition().y * camera.getPosition().y
                                        + camera.getPosition().z * camera.getPosition().z))));
        return cosLight;
    }

    public static Color[][] convertImageToIntArray(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
            }
        }
        return null;
    }
}
