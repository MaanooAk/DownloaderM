/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maanoo.downloaderm.face;

import com.maanoo.downloaderm.Settings;
import com.maanoo.downloaderm.Settings.Name;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Akritas
 */
public class FXMLSettingsController extends FXMLController {
        
    @FXML
    private TextField tfFolder;
    @FXML
    private TextField tfMaxConnections;
    @FXML
    private TextField tfMinBytes;
    @FXML
    private Button butFolder;
    @FXML
    private Button butStore;
    @FXML
    private Button butCancel;
    @FXML
    private TextField tfBuffers;
    @FXML
    private TextField tfBufferSize;
    @FXML
    private TextField tfRefreshRate;
    

    private static class Bind {
        public final TextField tf;
        public final Name name;

        public Bind(TextField tf, Name name) {
            this.tf = tf;
            this.name = name;
        }
    }
    
    private ArrayList<Bind> binds;
    
    private void bind(TextField tf, Name name) {
        tf.setText(Settings.getString(name));        
        binds.add(new Bind(tf, name));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        binds = new ArrayList<>();
        bind(tfFolder, Name.Folder);
        bind(tfMaxConnections, Name.MaxConnections);
        bind(tfMinBytes, Name.MinBytes);
        bind(tfBuffers, Name.Buffers);
        bind(tfBufferSize, Name.BufferSize);
        bind(tfRefreshRate, Name.RefreshRate);
        
        Platform.runLater(() -> butCancel.requestFocus());  
    }

    @Override
    public void onStageChange(Stage stage) {
        
    }
    
    @FXML
    private void actStore(ActionEvent event) {
        
        for(Bind i : binds) {
            Settings.set(i.name, i.tf.getText());
        }
        
        Settings.store();
        
        close();
    }

    
    
    @FXML
    private void actCancel(ActionEvent event) {
        
        close();
    }

    @FXML
    private void actMaanooCom(ActionEvent event) throws IOException, URISyntaxException {
        
        Desktop.getDesktop().browse(new URL("http://maanoo.com/downloaderm").toURI());        
    }

    @FXML
    private void actFolder(ActionEvent event) {
        
        Utils.chooseFolder(tfFolder);
    }
    
    private void close() {
        ((Stage)tfFolder.getScene().getWindow()).close();
    }
    
}
