// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.face;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.maanoo.downloaderm.Defs;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;


/**
 *
 * @author MaanooAk
 */
public final class Utils {

    private Utils() {}

    public static String getNameFromLink(String link) {
        String name = link;
        if (name.contains("/")) {
            name = link.substring(link.lastIndexOf("/") + 1);
        }
        if (name.contains("?")) {
            name = name.replace("?", ".");
        }
        name = name.replaceAll("[^\\w\\.]+", " ").trim();
        return name;
    }

    public static boolean isLink(String link) {
        return link.toLowerCase().startsWith("http") && link.contains("://");
    }

    public static void chooseFolder(TextField tf) {

        final DirectoryChooser dc = new DirectoryChooser();
        File f = new File(tf.getText());
        if (f.exists()) dc.setInitialDirectory(f);
        f = dc.showDialog(tf.getScene().getWindow());
        if (f != null) tf.setText(f.getAbsolutePath());

    }

    public static File chooseFile(TextField tf) {

        final FileChooser fc = new FileChooser();
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(Defs.EXTENSION_RECOVERY_NAME, "*" + Defs.EXTENSION_RECOVERY));
        File f = new File(tf.getText());
        if (f.exists()) fc.setInitialDirectory(f);
        f = fc.showOpenDialog(tf.getScene().getWindow());
        return f;
    }

    public static void exitInMilliseconds(long millis) {
        new Timer(true).schedule(new TimerTask() {

            @Override
            public void run() {
                Platform.runLater(() -> Platform.exit());
            }

        }, millis);
    }

    public static void goBackInMilliseconds(long millis) {
        new Timer(true).schedule(new TimerTask() {

            @Override
            public void run() {
                Platform.runLater(() -> StagesManager.goBack());
            }

        }, millis);
    }

}
