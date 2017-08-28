// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.core;

import com.maanoo.downloaderm.Defs;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author MaanooAk
 */
public final class Utils {
    
    private Utils() {
        // deny object creation
    }
    
    public static HttpURLConnection openConnection(URL url) throws IOException {
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                
        connection.setConnectTimeout(Defs.TIMEOUT_CONNECT);
        connection.setReadTimeout(Defs.TIMEOUT_READ);
        
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 OPR/39.0.2256.71");
        
        return connection;     
    }
    
    public static final void preallocateFile(File file, long size) {        
        try (RandomAccessFile out = new RandomAccessFile(file, "rw")) {
            
            out.setLength(size);
            
        } catch (IOException ex) {
            // nothing // SHTO inform user
        }
    }
    
    public static String getExceptionName(IOException ex) {
        return ex.getClass().getSimpleName();
    }
    
    public static File getRecoveryFile(File f) {
        return new File(f.getPath() + Defs.EXTENSION_RECOVERY);
    }
}
