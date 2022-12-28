package com.cgvsu;

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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    private static Color fillColor = Color.AQUA;

    private Color[][] texture;

    public static HashMap<RenderStyle, Boolean> renderProperties = new HashMap<>();

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
        renderProperties.put(RenderStyle.Texture,false);
        renderProperties.put(RenderStyle.Light,false);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height, fillColor, renderProperties,texture);
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
            ModelUtils.triangulatePolygons(mesh);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    private void switchPolygonalGrid() {
        renderProperties.put(RenderStyle.Polygonal_Grid, !renderProperties.get(RenderStyle.Polygonal_Grid));
    }

    @FXML
    private void switchColorFill() {

        renderProperties.put(RenderStyle.Color_Fill, !renderProperties.get(RenderStyle.Color_Fill));
        renderProperties.put(RenderStyle.Texture, false);
    }

    @FXML
    private void switchTexture() throws IOException {
        File imgFile = new File("C:\\Users\\debek\\Desktop\\IdeaProjects\\Simple3DViewerInitial\\texture.jpg");
        BufferedImage image = ImageIO.read(imgFile);

        texture = Utils.convertImageToIntArray(image);

        renderProperties.put(RenderStyle.Texture, !renderProperties.get(RenderStyle.Texture));
        renderProperties.put(RenderStyle.Color_Fill, false);
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
}