package com.cgvsu.model;

import com.cgvsu.math.Vector3f;

import java.time.chrono.MinguoDate;
import java.util.ArrayList;

import static com.cgvsu.math.Vector3f.*;
import static com.cgvsu.math.Vector3f.sum;

public class ModelUtils {

    public static void recalculateNormals(Model model) {
        model.normals.clear();
        for (int i = 0; i < model.vertices.size(); i++) {
            model.normals.add(calculateNormalForVertexInModel(model, i));
        }
    }

    protected static Vector3f calculateNormalForPolygon(final Polygon polygon, final Model model){

        ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
        int verticesCount = vertexIndices.size();

        Vector3f vector1 = fromTwoPoints(model.vertices.get(vertexIndices.get(0)), model.vertices.get(vertexIndices.get(1)));
        Vector3f vector2 = fromTwoPoints(model.vertices.get(vertexIndices.get(0)), model.vertices.get(vertexIndices.get(verticesCount - 1)));

        return calculateCrossProduct(vector1, vector2);
    }

    protected static Vector3f calculateNormalForVertexInModel(final Model model, final int vertexIndex) {
        ArrayList<Vector3f> saved = new ArrayList<>();
        for (Polygon polygon : model.polygons) {
            if (polygon.getVertexIndices().contains(vertexIndex)) {
                saved.add(calculateNormalForPolygon(polygon, model));
            }
        }
        return sum(saved).divide(saved.size());
    }

    public static ArrayList<Polygon> triangulatePolygon(Polygon polygon) {
        ArrayList<Polygon> triangles = new ArrayList<>();
        int index = 0;
        while (polygon.getVertexIndices().size() > 2) {

            Polygon triangle = new Polygon();

            // Добавляем вершины в треугольник
            triangle.getVertexIndices().add(
                    polygon.getVertexIndices().get(index));
            triangle.getVertexIndices().add(
                    polygon.getVertexIndices().get(index + 1));
            triangle.getVertexIndices().add(
                    polygon.getVertexIndices().get(index + 2));


            if (polygon.getTextureVertexIndices().size() != 0) {
                triangle.getTextureVertexIndices().add(
                        polygon.getTextureVertexIndices().get(index));
                triangle.getTextureVertexIndices().add(
                        polygon.getTextureVertexIndices().get(index + 1));
                triangle.getTextureVertexIndices().add(
                        polygon.getTextureVertexIndices().get(index + 2));
            }

            if (polygon.getNormalIndices().size() != 0) {
                triangle.getNormalIndices().add(
                        polygon.getNormalIndices().get(index));
                triangle.getNormalIndices().add(
                        polygon.getNormalIndices().get(index + 1));
                triangle.getNormalIndices().add(
                        polygon.getNormalIndices().get(index + 2));
            }


            polygon.getVertexIndices().remove(index + 1);
            triangles.add(triangle);
        }
        return triangles;
    }
    public static void triangulatePolygons(Model model) {
        ArrayList<Polygon> triangles = new ArrayList<>();
        for (Polygon polygon : model.polygons) {
            int index = 0;
            while (polygon.getVertexIndices().size() > 2) {

                Polygon triangle = new Polygon();

                // Добавляем вершины в треугольник
                triangle.getVertexIndices().add(
                        polygon.getVertexIndices().get(index));
                triangle.getVertexIndices().add(
                        polygon.getVertexIndices().get(index + 1));
                triangle.getVertexIndices().add(
                        polygon.getVertexIndices().get(index + 2));


                if (polygon.getTextureVertexIndices().size() != 0) {
                    triangle.getTextureVertexIndices().add(
                            polygon.getTextureVertexIndices().get(index));
                    triangle.getTextureVertexIndices().add(
                            polygon.getTextureVertexIndices().get(index + 1));
                    triangle.getTextureVertexIndices().add(
                            polygon.getTextureVertexIndices().get(index + 2));
                }

                if (polygon.getNormalIndices().size() != 0) {
                    triangle.getNormalIndices().add(
                            polygon.getNormalIndices().get(index));
                    triangle.getNormalIndices().add(
                            polygon.getNormalIndices().get(index + 1));
                    triangle.getNormalIndices().add(
                            polygon.getNormalIndices().get(index + 2));
                }


                polygon.getVertexIndices().remove(index + 1);
                triangles.add(triangle);
            }

            if (polygon.getVertexIndices().size() < 3) { //Убираем из массива точки, которые уже построились
                polygon.getVertexIndices().clear();
            }
        }
        model.polygons.clear();
        model.polygons = triangles;
    }
}
