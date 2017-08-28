// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author MaanooAk
 */
public class Settings {
    
    private static Properties ps;
    
    /**
     * Settings property name
     */
    public static enum Name {
        Folder(false), 
        MaxConnections, 
        MinBytes, 
        Buffers, 
        BufferSize, 
        RefreshRate;
        
        private final boolean num;

        private Name() {
            this.num = true;
        }
        private Name(boolean num) {
            this.num = num;
        }

        public boolean isNum() {
            return num;
        }
    }
    
    public static void load() {
        ps = new Properties();

        if (new File(Defs.PATH_SETTINGS).exists()) {

            try (FileInputStream stream = new FileInputStream(Defs.PATH_SETTINGS)) {
                ps.load(stream);
            } catch (IOException ex) {
                // nothing // SHTO inform user
            }
            
            setMissingDefaults();
        }else{            
            setDefaults();            
        }
    }
    
    private static void setMissingDefaults() {
        
        setMissingDefault(Name.Folder, Defs.DEFAULT_Folder);
        setMissingDefault(Name.MaxConnections, Defs.DEFAULT_MaxConnections);
        setMissingDefault(Name.MinBytes, Defs.DEFAULT_MinBytes);
        setMissingDefault(Name.Buffers, Defs.DEFAULT_Buffers);
        setMissingDefault(Name.BufferSize, Defs.DEFAULT_BufferSize);
        setMissingDefault(Name.RefreshRate, Defs.DEFAULT_RefreshRate);
        
    }
    
    private static void setMissingDefault(Name name, int value) {
        setMissingDefault(name, value + "");
    }
    private static void setMissingDefault(Name name, String value) {
        if(ps.getProperty(name.toString()) == null) {
            ps.setProperty(name.toString(), value);
        }
    }
    
    private static void setDefaults() {
        ps.clear();
        setMissingDefaults();
    }
    
    public static void store() {
       
        try (FileOutputStream stream = new FileOutputStream(Defs.PATH_SETTINGS, false)) {
            ps.store(stream, Defs.SETTINGS_COMMENT);
        } catch (IOException ex) {
            // nothing // SHTO inform user
        }

    }

    public static void set(Name name, String value) {
        if(name.isNum()) {
            try {
                ps.setProperty(name.toString(), Integer.parseInt(value) + "");
            } catch (NumberFormatException e) {
                // nothing
            }
        }else{
            ps.setProperty(name.toString(), value);
        }
    }
    
    public static String getString(Name name) {
        return ps.getProperty(name.toString());
    }
        
    public static int getInt(Name name) {
        return Integer.parseInt(ps.getProperty(name.toString()));
    }
    
}
