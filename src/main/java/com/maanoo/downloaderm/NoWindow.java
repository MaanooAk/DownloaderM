// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm;

import java.io.File;

import com.maanoo.downloaderm.core.DownloadEngine;
import com.maanoo.downloaderm.core.DownloadStatus;
import com.maanoo.downloaderm.core.DownloadThreadGroup;

import javafx.application.Platform;


/**
 *
 * @author MaanooAk
 */
public class NoWindow {

    public static void launch(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java -jar dm.jar url file");
            System.out.println("Usage: java -jar dm.jar -r recovery_file");
            Platform.exit();
            return;
        }

        final DownloadThreadGroup dtg;

        if (args[0].equals("-r")) {
            dtg = DownloadEngine.recovery(new File(args[1]), Settings.createDownloadConfig());
        } else {
            dtg = DownloadEngine.download(args[0], args[1], Settings.createDownloadConfig());
        }

        if (dtg == null) {
            System.out.println("Could parse input!");
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
                case Closing:
                    break;
                case Pausing:
                    break;
                case Preparing:
                    break;
                case Resuming:
                    break;
                }
            }

            @Override
            public void onPauseChange(int id, boolean paused) {
                System.out.println((id + 1) + " connection " + (paused ? "paused" : "resumed") + ".");
            }

            @Override
            public void onSubDone(int id) {
                System.out.println((id + 1) + " connection done.");
            }

        });

        dtg.start();

    }

}
