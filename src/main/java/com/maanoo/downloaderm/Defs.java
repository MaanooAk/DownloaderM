// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm;

import java.io.File;

/**
 *
 * @author MaanooAk
 */
public class Defs {
    
    // Files
    
    public static final String PATH_SETTINGS = "settings.ini";
    
    public static final String EXTENSION_RECOVERY = ".dmrecovery";
    public static final String EXTENSION_RECOVERY_NAME = "Downloader M Recovery file";
    
    // Connections
    
    public static final int TIMEOUT_CONNECT = 10000;
    public static final int TIMEOUT_READ = 10000;
    
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 OPR/39.0.2256.71";

    // Settings
    
    public static final String SETTINGS_COMMENT = "Downloader M settings file";
    
    public static final String DEFAULT_Folder = System.getProperty("user.home") + File.separator + "Downloads";
    
    public static final int DEFAULT_MaxConnections = 10;
    public static final int DEFAULT_MinBytes = 256 * 1024;
    public static final int DEFAULT_Buffers = 1;
    public static final int DEFAULT_BufferSize = 32 * 1024;
    public static final int DEFAULT_RefreshRate = 100;
    
    // Formating
    
    public static final char NUMBER_SEPARATOR = ',';
    
    public static final int REFRESH_RATE_SPEED = 1000;
}
