// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.face;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.maanoo.downloaderm.Settings;
import com.maanoo.downloaderm.core.DownloadEngine;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.stage.Stage;


/**
 *
 * @author MaanooAk
 */
public class FXMLMainController extends FXMLController {

    @FXML
    private TextField tfLink;
    @FXML
    private TextField tfFolder;
    @FXML
    private Button butFolder;
    @FXML
    private TextField tfName;
    @FXML
    private Button butDownload;
    @FXML
    private Button butSettings;
    @FXML
    private Button butRecovry;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // link change listener

        butDownload.setDisable(true);

        tfLink.textProperty()
                .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {

                    tfName.setText(Utils.getNameFromLink(newValue));

                    butDownload.setDisable(newValue.trim().isEmpty());
                });

        // download folder

        tfFolder.setText(Settings.getString(Settings.Name.Folder));

        // get clipboard

        final String link = Clipboard.getSystemClipboard().getString();

        if (link != null && Utils.isLink(link)) {

            tfLink.setText(link);
            Platform.runLater(() -> butDownload.requestFocus());
        } else {
            // tfLink.setText("http://download.thinkbroadband.com/5MB.zip"); // TODO remove
            Platform.runLater(() -> tfLink.requestFocus());
        }

    }

    @Override
    public void onStageChange(Stage stage) {
        // nothing
    }

    @FXML
    private void actFolder(ActionEvent event) {

        Utils.chooseFolder(tfFolder);
    }

    @FXML
    private void actDownload(ActionEvent event) throws IOException {

        final String url = tfLink.getText();
        final String file = tfFolder.getText() + File.separator + tfName.getText();

        if (!DownloadEngine.validate(url, url)) {
            // TODO Show error
            return;
        }

        if (DownloadEngine.download(url, file) == null) return;

        StagesManager.showDownloading((Stage) butDownload.getScene().getWindow());

    }

    @FXML
    private void actSettings(ActionEvent event) throws IOException {

        StagesManager.showSettings((Stage) butDownload.getScene().getWindow());

    }

    @FXML
    private void actRecovery(ActionEvent event) {

        final File f = Utils.chooseFile(tfFolder);

        if (f == null || !f.exists()) return;

        if (DownloadEngine.recovery(f) == null) return;

        StagesManager.showDownloading((Stage) butDownload.getScene().getWindow());

    }

}
