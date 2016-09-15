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
            nowindow(args);
        }
    }
    
    private static void nowindow(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java -jar dm.jar url file");
            System.out.println("Usage: java -jar dm.jar -r recovery_file");
            Platform.exit();
            return;
        }

        final DownloadThreadGroup dtg;
        
        if(args[0].equals("-r")) {
            dtg = DownloadEngine.recovery(new File(args[1]));
        }else{
            dtg = DownloadEngine.download(args[0], args[1]);
        }
        
        if (dtg == null) {
            System.out.println("Could parse input");
            Platform.exit();
            return;
        }

        dtg.getStatus().addListener(new DownloadStatus.Listener() {

            @Override
            public void onStateChange(DownloadStatus.State state) {

                System.out.println("- " + state.toString());
                
                switch (state) {
                case Downloading:
                    System.out.println(dtg.getConnections() + " connections opened");
                    break;
                case Error:
                    DownloadEngine.storeRecovery(dtg);
                    System.out.println(dtg.getStatus().getError());
                    Platform.exit();
                case Pause:
                    DownloadEngine.storeRecovery(dtg);
                    break;
                case Closed:
                    DownloadEngine.storeRecovery(dtg);
                    Platform.exit();
                    break;
                case Done:
                    DownloadEngine.deleteRecovery(dtg);
                    Platform.exit();
                    break;
                }
            }

            @Override
            public void onPauseChange(int id, boolean paused) {
                System.out.println((id+1) + " connection " + (paused ? "paused" : "resumed"));
            }

            @Override
            public void onSubDone(int id) {
                System.out.println((id+1) + " connection done");
            }
        });

        dtg.start();

    }
}
