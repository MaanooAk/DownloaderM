package com.maanoo.downloaderm.face;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 *
 * @author Akritas
 */
public abstract class FXMLController implements Initializable{

    protected Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
        onStageChange(stage);
    }
    
    public abstract void onStageChange(Stage stage);
}
