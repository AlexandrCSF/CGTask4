package com.cgvsu.math;

import java.util.ArrayList;

// Это заготовка для собственной библиотеки для работы с линейной алгеброй
public class Vector3d {

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Vector3d other) {
        // todo: желательно, чтобы это была глобальная константа
        final float eps = 1e-4f;
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps;
    }

    public double x;
    public double y;
    public double z;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3d multiplication(double num){
        return new Vector3d(getX() * num, getY() * num, getZ() * num);
    }

    public static Vector3d sum(Vector3d... vectors){
        double x = vectors[0].getX();
        double y = vectors[0].getY();
        double z = vectors[0].getZ();
        for (int i = 1; i < vectors.length; i++) {
            x += vectors[i].getX();
            y += vectors[i].getY();
            z += vectors[i].getZ();
        }
        return new Vector3d(x,y,z);
    }

    public static Vector3d sum(ArrayList<Vector3d> vectors){
        double x = vectors.get(0).getX();
        double y = vectors.get(0).getY();
        double z = vectors.get(0).getZ();
        for (int i = 1; i < vectors.size(); i++) {
            x += vectors.get(i).getX();
            y += vectors.get(i).getY();
            z += vectors.get(i).getZ();
        }
        return new Vector3d(x,y,z);
    }


    public Vector3d divide(float num){
        final float eps = 1e-7f;
        if(num - 0 < eps)
            throw new ArithmeticException("Division by zero");
        return new Vector3d(x / num, y / num, z / num);
    }

    public static Vector3d calculateCrossProduct(Vector3d vector1, Vector3d vector2){
        double x = vector1.getY()* vector2.getZ() - vector1.getZ()* vector2.getY();
        double y = vector1.getZ() * vector2.getX() - vector1.getX() * vector2.getZ();
        double z = vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX();
        return new Vector3d(x,y,z);
    }

    public static Vector3d fromTwoPoints(float x1, float y1, float z1, float x2, float y2, float z2){
        return new Vector3d(x2 - x1,y2-y1,z2-z1);
    }

    public static Vector3d fromTwoPoints(Vector3d vertex1, Vector3d vertex2){
        return new Vector3d(vertex2.getX() - vertex1.getX(), vertex2.getY() - vertex1.getY(), vertex2.getZ()- vertex1.getZ());
    }

    public void normalize(){
        double length = Math.sqrt(x * x + y * y + z * z);
        if(length != 0) {
            x /= length;
            y /= length;
            z /= length;
        }
    }
}
