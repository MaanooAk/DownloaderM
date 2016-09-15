package com.maanoo.downloaderm.core;

import com.maanoo.downloaderm.Settings;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 *
 * @author Akritas
 */
public final class DownloadEngine {
    
    public static DownloadThreadGroup dtg;


    private DownloadEngine() {
        // deny object creation
    }
    
    public static boolean validate(String link, String file) {
        
        try {
            URL url = new URL(link);
        } catch (MalformedURLException ex) {
            return false;
        }
        
        return true;
    }
    
    public static DownloadThreadGroup download(String link, String file) {
        
        URL url;
        
        try {
            url = new URL(link);
        } catch (MalformedURLException ex) {
            return null;
        }
        
        File f = new File(file);
                
        dtg = new DownloadThreadGroup(url, f, 
                Settings.getInt(Settings.Name.MaxConnections), 
                Settings.getInt(Settings.Name.MinBytes),
                Settings.getInt(Settings.Name.Buffers),
                Settings.getInt(Settings.Name.BufferSize)
        );
        
        return dtg;
    }
    
    public static void storeRecovery(DownloadThreadGroup dtg) {
        if(dtg.getStatus().getProgMain() <= 0) return;
        
        File f = Utils.getRecoveryFile(dtg.file);

        try (PrintWriter out = new PrintWriter(f, StandardCharsets.UTF_8.name())) {

            out.println(dtg.getUrl().toExternalForm());
            out.println(dtg.getFile().toPath());
            out.println(dtg.size);
            out.println(dtg.connections);

            for (int i = 0; i < dtg.connections; i++) {
                DownloadThread t = dtg.threads.get(i);

                out.println(t.start);
                out.println(t.end);
                out.println(t.now);
            }

        } catch (IOException ex) {
            // nothing // SHTO inform user
        }
    }
    
    public static void deleteRecovery(DownloadThreadGroup dtg) {
        
        File f = Utils.getRecoveryFile(dtg.file);
        
        if(f.exists()) f.delete();
        
    }
    
    public static DownloadThreadGroup recovery(File frecovery) {
        
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
            
        } catch (IOException ex) {
            // nothing // SHTO inform user
            return null;
        }

        URL url;
        
        try {
            url = new URL(link);
        } catch (MalformedURLException ex) {
            return null;
        }
        
        File f = new File(file);        
        
        dtg = new DownloadThreadGroupRecovery(url, f, 
                connections, 
                Settings.getInt(Settings.Name.MinBytes),
                Settings.getInt(Settings.Name.Buffers),
                Settings.getInt(Settings.Name.BufferSize),
                pointers
        );
        
        return dtg;
    }
    
    public static void deteleDownloaded(DownloadThreadGroup dtg) {
        File f;
        
        f = dtg.getFile();
        if(f.exists()) f.delete();
        
        f = Utils.getRecoveryFile(f);
        if(f.exists()) f.delete();        
    }
}
