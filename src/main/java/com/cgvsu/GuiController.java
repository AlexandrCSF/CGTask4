package com.cgvsu;

import com.cgvsu.model.ModelUtils;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.RenderStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;

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


    private static final Color fillColor = Color.AQUA;

    @FXML
    public CheckMenuItem textureButton;
    @FXML
    public CheckMenuItem colorFill;
    @FXML
    public MenuItem allCameras;
    @FXML
    public ListView<String> ListOfCameras;
    @FXML
    public MenuItem addCamera;
    @FXML
    public TextField PositionX;
    @FXML
    public TextField PositionY;
    @FXML
    public TextField PositionZ;
    @FXML
    public TextField TargetX;
    @FXML
    public TextField TargetY;
    @FXML
    public TextField TargetZ;
    @FXML
    public TextField FOV;
    @FXML
    public TextField cameraIndex;

    private final ObservableList<Camera> cameras = FXCollections.observableArrayList();

    private BufferedImage texture;

    public static HashMap<RenderStyle, Boolean> renderProperties = new HashMap<>();

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    Camera currCamera;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        cameras.add(new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100));
        currCamera = cameras.get(0);
        ListOfCameras.getItems().add(0 + " " + Utils.vector3ftoString(currCamera.getPosition()));


        renderProperties.put(RenderStyle.Polygonal_Grid, true);
        renderProperties.put(RenderStyle.Color_Fill, false);
        renderProperties.put(RenderStyle.Texture, false);
        renderProperties.put(RenderStyle.Light, false);

        KeyFrame frame = new KeyFrame(Duration.millis(500), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            currCamera.setAspectRatio((float) (width / height));

            if (mesh != null && cameras.size() > 0) {
                try {
                    RenderEngine.render(canvas.getGraphicsContext2D(), currCamera, mesh, (int) width, (int) height, fillColor, renderProperties, texture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();


        PositionX.setText(String.valueOf(currCamera.getPosition().x));
        PositionY.setText(String.valueOf(currCamera.getPosition().y));
        PositionZ.setText(String.valueOf(currCamera.getPosition().z));
        TargetX.setText(String.valueOf(currCamera.getTarget().x));
        TargetY.setText(String.valueOf(currCamera.getTarget().y));
        TargetZ.setText(String.valueOf(currCamera.getTarget().z));
        this.FOV.setText(String.valueOf(currCamera.getFov()));
    }


    @FXML
    private void addCamera() {
        try {
            float positionX = Float.parseFloat(PositionX.getText());
            float positionY = Float.parseFloat(PositionY.getText());
            float positionZ = Float.parseFloat(PositionZ.getText());
            float targetX = Float.parseFloat(TargetX.getText());
            float targetY = Float.parseFloat(TargetY.getText());
            float targetZ = Float.parseFloat(TargetZ.getText());
            float FOV = Float.parseFloat(this.FOV.getText());
            Camera camera = new Camera(new Vector3f(positionX, positionY, positionZ),
                    new Vector3f(targetX, targetY, targetZ), FOV, 1, 0.01F, 100);
            currCamera = camera;
            cameras.add(camera);
            ListOfCameras.getItems().add(ListOfCameras.getItems().size() + " " + Utils.vector3ftoString(camera.getPosition()));
        } catch (NumberFormatException ignored) {
        }
    }

    @FXML
    private void selectCamera() {
        try {
            int cameraIndex = Integer.parseInt(this.cameraIndex.getText());
            if (cameraIndex >= 0 && cameraIndex < ListOfCameras.getItems().size())
                currCamera = cameras.get(cameraIndex);
        } catch (NumberFormatException ignore) {
        }
    }

    @FXML
    private void deleteCamera() {
        try {
            int cameraIndex = Integer.parseInt(this.cameraIndex.getText());
            if (cameraIndex >= 0 && cameraIndex < ListOfCameras.getItems().size()) {
                cameras.remove(cameraIndex);
                ListOfCameras.getItems().remove(cameraIndex);
                Utils.recalculateIndexes(cameras, ListOfCameras.getItems());
            }
        } catch (NumberFormatException ignore) {
        }
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

        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
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
        textureButton.setSelected(false);
    }

    @FXML
    private void switchTexture() throws IOException {
        if (renderProperties.get(RenderStyle.Texture)) {
            renderProperties.put(RenderStyle.Texture, !renderProperties.get(RenderStyle.Texture));
            textureButton.setSelected(false);
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Portable Network Graphics (*.png)", "*.png", "*.jpg"));
            fileChooser.setTitle("Load Texture");

            File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
            if (file == null) {
                return;
            }

            texture = ImageIO.read(file);

            renderProperties.put(RenderStyle.Texture, !renderProperties.get(RenderStyle.Texture));
            renderProperties.put(RenderStyle.Color_Fill, false);
            textureButton.setSelected(true);
            colorFill.setSelected(false);
        }
    }

    @FXML
    private void switchLight() {
        renderProperties.put(RenderStyle.Light, !renderProperties.get(RenderStyle.Light));
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        currCamera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        currCamera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        currCamera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        currCamera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        currCamera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        currCamera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }
}