package com.cgvsu;

import com.cgvsu.math.Vector3d;
import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.Camera;
import javafx.collections.ObservableList;


public class Utils {
    public static Vector3d getNormal(Vector3d p1, Vector3d p2, Vector3d p3) {
        Vector3d p1p2 = new Vector3d(p2.getX() - p1.getX(), p2.getY() - p1.getY(), p2.getZ() - p1.getZ());
        Vector3d p1p3 = new Vector3d(p3.getX() - p1.getX(), p3.getY() - p1.getY(), p3.getZ() - p1.getZ());
        return Vector3d.calculateCrossProduct(p1p2, p1p3);
    }

    public static double getZ(Vector3d p1, Vector3d p2, Vector3d p3, double x, double y) {
        Vector3d normal = getNormal(p1, p2, p3);
        return (-normal.x * (x - p1.getX()) - normal.y * (y - p1.getY())) / normal.z + p1.getZ();
    }

    public static double getCosLight(Camera camera, Vector3d p1, Vector3d p2, Vector3d p3) {
        Vector3d normal = getNormal(p1, p2, p3);
        normal.normalize();
        Vector3d target = Utils.getNormalizedVector(Utils.minus(new Vector3f(camera.getPosition()),new Vector3f(camera.getTarget())));
        return Math.abs(dotProduct(normal, target));
    }

    public static Vector3d getNormalizedVector(Vector3d vector) {
        double length = Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
        if (length != 0)
            return new Vector3d(vector.x / length, vector.y / length, vector.z / length);
        return new Vector3d(0,0,0);
    }

    public static String vector3ftoString(javax.vecmath.Vector3f vector3f) {
        return "x:" + String.format(String.valueOf(vector3f.x), 3) + " y:" +
                String.format(String.valueOf(vector3f.y), 3) + " z:" + String.format(String.valueOf(vector3f.z), 3);
    }

    public static void recalculateIndexes(ObservableList<Camera> cameras, ObservableList<String> listOfCameras) {
        for (int i = 0; i < cameras.size(); i++) {
            listOfCameras.add(i + Utils.vector3ftoString(cameras.get(i).getPosition()));
            listOfCameras.remove(0);
        }
    }

    public static double dotProduct(Vector3d vector1, Vector3d vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y + vector1.z * vector2.z;
    }

    public static Vector3d minus(Vector3f vector1 , Vector3f vector2){
        return new Vector3d(vector2.x - vector1.x , vector2.y - vector1.y, vector2.z - vector1.z);
    }
}
