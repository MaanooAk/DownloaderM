// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


/**
 *
 * @author MaanooAk
 */
public final class DownloadEngine {

    @Deprecated
    private static DownloadThreadGroup dtg;

    private DownloadEngine() {}

    @Deprecated
    public static DownloadThreadGroup getActiveDownloadThreadGroup() {
        return dtg;
    }

    public static boolean validate(String link, String file) {

        try {
            final URL url = new URL(link);
        } catch (final MalformedURLException ex) {
            return false;
        }

        return true;
    }

    public static DownloadThreadGroup download(String link, String file) {
        return download(link, file, DownloadConfig.DEFAULT);
    }

    public static DownloadThreadGroup download(String link, String file, DownloadConfig config) {

        URL url;

        try {
            url = new URL(link);
        } catch (final MalformedURLException ex) {
            return null;
        }

        final File f = new File(file);

        final DownloadThreadGroup dtg = new DownloadThreadGroup(url, f, config.maxConnection, config.minBytes,
                config.buffers, config.bufferSize);

        return DownloadEngine.dtg = dtg;
    }

    public static void storeRecovery(DownloadThreadGroup dtg) {
        if (dtg.getStatus().getProgMain() <= 0) return;

        final File f = Utils.getRecoveryFile(dtg.file);

        try (PrintWriter out = new PrintWriter(f, StandardCharsets.UTF_8.name())) {

            out.println(dtg.getUrl().toExternalForm());
            out.println(dtg.getFile().toPath());
            out.println(dtg.size);
            out.println(dtg.connections);

            for (int i = 0; i < dtg.connections; i++) {
                final DownloadThread t = dtg.threads.get(i);

                out.println(t.start);
                out.println(t.end);
                out.println(t.now);
            }

        } catch (final IOException ex) {
            // nothing // SHTO inform user
        }
    }

    public static void deleteRecovery(DownloadThreadGroup dtg) {

        final File f = Utils.getRecoveryFile(dtg.file);

        if (f.exists()) f.delete();

    }

    public static DownloadThreadGroup recovery(File frecovery) {
        return recovery(frecovery, DownloadConfig.DEFAULT);
    }

    public static DownloadThreadGroup recovery(File frecovery, DownloadConfig config) {

        String link, file;
        long size;
        int connections;
        long pointers[][];

        try (Scanner scanner = new Scanner(frecovery, StandardCharsets.UTF_8.name())) {

            link = scanner.nextLine();
            file = scanner.nextLine();
            size = scanner.nextLong();
            connections = scanner.nextInt();

            pointers = new long[connections][3];

            for (int i = 0; i < connections; i++) {

                pointers[i][0] = scanner.nextLong(); // start
                pointers[i][1] = scanner.nextLong(); // end
                pointers[i][2] = scanner.nextLong(); // now

            }

        } catch (final IOException ex) {
            // nothing // SHTO inform user
            return null;
        }

        URL url;

        try {
            url = new URL(link);
        } catch (final MalformedURLException ex) {
            return null;
        }

        final File f = new File(file);

        final DownloadThreadGroup dtg = new DownloadThreadGroupRecovery(url, f, connections, config.minBytes,
                config.buffers, config.bufferSize, pointers);

        return DownloadEngine.dtg = dtg;
    }

    public static void deteleDownloaded(DownloadThreadGroup dtg) {
        File f;

        f = dtg.getFile();
        if (f.exists()) f.delete();

        f = Utils.getRecoveryFile(f);
        if (f.exists()) f.delete();
    }
}
