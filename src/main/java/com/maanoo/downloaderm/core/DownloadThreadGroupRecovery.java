// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.core;

import java.io.File;
import java.net.URL;


/**
 *
 * @author MaanooAk
 */
public class DownloadThreadGroupRecovery extends DownloadThreadGroup {

    protected final long pointers[][];

    public DownloadThreadGroupRecovery(URL url, File file, int connections, int minbytes, int buffers, int buffersize,
            long[][] pointers) {
        super(url, file, connections, minbytes, buffers, buffersize);
        this.pointers = pointers;
    }

    @Override
    protected void createThreads(long size) {

        status.init(connections, size);

        for (int i = 0; i < connections; i++) {

            final long start = pointers[i][0];
            final long end = pointers[i][1];
            final long now = pointers[i][2];

            DownloadThread dt;
            if (buffers <= 1) {
                dt = new DownloadThread(i, url, file, start, end, status, buffersize);
            } else {
                dt = new DownloadThreadDoubleBuffered(i, url, file, start, end, status, buffersize, buffers);
            }
            dt.setNow(now);

            threads.add(dt);
        }

    }

    @Override
    protected void createThreads() {

        DownloadThread dt;
        if (buffers <= 1) {
            dt = new DownloadThread(0, url, file, 0, Long.MAX_VALUE, status, buffersize);
        } else {
            dt = new DownloadThreadDoubleBuffered(0, url, file, 0, Long.MAX_VALUE, status, buffersize, buffers);
        }
        dt.setNow(pointers[0][2]);

        threads.add(dt);

    }

}
