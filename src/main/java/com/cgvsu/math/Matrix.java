package com.cgvsu.math;

public class Matrix {

    public static float[][] multiply(float[][] a, float[][] b) {
        float[][] m = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m[i][j] = a[i][0] * b[0][j] +
                        a[i][1] * b[1][j] +
                        a[i][2] * b[2][j] +
                        a[i][3] * b[3][j];
            }
        }
        return m;
    }

    public static Vector3f multiplyVector(float[][] m, Vector3f v) {
        return new Vector3f(
                m[0][0] * v.getX() + m[0][1] * v.getY() + m[0][2] * v.getZ() + m[0][3] * v.getW(),
                m[1][0] * v.getX() + m[1][1] * v.getY() + m[1][2] * v.getZ() + m[1][3] * v.getW(),
                m[2][0] * v.getX() + m[2][1] * v.getY() + m[2][2] * v.getZ() + m[2][3] * v.getW(),
                m[3][0] * v.getX() + m[3][1] * v.getY() + m[3][2] * v.getZ() + m[3][3] * v.getW()
        );
    }

    public static float[][] getTranslation(float dx, float dy, float dz) {
        return new float[][]{
                {1, 0, 0, dx},
                {0, 1, 0, dy},
                {0, 0, 1, dz},
                {0, 0, 0, 1}
        };
    }

    public static float[][] getScale(float sx, float sy, float sz) {
        return new float[][]{
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, 0},
                {0, 0, 0,  1}
        };
    }

    /////Поворот

    public static float[][] getRotationX(float angle) {
        float rad = (float)(Math.PI / 180 * angle);

        return new float[][]{
                {1,         0,                     0,              0},
                {0, (float)Math.cos(rad), (float)-Math.sin(rad),   0},
                {0, (float)Math.sin(rad), (float)Math.cos(rad),    0},
                {0,         0,                     0,              1}
        };
    }

    public static float[][] getRotationY(float angle) {
        float rad = (float)(Math.PI / 180 * angle);

        return new float[][]{
                {(float)Math.cos(rad),  0, (float)Math.sin(rad), 0},
                {        0,             1,            0,         0},
                {(float)-Math.sin(rad), 0, (float)Math.cos(rad), 0},
                {        0,             0,            0,         1}
        };
    }

    public static float[][] getRotationZ(float angle) {
        float rad = (float)(Math.PI / 180 * angle);

        return new float[][]{
                {(float)Math.cos(rad), (float)-Math.sin(rad), 0,       0},
                {(float)Math.sin(rad), (float)Math.cos(rad),  0,       0},
                {        0,                   0,              1,       0},
                {        0,                   0,              0,       1}
        };
    }
}


