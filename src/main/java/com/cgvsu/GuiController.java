package com.cgvsu;

import com.cgvsu.math.Matrix;
import com.cgvsu.model.ModelUtils;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.RenderStyle;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import javax.vecmath.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    Color fillColor = Color.AQUA;

    HashMap<RenderStyle, Boolean> renderProperties = new HashMap<>();

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        renderProperties.put(RenderStyle.Polygonal_Grid,true);
        renderProperties.put(RenderStyle.Color_Fill,false);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                ModelUtils.triangulatePolygons(mesh);
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height, fillColor, renderProperties);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void recalculateNormals() {
        ModelUtils.recalculateNormals(mesh);
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    private void switchPolygonalGrid() {
        renderProperties.put(RenderStyle.Polygonal_Grid, !renderProperties.get(RenderStyle.Polygonal_Grid));
    }

    @FXML
    private void setRenderStyleToColorFill() {
        renderProperties.put(RenderStyle.Color_Fill, !renderProperties.get(RenderStyle.Color_Fill));
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }

    //Масштабирование
    @FXML
    public void handleScaleXPlus(ActionEvent actionEvent) {
        float[][] scaleMatrix = Matrix.getScale(1.01f, 1, 1);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(scaleMatrix));
    }

    @FXML
    public void handleScaleYPlus(ActionEvent actionEvent) {
        float[][] scaleMatrix = Matrix.getScale(1, 1.01f, 1);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(scaleMatrix));
    }

    @FXML
    public void handleScaleZPlus(ActionEvent actionEvent) {
        float[][] scaleMatrix = Matrix.getScale(1, 1, 1.01f);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(scaleMatrix));
    }

    @FXML
    public void handleScaleXMinus(ActionEvent actionEvent) {
        float[][] scaleMatrix = Matrix.getScale(0.99f, 1, 1);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(scaleMatrix));
    }

    @FXML
    public void handleScaleYMinus(ActionEvent actionEvent) {
        float[][] scaleMatrix = Matrix.getScale(1, 0.99f, 1);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(scaleMatrix));
    }

    @FXML
    public void handleScaleZMinus(ActionEvent actionEvent) {
        float[][] scaleMatrix = Matrix.getScale(1, 1, 0.99f);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(scaleMatrix));
    }


    //Перемещение
    @FXML
    public void handleMovementYPlus(ActionEvent actionEvent) {
        float[][] transform = Matrix.getTranslation(0, 0.5f, 0);
        for (int i = 0; i < mesh.vertices.size(); i++) {
            com.cgvsu.math.Vector3f v = mesh.vertices.get(i);
            mesh.vertices.set(i, Matrix.multiplyVector(transform, v));
        }
    }

    @FXML
    public void handleMovementYMinus(ActionEvent actionEvent) {
        float[][] transform = Matrix.getTranslation(0, -0.5f, 0);
        for (int i = 0; i < mesh.vertices.size(); i++) {
            com.cgvsu.math.Vector3f v = mesh.vertices.get(i);
            mesh.vertices.set(i, Matrix.multiplyVector(transform, v));
        }
    }

    @FXML
    public void handleMovementXMinus(ActionEvent actionEvent) {
        float[][] transform = Matrix.getTranslation(0.5f, 0, 0);
        for (int i = 0; i < mesh.vertices.size(); i++) {
            com.cgvsu.math.Vector3f v = mesh.vertices.get(i);
            mesh.vertices.set(i, Matrix.multiplyVector(transform, v));
        }
    }

    @FXML
    public void handleMovementXPlus(ActionEvent actionEvent) {
        float[][] transform = Matrix.getTranslation(-0.5f, 0, 0);
        for (int i = 0; i < mesh.vertices.size(); i++) {
            com.cgvsu.math.Vector3f v = mesh.vertices.get(i);
            mesh.vertices.set(i, Matrix.multiplyVector(transform, v));
        }
    }

    @FXML
    public void handleMovementZPlus(ActionEvent actionEvent) {
        float[][] transform = Matrix.getTranslation(0, 0, 0.5f);
        for (int i = 0; i < mesh.vertices.size(); i++) {
            com.cgvsu.math.Vector3f v = mesh.vertices.get(i);
            mesh.vertices.set(i, Matrix.multiplyVector(transform, v));
        }
    }

    @FXML
    public void handleMovementZMinus(ActionEvent actionEvent) {
        float[][] transform = Matrix.getTranslation(0, 0, -0.5f);
        for (int i = 0; i < mesh.vertices.size(); i++) {
            com.cgvsu.math.Vector3f v = mesh.vertices.get(i);
            mesh.vertices.set(i, Matrix.multiplyVector(transform, v));
        }
    }

    //Вращение
    @FXML
    public void handleRotationXPlus(ActionEvent actionEvent) {
        float angle = 10.0F;
        float[][] rotationMatrix = Matrix.getRotationX(angle);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(rotationMatrix));
    }

    @FXML
    public void handleRotationYPlus(ActionEvent actionEvent) {
        float angle = 10.0F;
        float[][] rotationMatrix = Matrix.getRotationY(angle);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(rotationMatrix));
    }

    @FXML
    public void handleRotationZPlus(ActionEvent actionEvent) {
        float angle = 10.0F;
        float[][] rotationMatrix = Matrix.getRotationZ(angle);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(rotationMatrix));
    }

    @FXML
    public void handleRotationXMinus(ActionEvent actionEvent) {
        float angle = -10.0F;
        float[][] rotationMatrix = Matrix.getRotationX(angle);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(rotationMatrix));
    }

    @FXML
    public void handleRotationYMinus(ActionEvent actionEvent) {
        float angle = -10.0F;
        float[][] rotationMatrix = Matrix.getRotationY(angle);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(rotationMatrix));
    }

    @FXML
    public void handleRotationZMinus(ActionEvent actionEvent) {
        float angle = -10.0F;
        float[][] rotationMatrix = Matrix.getRotationZ(angle);
        mesh.vertices.forEach(vertex -> vertex.applyMatrix(rotationMatrix));
    }


}