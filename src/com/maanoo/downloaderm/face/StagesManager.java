package com.maanoo.downloaderm.face;

import java.io.IOException;
import java.util.Stack;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Akritas
 */
public class StagesManager {
    
    public static Stack<Stage> stack = new Stack<>();
    public static Stage current;

    private static void pop() {
        current = stack.pop();
    }
    private static void push(Stage stage) {
        stack.push(current);
        current = stage;
    }
    
    public static void goBack() {
        current.close();
        pop();
        current.show();
    }
        
    public static void showMain() {
        
        Stage stage = createStage(null, "FXMLMain.fxml", "Downloader M", false);
        
        current = stage;
        stage.show();
    }
    
    public static void showDownloading(Stage parent) {        
        
        Stage stage = createStage(parent, "FXMLDownload.fxml", "Downloading", false);
        
        current.hide();
        push(stage);
        stage.show();
    }

    public static void showSettings(Stage parent) {
        
        Stage stage = createStage(parent, "FXMLSettings.fxml", "Settings", true);

        push(stage);
        stage.showAndWait();        
        pop();
    }
    
    public static Stage createStage(Stage parent, String fxml, String title, boolean modal) {
        Class cl = StagesManager.class;
        
        Stage stage = new Stage();
        
        Region root;
        FXMLController controller;
        try {
            FXMLLoader loader = new FXMLLoader();
            root = loader.load(cl.getResourceAsStream(fxml));
            controller = loader.getController();
        } catch (IOException ex) {
            return null;
        }
        
        controller.setStage(stage);
                        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(cl.getResource("style.css").toExternalForm());
        
        stage.setOnShown((WindowEvent event) -> {

            double offH = stage.getHeight() - scene.getHeight();
            double offW = stage.getWidth()- scene.getWidth();            
            
            stage.setMinWidth(root.getMinWidth()+offW);
            stage.setMinHeight(root.getMinHeight()+offH);
            stage.setMaxWidth(root.getMaxWidth()+offW);
            stage.setMaxHeight(root.getMaxHeight()+offH);
        });
        
        
        if(parent != null) stage.initOwner(parent);
        if(modal) stage.initModality(Modality.WINDOW_MODAL);
                
        stage.setTitle(title);
        stage.getIcons().add(new Image(cl.getResourceAsStream("icon.png")));
        stage.setScene(scene);
        
        return stage;
    }
}
