// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 *
 * @author MaanooAk
 */
public class DownloadThread extends Thread {

    protected final int id;

    protected final URL url;
    protected final File file;
    protected final long start;
    protected final long end;
    protected final long size;

    protected final DownloadStatus status;

    protected final int buffersize;

    protected long now;
    protected boolean done;
    protected boolean working;
    protected boolean closing;

    public DownloadThread(int id, URL url, File file, long start, long end, DownloadStatus status, int buffersize) {
        super("Download " + id);
        this.id = id;
        this.url = url;
        this.file = file;
        this.start = start;
        this.end = end;
        this.status = status;
        this.buffersize = buffersize;

        size = end - start;
        status.progsMax[id] = size;

        now = start;
        done = false;
        working = true;
    }

    public void setNow(long now) {
        final long dnow = now - this.now;
        this.now = now;

        status.addProgMain(dnow);
        status.progs[id] += dnow;
    }

    @Override
    public final void run() {

        if (now > end) {
            done = true;
            status.setDone(id);
            return;
        }

        transferData(createConnection());
    }

    protected HttpURLConnection createConnection() {
        HttpURLConnection connection = null;

        while (connection == null) {
            try {
                connection = Utils.openConnection(url);
                if (end < Long.MAX_VALUE) connection.setRequestProperty("Range", "bytes=" + now + "-" + end);
                connection.connect();

                final int response = connection.getResponseCode();
                if (response < 200 || response >= 300) {
                    throw new Exception("ResponseCode: " + response);
                }

            } catch (final Exception ex) {
                // retry after 500 ms

                if (connection != null) {
                    connection.disconnect();
                    connection = null;
                }

                try {
                    Thread.sleep(500);
                } catch (final InterruptedException ex1) {}
            }
        }

        return connection;
    }

    @SuppressWarnings("ConvertToTryWithResources")
    protected void transferData(HttpURLConnection connection) {

        try {
            final BufferedInputStream in = new BufferedInputStream(connection.getInputStream(), buffersize);

            if (end == Long.MAX_VALUE) {
                long skip = now;
                while (skip > 0) {
                    skip -= in.skip(skip);
                }
            }

            final RandomAccessFile out = new RandomAccessFile(file, "rw");
            out.seek(now);

            int readed;
            final byte buffer[] = new byte[buffersize];

            while ((readed = in.read(buffer)) != -1) {
                out.write(buffer, 0, readed);

                status.addProgMain(readed);
                status.progs[id] += readed;
                now += readed;

                if (!working) {
                    status.setPaused(id, true);
                    while (!working) {
                        synchronized (this) {
                            try {
                                wait();
                            } catch (final InterruptedException ex) {}
                        }
                    }
                    status.setPaused(id, false);
                }

                if (closing) break;
            }

            in.close();
            connection.disconnect();
            out.close();

            done = true;
            status.setDone(id);

        } catch (final IOException ex) {
            status.error("Data transfering failed (" + Utils.getExceptionName(ex) + ")");
        }

    }

    public void stopWorking() {
        working = false;
    }

    public void resumeWorking() {
        working = true;
        synchronized (this) {
            notifyAll();
        }
    }

    public void closeAll() {
        closing = true;
    }

    public final boolean isDone() {
        return done;
    }

}
