package com.cgvsu.render_engine;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.cgvsu.math.Vector3f;
import com.cgvsu.rasterization.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javax.vecmath.*;

import com.cgvsu.model.Model;
import javafx.scene.paint.Color;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            Color fillColor,
            HashMap<RenderStyle, Boolean> renderProperties,
            BufferedImage texture) throws IOException {
        double redColor = fillColor.getRed();
        double greenColor = fillColor.getGreen();
        double blueColor = fillColor.getBlue();

        GraphicsUtils<Canvas> graphicsUtils = new DrawUtilsJavaFX(graphicsContext.getCanvas());
        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        final int nPolygons = mesh.polygons.size();

        Double[][] zBuffer = new Double[width][height];

        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            List<Double> pointsZ = new ArrayList<>();
            ArrayList<Point2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z);

                pointsZ.add((double) vertex.z);

                Point2f resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath), width, height);
                resultPoints.add(resultPoint);
            }

            if (renderProperties.get(RenderStyle.Polygonal_Grid)) {
                for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                    Rasterization.drawLineWithZbuffer(graphicsUtils,
                            resultPoints.get(vertexInPolygonInd - 1).x,
                            resultPoints.get(vertexInPolygonInd - 1).y,
                            pointsZ.get(vertexInPolygonInd - 1),
                            resultPoints.get(vertexInPolygonInd).x,
                            resultPoints.get(vertexInPolygonInd).y,
                            pointsZ.get(vertexInPolygonInd),
                            Color.BLACK,Color.BLACK,
                            zBuffer,camera ,graphicsContext.getCanvas());
                }

                if (nVerticesInPolygon > 0)
                    Rasterization.drawLineWithZbuffer(graphicsUtils,
                            resultPoints.get(nVerticesInPolygon - 1).x,
                            resultPoints.get(nVerticesInPolygon - 1).y,
                            pointsZ.get(nVerticesInPolygon - 1),
                            resultPoints.get(0).x,
                            resultPoints.get(0).y,
                            pointsZ.get(0),
                            Color.BLACK,Color.BLACK,
                            zBuffer,camera,graphicsContext.getCanvas());
            }
            if (renderProperties.get(RenderStyle.Color_Fill)) {
                Rasterization.fillTriangle(graphicsUtils, new MyPoint3D(resultPoints.get(0).x, resultPoints.get(0).y, pointsZ.get(0)),
                        new MyPoint3D(resultPoints.get(1).x,resultPoints.get(1).y, pointsZ.get(1)),
                        new MyPoint3D(resultPoints.get(2).x, resultPoints.get(2).y, pointsZ.get(2)),
                        new MyColor(redColor, greenColor, blueColor),
                        new MyColor(redColor, greenColor, blueColor),
                        new MyColor(redColor, greenColor, blueColor),
                        zBuffer,camera);
            }
            if (renderProperties.get(RenderStyle.Texture)) {
                Rasterization.fillTriangle(graphicsUtils,
                        resultPoints.get(0).x, resultPoints.get(0).y, pointsZ.get(0),
                        resultPoints.get(1).x, resultPoints.get(1).y, pointsZ.get(1),
                        resultPoints.get(2).x, resultPoints.get(2).y, pointsZ.get(2),
                        MyColor.RED, MyColor.RED, MyColor.RED, zBuffer, camera, texture,
                        mesh.textureVertices.get(mesh.polygons.get(polygonInd).getTextureVertexIndices().get(0)),
                        mesh.textureVertices.get(mesh.polygons.get(polygonInd).getTextureVertexIndices().get(1)),
                        mesh.textureVertices.get(mesh.polygons.get(polygonInd).getTextureVertexIndices().get(2)));
            }
        }
        for (Double[] doubles : zBuffer) {
            Arrays.fill(doubles, null);
        }
    }
}