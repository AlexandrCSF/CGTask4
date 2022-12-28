package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.HashMap;

import com.cgvsu.Utils;
import com.cgvsu.math.Vector3f;
import com.cgvsu.rasterization.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javax.vecmath.*;
import com.cgvsu.model.Model;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {


    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            Color fillColor,
            HashMap<RenderStyle,Boolean> renderProperties,
            Color[][] texture)
    {
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
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            ArrayList<Point2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z);

                Point2f resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath), width, height);
                resultPoints.add(resultPoint);
            }

            if (renderProperties.get(RenderStyle.Polygonal_Grid)) {
                for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                    graphicsContext.strokeLine(
                            resultPoints.get(vertexInPolygonInd - 1).x,
                            resultPoints.get(vertexInPolygonInd - 1).y,
                            resultPoints.get(vertexInPolygonInd).x,
                            resultPoints.get(vertexInPolygonInd).y);
                }

                if (nVerticesInPolygon > 0)
                    graphicsContext.strokeLine(
                            resultPoints.get(nVerticesInPolygon - 1).x,
                            resultPoints.get(nVerticesInPolygon - 1).y,
                            resultPoints.get(0).x,
                            resultPoints.get(0).y);

            } if(renderProperties.get(RenderStyle.Color_Fill)){
                Rasterization.fillTriangle(graphicsUtils,new MyPoint2D(resultPoints.get(0).x,resultPoints.get(0).y),
                    new MyPoint2D(resultPoints.get(1).x,resultPoints.get(1).y), new MyPoint2D(resultPoints.get(2).x,resultPoints.get(2).y),
                    new MyColor(redColor,greenColor,blueColor),new MyColor(redColor,greenColor,blueColor),new MyColor(redColor,greenColor,blueColor));
            }
        }
    }
}