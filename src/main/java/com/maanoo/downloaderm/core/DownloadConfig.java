// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.core;

/**
 *
 * @author MaanooAk
 */
public class DownloadConfig {

    public static final int DEFAULT_MaxConnections = 10;
    public static final int DEFAULT_MinBytes = 256 * 1024;
    public static final int DEFAULT_Buffers = 1;
    public static final int DEFAULT_BufferSize = 32 * 1024;

    public static final DownloadConfig DEFAULT = new DownloadConfig(DEFAULT_MaxConnections, DEFAULT_MinBytes,
            DEFAULT_Buffers, DEFAULT_BufferSize);

    public final int maxConnection;
    public final int minBytes;
    public final int buffers;
    public final int bufferSize;

    public DownloadConfig(int maxConnection, int minBytes, int buffers, int bufferSize) {
        this.maxConnection = maxConnection;
        this.minBytes = minBytes;
        this.buffers = buffers;
        this.bufferSize = bufferSize;
    }

}
