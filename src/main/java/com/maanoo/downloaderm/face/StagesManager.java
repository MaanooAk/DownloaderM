// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.face;

import java.io.IOException;
import java.util.Stack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * Stages manager.
 * <p>
 * Module
 *
 * @author MaanooAk
 */
public class StagesManager {

    public static Stack<Stage> stack = new Stack<>();
    public static Stage current;

    private StagesManager() {}

    private static void pop() {
        current = stack.pop();
    }

    private static void push(Stage stage) {
        stack.push(current);
        current = stage;
    }

    private static Stage getCurrent() {
        return current;
    }

    public static void goBack() {
        getCurrent().close();
        pop();
        getCurrent().show();
    }

    public static void showMain() {

        final Stage stage = createStage(null, "FXMLMain.fxml", "Downloader M", false);

        current = stage;
        stage.show();
    }

    public static void showDownloading(Stage parent) {

        final Stage stage = createStage(parent, "FXMLDownload.fxml", "Downloading", false);

        getCurrent().hide();
        push(stage);
        stage.show();
    }

    public static void showSettings(Stage parent) {

        final Stage stage = createStage(parent, "FXMLSettings.fxml", "Settings", true);

        push(stage);
        stage.showAndWait();
        pop();
    }

    private static Stage createStage(Stage parent, String fxml, String title, boolean modal) {
        final Class<StagesManager> cl = StagesManager.class;

        final Stage stage = new Stage();

        Region root;
        FXMLController controller;
        try {
            final FXMLLoader loader = new FXMLLoader();
            root = loader.load(cl.getResourceAsStream(fxml));
            controller = loader.getController();
        } catch (final IOException ex) {
            return null;
        }

        controller.setStage(stage);

        final Scene scene = new Scene(root);
        scene.getStylesheets().add(cl.getResource("style.css").toExternalForm());

        if (parent != null) stage.initOwner(parent);
        if (modal) stage.initModality(Modality.WINDOW_MODAL);

        stage.setTitle(title);
        stage.getIcons().add(new Image(cl.getResourceAsStream("icon.png")));
        stage.setScene(scene);

        return stage;
    }
}
