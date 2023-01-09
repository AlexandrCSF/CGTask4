package com.cgvsu.math;

import java.util.ArrayList;

// Это заготовка для собственной библиотеки для работы с линейной алгеброй
public class Vector3f {

    public Vector3f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public boolean equals(Vector3f other) {
        // todo: желательно, чтобы это была глобальная константа
        final float eps = 1e-4f;
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps;
    }

    public float x;
    public float y;
    public float z;
    public float w = 1;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
    public float getW() {
        return w;
    }

    public Vector3f multiplication(float num){
        return new Vector3f(getX() * num, getY() * num, getZ() * num);
    }

    public static Vector3f sum(Vector3f ... vectors){
        float x = vectors[0].getX();
        float y = vectors[0].getY();
        float z = vectors[0].getZ();
        for (int i = 1; i < vectors.length; i++) {
            x += vectors[i].getX();
            y += vectors[i].getY();
            z += vectors[i].getZ();
        }
        return new Vector3f(x,y,z);
    }

    public static Vector3f sum(ArrayList<Vector3f> vectors){
        float x = vectors.get(0).getX();
        float y = vectors.get(0).getY();
        float z = vectors.get(0).getZ();
        for (int i = 1; i < vectors.size(); i++) {
            x += vectors.get(i).getX();
            y += vectors.get(i).getY();
            z += vectors.get(i).getZ();
        }
        return new Vector3f(x,y,z);
    }


    public Vector3f divide(float num){
        final float eps = 1e-7f;
        if(num - 0 < eps)
            throw new ArithmeticException("Division by zero");
        return new Vector3f(x / num, y / num, z / num);
    }

    public static Vector3f calculateCrossProduct(Vector3f vector1,Vector3f vector2){
        float x = vector1.getY()* vector2.getZ() - vector1.getZ()* vector2.getY();
        float y = vector1.getZ() * vector2.getX() - vector1.getX() * vector2.getZ();
        float z = vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX();
        return new Vector3f(x,y,z);
    }

    public static Vector3f fromTwoPoints(float x1, float y1, float z1, float x2, float y2, float z2){
        return new Vector3f(x2 - x1,y2-y1,z2-z1);
    }

    public static Vector3f fromTwoPoints(Vector3f vertex1, Vector3f vertex2){
        return new Vector3f(vertex2.getX() - vertex1.getX(), vertex2.getY() - vertex1.getY(), vertex2.getZ()- vertex1.getZ());
    }

    public void applyMatrix(float[][] matrix) {
        Vector3f result = Matrix.multiplyVector(matrix, this);
        this.x = result.x;
        this.y = result.y;
        this.z = result.z;
    }
}
