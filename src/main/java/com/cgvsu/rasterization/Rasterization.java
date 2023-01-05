package com.cgvsu.rasterization;

import javafx.scene.canvas.Canvas;
import com.cgvsu.GuiController;
import com.cgvsu.Utils;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderStyle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Rasterization {

    public static void fillTriangle(
            final GraphicsUtils gr,
            MyPoint3D p1, MyPoint3D p2, MyPoint3D p3,
            MyColor myColor1, MyColor myColor2, MyColor myColor3,
            Double[][] zBuffer, Camera camera) {

        List<MyPoint3D> points = new ArrayList<>(Arrays.asList(p1, p2, p3));

        points.sort(Comparator.comparingDouble(MyPoint3D::getY));
        double cosLight;
        if (GuiController.renderProperties.get(RenderStyle.Light)) cosLight = Utils.getCosLight(camera, p1, p2, p3);
        else cosLight = 1;
        final double x1 = points.get(0).getX();
        final double x2 = points.get(1).getX();
        final double x3 = points.get(2).getX();
        final double y1 = points.get(0).getY();
        final double y2 = points.get(1).getY();
        final double y3 = points.get(2).getY();
        final double z1 = points.get(0).getZ();
        final double z2 = points.get(1).getZ();
        final double z3 = points.get(2).getZ();

        for (int y = (int) (y1 + 1); y <= y2; y++) {
            double startX = getX(y, x1, x2, y1, y2);
            double endX = getX(y, x1, x3, y1, y3);
            fillLine(gr, y, startX, endX, myColor1, myColor2, myColor3, x1, x2, x3, y1, y2, y3, z1, z2, z3, zBuffer, camera, cosLight);
        }

        for (int y = (int) (y2 + 1); y < y3; y++) {
            double startX = getX(y, x1, x3, y1, y3);
            double endX = getX(y, x2, x3, y2, y3);
            fillLine(gr, y, startX, endX, myColor1, myColor2, myColor3, x1, x2, x3, y1, y2, y3, z1, z2, z3, zBuffer, camera, cosLight);
        }
    }

    public static void fillTriangle(
            GraphicsUtils gr,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3,
            MyColor myColor1, MyColor myColor2, MyColor myColor3,
            Double[][] zBuffer, Camera camera) {
        fillTriangle(gr, new MyPoint3D(x1, y1, z1), new MyPoint3D(x2, y2, z2), new MyPoint3D(x3, y3, z3),
                myColor1, myColor2, myColor3, zBuffer, camera);
    }

    private static double getX(double y, double x1, double x2, double y1, double y2) {
        return (x2 - x1) * (y - y1) / (y2 - y1) + x1;
    }

    public static void drawLineWithZbuffer(GraphicsUtils gr,
                                           double x1, double y1, double z1,
                                           double x2, double y2, double z2,
                                           Color color1, Color color2,
                                           Double[][] zBuffer, Camera camera,
                                           Canvas canvas) {

        double dx = (x1 - x2);
        double dy = (y1 - y2);
        int step;
        if (Math.abs(dx) >= Math.abs(dy)) {
            step = (int) Math.abs(dx);
        } else {
            step = (int) Math.abs(dy);
        }

        dx = -(dx / step);
        dy = -(dy / step);
        int i = 0;
        double x = x1;
        double y = y1;
        double z = z1;

        double path = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
        double completedPath;
        double pathRatio;

        while (i <= step) {
                completedPath = Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y) + (z1 - z) * (z1 - z));
                pathRatio = completedPath / path;
                z = z1 + pathRatio * z2;
                if ((x > 0 && y > 0) && (x < canvas.getWidth() && y < canvas.getHeight()) && (zBuffer[(int) x][(int) y] == null ||
                        zBuffer[(int) x][(int) y] > Math.abs(z - camera.getPosition().z)))
                    gr.setPixel((int) x, (int) y, new MyColor(
                            color1.getRed() + pathRatio * color2.getRed(),
                            color1.getGreen() + pathRatio * color2.getGreen(),
                            color1.getBlue() + pathRatio * color2.getBlue()));
                x += dx;
                y += dy;
                ++i;
        }
    }

    public static void fillTriangleWithTexture(final GraphicsUtils gr,
                                               MyPoint3D p1, MyPoint3D p2, MyPoint3D p3,
                                               Double[][] zBuffer, Camera camera, int[][] texture,
                                               ArrayList<Integer> textureCordsIndices, Model mesh) {
        double[][] red = new double[texture.length][texture[0].length];
        double[][] green = new double[texture.length][texture[0].length];
        double[][] blue = new double[texture.length][texture[0].length];
        for (int i = 0; i < texture.length; i++) {
            for (int j = 0; j < texture[0].length; j++) {
                red[i][j] = ((texture[i][j] & 0xff0000) >> 16) / 255.0;
                green[i][j] = ((texture[i][j] & 0xff00) >> 8) / 255.0;
                blue[i][j] = (texture[i][j] & 0xff) / 255.0;
            }
        }

        List<MyPoint3D> points = new ArrayList<>(Arrays.asList(p1, p2, p3));

        points.sort(Comparator.comparingDouble(MyPoint3D::getY));
        double cosLight;
        if (GuiController.renderProperties.get(RenderStyle.Light)) cosLight = Utils.getCosLight(camera, p1, p2, p3);
        else cosLight = 1;
        final double x1 = points.get(0).getX();
        final double x2 = points.get(1).getX();
        final double x3 = points.get(2).getX();
        final double y1 = points.get(0).getY();
        final double y2 = points.get(1).getY();
        final double y3 = points.get(2).getY();
        final double z1 = points.get(0).getZ();
        final double z2 = points.get(1).getZ();
        final double z3 = points.get(2).getZ();

        for (int y = (int) (y1 + 1); y <= y2; y++) {
            double startX = getX(y, x1, x2, y1, y2);
            double endX = getX(y, x1, x3, y1, y3);
            fillLineWithTexture(gr, y, startX, endX, x1, x2, x3, y1, y2, y3, z1, z2, z3, zBuffer, camera, cosLight, red, green, blue);
        }

        for (int y = (int) (y2 + 1); y < y3; y++) {
            double startX = getX(y, x1, x3, y1, y3);
            double endX = getX(y, x2, x3, y2, y3);
            fillLineWithTexture(gr, y, startX, endX, x1, x2, x3, y1, y2, y3, z1, z2, z3, zBuffer, camera, cosLight, red, green, blue);
        }
    }

    private static void fillLineWithTexture(GraphicsUtils gr,
                                            int y, double startX, double endX,
                                            double x1, double x2, double x3,
                                            double y1, double y2, double y3,
                                            double z1, double z2, double z3,
                                            Double[][] zBuffer, Camera camera, double cosLight,
                                            double[][] red, double[][] green, double[][] blue) {

        if (Double.compare(startX, endX) > 0) {
            double temp = startX;
            startX = endX;
            endX = temp;
        }

        for (int x = (int) startX + 1; x < endX; x++) {
            double z = Utils.getZ(new MyPoint3D(x1, y1, z1), new MyPoint3D(x2, y2, z2), new MyPoint3D(x3, y3, z3), x, y);
            if (x >= 0 && y >= 0) {
                if (zBuffer[x][y] == null || zBuffer[x][y] > Math.abs(z - camera.getPosition().z)) {
                    gr.setPixel(x, y, new MyColor(red[y][x] * cosLight, green[y][x] * cosLight, blue[y][x] * cosLight));
                    zBuffer[x][y] = Math.abs(z - camera.getPosition().z);
                }
            }
        }
    }

    private static void fillLine(
            final GraphicsUtils gr, int y, double startX, double endX,
            MyColor myColor1, MyColor myColor2, MyColor myColor3,
            double x1, double x2, double x3,
            double y1, double y2, double y3,
            double z1, double z2, double z3,
            Double[][] zBuffer, Camera camera, double cosLight) {

        if (Double.compare(startX, endX) > 0) {
            double temp = startX;
            startX = endX;
            endX = temp;
        }

        for (int x = (int) startX + 1; x < endX; x++) {
            double z = Utils.getZ(new MyPoint3D(x1, y1, z1), new MyPoint3D(x2, y2, z2), new MyPoint3D(x3, y3, z3), x, y);
            if (x >= 0 && y >= 0) {
                if (zBuffer[x][y] == null || zBuffer[x][y] > Math.abs(z - camera.getPosition().z)) {
                    gr.setPixel(x, y, getColor(myColor1, myColor2, myColor3, x, y, x1, x2, x3, y1, y2, y3, cosLight));
                    zBuffer[x][y] = Math.abs(z - camera.getPosition().z);
                }
            }
        }
    }


    private static MyColor getColor(
            MyColor myColor1, MyColor myColor2, MyColor myColor3,
            double x, double y,
            double x1, double x2, double x3,
            double y1, double y2, double y3,
            double cosLight) {

        double detT = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);

        double alpha = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / detT;

        double betta = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / detT;

        double gamma = 1 - alpha - betta;

        double r = (alpha * myColor1.getRed() + betta * myColor2.getRed() + gamma * myColor3.getRed());
        double g = (alpha * myColor1.getGreen() + betta * myColor2.getGreen() + gamma * myColor3.getGreen());
        double b = (alpha * myColor1.getBlue() + betta * myColor2.getBlue() + gamma * myColor3.getBlue());

        double rLight = r * cosLight;
        double gLight = g * cosLight;
        double bLight = b * cosLight;

        return new MyColor(rLight, gLight, bLight);
    }

}