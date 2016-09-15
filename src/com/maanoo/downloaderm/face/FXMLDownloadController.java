/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maanoo.downloaderm.face;

import com.maanoo.downloaderm.Defs;
import com.maanoo.downloaderm.Settings;
import com.maanoo.downloaderm.core.SpeedMonitor;
import com.maanoo.downloaderm.core.DownloadEngine;
import com.maanoo.downloaderm.core.DownloadStatus;
import com.maanoo.downloaderm.core.DownloadThreadGroup;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Akritas
 */
public class FXMLDownloadController extends FXMLController {
        
    @FXML
    private Label laStatus;
    @FXML
    private ProgressBar progMain;
    @FXML
    private HBox progHolder;
    @FXML
    private Label laBytes;
    @FXML
    private Label laPer;
    @FXML
    private Label laTime;
    @FXML
    private Button butPause;
    @FXML
    private Button butCancel;

    private ProgressBar progs[];
    
    private DownloadThreadGroup dtg;
    private DownloadStatus status;
    
    private Timer updateTimer;
    private SpeedMonitor sm;
    
    private long dateS, dateE;
    private boolean canceled;
    private boolean exiting;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        dtg = DownloadEngine.dtg;
        status = dtg.getStatus();
        
        progHolder.getChildren().clear();
                
        //butPause.setText("\u23F8");
        butPause.setDisable(true);
        
        // ===
        
        dtg.start();
        
        dateS = System.currentTimeMillis();        
        sm = new SpeedMonitor.Group(dateS, 0, 3);
        
        canceled = false;
        exiting = false;
        
                
    }

    @Override
    public void onStageChange(Stage stage) {
        
        stage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();

            if(exiting) {
                Platform.exit();
            }else if(status.getState() == DownloadStatus.State.Downloading) {

            } else if (status.getState() == DownloadStatus.State.Pause
                    || status.getState() == DownloadStatus.State.Pausing
                    || status.getState() == DownloadStatus.State.Closing) {

                System.exit(0);
            }

        });
        
        dtg.getStatus().addListener(new DownloadStatus.Listener() {

            @Override
            public void onStateChange(DownloadStatus.State state) {
                Platform.runLater(() -> updateState(state));
            }

            @Override
            public void onPauseChange(int id, boolean paused) {
                Platform.runLater(() -> {
                    if(paused) progs[id].getStyleClass().add("pause");
                    else progs[id].getStyleClass().remove("pause");
                });
            }

            @Override
            public void onSubDone(int id) {
                Platform.runLater(() -> {
                    progs[id].setProgress(1);
                    progs[id].getStyleClass().add("done");
                });
            }
        });
    }
    
    protected void updateState(DownloadStatus.State state) {
        
        if(exiting) return;
        
        butPause.setDisable(!(state == DownloadStatus.State.Downloading || state == DownloadStatus.State.Pause));
        butCancel.setDisable(!(state == DownloadStatus.State.Downloading || state == DownloadStatus.State.Pause || state == DownloadStatus.State.Preparing));

        switch(state) {
        case Preparing:
            
            setTitle("Preparing");
            laPer.setText("");
            laBytes.setText("");
            laTime.setText("");
            
            break;
        case Downloading:
            
            setTitle("Downloading");
            laPer.setText("");
            laBytes.setText("");
            laTime.setText("");
            
            // set up sub progress bars
            
            progs = new ProgressBar[status.connections];
            progHolder.getChildren().clear();

            if(progs.length > 1) {
            
                for(int i=0; i<progs.length; i++) {

                    ProgressBar pb = new ProgressBar(0);  
                    pb.prefWidthProperty().bind(progHolder.widthProperty());
                    pb.getStyleClass().add("sub");

                    progs[i] = pb;            
                    progHolder.getChildren().add(pb);
                }

                progs[0].getStyleClass().add("first");
                progs[progs.length-1].getStyleClass().add("last");
            
            }else{
                
                ProgressBar pb = new ProgressBar(0);  
                pb.prefWidthProperty().bind(progHolder.widthProperty());

                progs[0] = pb;            
                progHolder.getChildren().add(pb);
                
            }
            
            break;
        case Done:            
            
            dateE = System.currentTimeMillis();

            long time = System.currentTimeMillis() - dateS;
            long seconds = time / (1000);

            if(status.progMainMax < 1) status.progMainMax = status.getProgMain();

            setTitle("Done");
            laPer.setText("100 %");
            laTime.setText(TextUtils.time(time) + " - " + TextUtils.speed((status.progMainMax)/time));
            laBytes.setText(TextUtils.dash(status.getProgMain(), status.progMainMax));
            
            progMain.setProgress(1);
            progMain.getStyleClass().add("done");
            
            DownloadEngine.deleteRecovery(dtg);
            
            exiting = true;
            Utils.exitInMilliseconds(3000);

            break;
        case Error:
            
            if(!dtg.isClosing()) {
            
                setTitle("Error");
                laTime.setText(status.getError());

                if(status.getProgMain() > 0) {

                    DownloadEngine.storeRecovery(dtg);

                    dtg.closeAll();

                }else{

                    exiting = true;
                    Utils.goBackInMilliseconds(3000);                
                }
            
            }
            break;
        case Pausing:
            
            setTitle("Pausing");
            
            DownloadEngine.storeRecovery(dtg);
            
            break;
        case Pause:
            
            setTitle("Pause");
            
            DownloadEngine.storeRecovery(dtg);
            
            break;
        case Resuming:
            
            setTitle("Resuming");
                                    
            break;
        case Closing:
            
            setTitle("Closing");
            
            break;
        case Closed:
            
            setTitle("Closed");
            
            if(canceled) {
                DownloadEngine.deteleDownloaded(dtg);
            }
            
            exiting = true;
            Utils.exitInMilliseconds(3000);
        }
        
        if(status.getState().isUpdating()) {            
            
            // set update loop
            
            updateTimer = new Timer(true);
            updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> update());
                }
            }, Settings.getInt(Settings.Name.RefreshRate)/2, Settings.getInt(Settings.Name.RefreshRate));
            updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> updateSpeed());
                }
            }, Defs.REFRESH_RATE_SPEED/2, Defs.REFRESH_RATE_SPEED);
            
        }
        
    }
    
    private void setTitle(String text) {
        stage.setTitle(text);
        laStatus.setText(text);
    }
        
    protected void update() {
        
        if(!status.getState().isUpdating()) {
            updateTimer.cancel();
            return;
        }
        
        if(status.progMainMax > 0) {
                      
            laPer.setText(TextUtils.percent(status.getProgMain(), status.progMainMax));
            laBytes.setText(TextUtils.dash(status.getProgMain(), status.progMainMax));
            
            if(status.getState() == DownloadStatus.State.Downloading) 
                stage.setTitle(laPer.getText());
            
            progMain.setProgress(status.getProgressMain());
            for (int i = 0; i < progs.length; i++) {
                progs[i].setProgress(status.getProgress(i));
            }

            long speed = sm.getSpeed();
            if(speed > 0) {
                long time = ((status.progMainMax - status.getProgMain())) / speed;
                laTime.setText(TextUtils.timeRounded(time) + " - " + TextUtils.speed(speed));
            }else{
                laTime.setText("");
            }

        }else{

            laBytes.setText(TextUtils.integer(status.getProgMain()) + " / ?");

        }
        
    }
    
    protected void updateSpeed() {
        sm.give(System.currentTimeMillis(), status.getProgMain());
    }
    
    @FXML
    private void actPause(ActionEvent event) {
        
        if(status.getState() == DownloadStatus.State.Downloading) {
            
            butPause.setDisable(true);
            dtg.stopWorking();
            
        }else if(status.getState() == DownloadStatus.State.Pause) {
            
            butPause.setDisable(true);
            dtg.resumeWorking();
        }
        
    }

    @FXML
    private void actCancel(ActionEvent event) {
        
        if (status.getState() == DownloadStatus.State.Preparing
                || status.getState() == DownloadStatus.State.Downloading
                || status.getState() == DownloadStatus.State.Pause) {
            
            canceled = true;
            
            butCancel.setDisable(true);            
            dtg.closeAll();            
        }
                
    }
    
    
}
