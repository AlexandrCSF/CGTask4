
package com.cgvsu.rasterization;


import com.cgvsu.GuiController;
import com.cgvsu.Utils;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3d;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderStyle;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Rasterization {

    public static void fillTriangleWithTexture(
            final GraphicsUtils gr,
            Vector3d p1, Vector3d p2, Vector3d p3,
            MyColor myColor1, MyColor myColor2, MyColor myColor3,
            Double[][] zBuffer, Camera camera) {

        List<Vector3d> points = new ArrayList<>(Arrays.asList(p1, p2, p3));

        points.sort(Comparator.comparingDouble(Vector3d::getY));
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
            double z = Utils.getZ(new Vector3d(x1, y1, z1), new Vector3d(x2, y2, z2), new Vector3d(x3, y3, z3), x, y);
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

    public static void fillTriangleWithTexture(
            final GraphicsUtils gr,
            Vector3d p1, Vector3d p2, Vector3d p3,
            MyColor myColor1, MyColor myColor2, MyColor myColor3,
            Double[][] zBuffer, Camera camera, BufferedImage image,
            Vector2f texturePoint1, Vector2f texturePoint2, Vector2f texturePoint3) throws IOException {

        List<Vector3d> points = new ArrayList<>(Arrays.asList(p1, p2, p3));


        if (points.get(0).getY() > points.get(1).getY()) {
            Vector3d tmp = points.get(1);
            points.set(1, points.get(0));
            points.set(0, tmp);
            Vector2f tmp1 = texturePoint1;
            texturePoint1 = texturePoint2;
            texturePoint2 = tmp1;
        }
        if (points.get(1).getY() > points.get(2).getY()) {
            Vector3d tmp = points.get(2);
            points.set(2, points.get(1));
            points.set(1, tmp);
            Vector2f tmp1 = texturePoint2;
            texturePoint2 = texturePoint3;
            texturePoint3 = tmp1;
            if (points.get(0).getY() > points.get(1).getY()) {
                Vector3d tmp2 = points.get(1);
                points.set(1, points.get(0));
                points.set(0, tmp2);
                Vector2f tmp3 = texturePoint1;
                texturePoint1 = texturePoint2;
                texturePoint2 = tmp3;
            }
        }

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
            fillLine(gr, y, startX, endX, myColor1, myColor2, myColor3, x1, x2, x3, y1, y2, y3, z1, z2, z3, zBuffer, camera, cosLight, image, texturePoint1, texturePoint2, texturePoint3);
        }

        for (int y = (int) (y2 + 1); y < y3; y++) {
            double startX = getX(y, x1, x3, y1, y3);
            double endX = getX(y, x2, x3, y2, y3);
            fillLine(gr, y, startX, endX, myColor1, myColor2, myColor3, x1, x2, x3, y1, y2, y3, z1, z2, z3, zBuffer, camera, cosLight, image, texturePoint1, texturePoint2, texturePoint3);
        }
    }

    public static void fillTriangleWithTexture(
            GraphicsUtils gr,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3,
            MyColor myColor1, MyColor myColor2, MyColor myColor3,
            Double[][] zBuffer, Camera camera, BufferedImage image,
            Vector2f texturePoint1, Vector2f texturePoint2, Vector2f texturePoint3) throws IOException {
        fillTriangleWithTexture(gr, new Vector3d((float) x1, (float) y1, (float) z1), new Vector3d((float) x2, (float) y2, (float) z2),
                new Vector3d((float) x3, (float) y3, (float) z3),
                myColor1, myColor2, myColor3, zBuffer, camera, image, texturePoint1, texturePoint2, texturePoint3);
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

    private static void fillLine(
            final GraphicsUtils gr, int y, double startX, double endX,
            MyColor myColor1, MyColor myColor2, MyColor myColor3,
            double x1, double x2, double x3,
            double y1, double y2, double y3,
            double z1, double z2, double z3,
            Double[][] zBuffer, Camera camera, double cosLight, BufferedImage image,
            Vector2f texturePoint1, Vector2f texturePoint2, Vector2f texturePoint3){

        if (Double.compare(startX, endX) > 0) {
            double temp = startX;
            startX = endX;
            endX = temp;
        }

        for (int x = (int) startX + 1; x < endX; x++) {
            double z = Utils.getZ(new Vector3d(x1, y1, z1), new Vector3d(x2, y2, z2), new Vector3d(x3, y3, z3), x, y);
            if (x >= 0 && y >= 0) {
                if (zBuffer[x][y] == null || zBuffer[x][y] > Math.abs(z - camera.getPosition().z)) {
                    MyColor color = getColor(myColor1, myColor2, myColor3, x, y, x1, x2, x3, y1, y2, y3, image,
                            texturePoint1, texturePoint2, texturePoint3);
                    gr.setPixel(x, y, new MyColor(color.getRed() * cosLight, color.getGreen() * cosLight, color.getBlue() * cosLight));
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
            BufferedImage image, Vector2f texturePoint1, Vector2f texturePoint2, Vector2f texturePoint3) {
        if (!GuiController.renderProperties.get(RenderStyle.Texture)) {
            double detT = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);

            double alpha = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / detT;

            double betta = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / detT;

            double gamma = 1 - alpha - betta;

            double r = (alpha * myColor1.getRed() + betta * myColor2.getRed() + gamma * myColor3.getRed());
            double g = (alpha * myColor1.getGreen() + betta * myColor2.getGreen() + gamma * myColor3.getGreen());
            double b = (alpha * myColor1.getBlue() + betta * myColor2.getBlue() + gamma * myColor3.getBlue());

            return new MyColor(r, g, b);

        } else {

            float detT = (1.0f / (float) ((x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1)));
            float alpha = (float) ((x2 - x) * (y3 - y) - (y2 - y) * (x3 - x)) * detT;
            float betta = (float) ((x3 - x) * (y1 - y) - (y3 - y) * (x1 - x)) * detT;
            float gamma = 1.0f - (alpha + betta);

            double resultX = alpha * texturePoint1.getX() + betta * texturePoint2.getX() + gamma * texturePoint3.getX();
            double resultY = alpha * texturePoint1.getY() + betta * texturePoint2.getY() + gamma * texturePoint3.getY();
            return getColorTexture(resultX, resultY, image);
        }
    }

    public static MyColor getColorTexture(double x0, double y0, BufferedImage image) {
        int width = image.getWidth() - 1;
        int height = image.getHeight() - 1;
        int x = (int) (x0 * width);
        int y = (int) ((1 - y0) * height);

        int color = image.getRGB(x, y);
        double red = ((color & 0x00ff0000) >> 16) / 255.0f;
        double green = ((color & 0x0000ff00) >> 8) / 255.0f;
        double blue = (color & 0x000000ff) / 255.0f;
        return new MyColor(red, green, blue);
    }

}