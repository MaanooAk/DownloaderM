// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.face;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 *
 * @author MaanooAk
 */
public abstract class FXMLController implements Initializable{

    protected Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
        onStageChange(stage);
    }
    
    public abstract void onStageChange(Stage stage);
}
