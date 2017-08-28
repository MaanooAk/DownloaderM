// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm;

import com.maanoo.downloaderm.face.StagesManager;

import javafx.application.Application;
import javafx.stage.Stage;


/**
 *
 * @author MaanooAk
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        StagesManager.showMain();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Settings.load();

        if (args.length == 0) {
            launch(args);
        } else {
            NoWindow.launch(args);
        }
    }

}
