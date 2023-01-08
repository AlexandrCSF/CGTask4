package com.cgvsu.rasterization;

import com.cgvsu.model.Model;
import com.cgvsu.model.ModelUtils;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Rasterization {

    public static void fillPolygon(final GraphicsUtils gr,final Polygon polygon,final Model model){
        ArrayList<Integer> polygonVertexIndices = polygon.getVertexIndices();
        if(polygonVertexIndices.size() < 3)
            return;
        if(polygonVertexIndices.size() == 3)
            fillTriangle(gr,new MyPoint2D(model.vertices.get(polygonVertexIndices.get(0)).x,model.vertices.get(polygonVertexIndices.get(0)).y),
                    new MyPoint2D(model.vertices.get(polygonVertexIndices.get(1)).x,model.vertices.get(polygonVertexIndices.get(1)).y),
                    new MyPoint2D(model.vertices.get(polygonVertexIndices.get(2)).x,model.vertices.get(polygonVertexIndices.get(2)).y),
                    new MyColor(1,0,0),new MyColor(1,0,0),new MyColor(1,0,0));
        else {
            ArrayList<Polygon> triangles = ModelUtils.triangulatePolygon(polygon);
            for (Polygon triangle : triangles) {
                fillPolygon(gr, triangle, model);
            }
        }
    }

    public static void fillTriangle(
            final GraphicsUtils gr,
            MyPoint2D p1, MyPoint2D p2, MyPoint2D p3,
            MyColor myColor1, MyColor myColor2, MyColor myColor3) {


        List<MyPoint2D> points = new ArrayList<>(Arrays.asList(p1, p2, p3));

        points.sort(Comparator.comparingDouble(MyPoint2D::getY));

        final double x1 = points.get(0).getX();
        double x2 = points.get(1).getX();
        double x3 = points.get(2).getX();
        double y1 = points.get(0).getY();
        double y2 = points.get(1).getY();
        double y3 = points.get(2).getY();

        for (int y = (int) Math.round(y1 + 0.5); y <= y2; y++) {
            double startX = getX(y, x1, x2, y1, y2);
            double endX = getX(y, x1, x3, y1, y3);

            fillLine(gr, y, startX, endX, myColor1, myColor2, myColor3, x1, x2, x3, y1, y2, y3);
        }

        for (int y = (int) Math.round(y2 + 0.5); y < y3; y++) {
            double startX = getX(y, x1, x3, y1, y3);
            double endX = getX(y, x2, x3, y2, y3);
            fillLine(gr, y, startX, endX, myColor1, myColor2, myColor3, x1, x2, x3, y1, y2, y3);
        }
    }

    public static void fillTriangle(
            GraphicsUtils gr,
            double x1, double y1,
            double x2, double y2,
            double x3, double y3,
            MyColor myColor1, MyColor myColor2, MyColor myColor3) {
        fillTriangle(gr, new MyPoint2D(x1, y1), new MyPoint2D(x2, y2), new MyPoint2D(x3, y3), myColor1, myColor2, myColor3);
    }

    private static double getX(double y, double x1, double x2, double y1, double y2) {
        return (x2 - x1) * (y - y1) / (y2 - y1) + x1;
    }

    private static void fillLine(
            final GraphicsUtils gr, int y, double startX, double endX,
            MyColor myColor1, MyColor myColor2, MyColor myColor3,
            double x1, double x2, double x3,
            double y1, double y2, double y3) {

        if (Double.compare(startX, endX) > 0) {
            double temp = startX;
            startX = endX;
            endX = temp;
        }

        for (int x = (int) Math.round(startX + 0.5); x < endX; x++) {
            gr.setPixel(x, y, getColor(myColor1, myColor2, myColor3, x, y, x1, x2, x3, y1, y2, y3));
        }
    }


    private static MyColor getColor(
            MyColor myColor1, MyColor myColor2, MyColor myColor3,
            double x, double y,
            double x1, double x2, double x3,
            double y1, double y2, double y3) {

        double detT = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);

        double alpha = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / detT;

        double betta = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / detT;

        double gamma = 1 - alpha - betta;

        double r = (alpha * myColor1.getRed() + betta * myColor2.getRed() + gamma * myColor3.getRed());
        double g = (alpha * myColor1.getGreen() + betta * myColor2.getGreen() + gamma * myColor3.getGreen());
        double b = (alpha * myColor1.getBlue() + betta * myColor2.getBlue() + gamma * myColor3.getBlue());

        return new MyColor(r, g, b);
    }

}
