/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maanoo.downloaderm;

import com.maanoo.downloaderm.core.DownloadEngine;
import com.maanoo.downloaderm.core.DownloadStatus;
import com.maanoo.downloaderm.core.DownloadThreadGroup;
import com.maanoo.downloaderm.face.StagesManager;
import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 *
 * @author Akritas
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
        
        if(args.length == 0) {
            launch(args);
        }else{
            NoWindow.launch(args);
        }
    }
    
}
