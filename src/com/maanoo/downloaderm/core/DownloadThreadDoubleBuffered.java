package com.maanoo.downloaderm.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Akritas
 */
public class DownloadThreadDoubleBuffered extends DownloadThread {

    protected final int buffers;
        
    public DownloadThreadDoubleBuffered(int id, URL url, File file, long start, long end, DownloadStatus status, int buffersize, int buffers) {
        super(id, url, file, start, end, status, buffersize);
        this.buffers = buffers;
    }

    @Override
    @SuppressWarnings("ConvertToTryWithResources")
    protected void transferData(HttpURLConnection connection) {
        
        try {
            final BufferedInputStream in = new BufferedInputStream(connection.getInputStream(), buffersize);
            
            if(end == Long.MAX_VALUE) {
                long skip = now;
                while(skip > 0) {
                    skip -= in.skip(skip);
                }
            }
            
            final RandomAccessFile out = new RandomAccessFile(file, "rw");
            out.seek(now);
            
            final byte buffer[][] = new byte[buffers][buffersize];
            final int readed[] = new int[buffers];
            final boolean free[] = new boolean[buffers];
            
            for(int i=0; i<buffers; i++) {
                free[i] = true;
            }
            
            Thread tin = new Thread(()->{
                try {
                    int i = 0;
                    
                    while(true) {
                        
                        while(!free[i] && !closing) {
                            synchronized(this) {
                                try {
                                    wait();
                                } catch (InterruptedException ex) { }
                            }
                        }
                        
                        if(closing) break;
                        
                        readed[i] = in.read(buffer[i]);
                        free[i] = false;
                        if(readed[i] == -1) break;
                            
                        
                        synchronized(this) {
                            notifyAll();
                        }                      
                        
                        if(!working) {                   
                            status.setPaused(id, true);
                            while(!working && !closing) {
                                synchronized(this) {
                                    try {
                                        wait();
                                    } catch (InterruptedException ex) { }
                                }
                            }
                            status.setPaused(id, false);
                        }
                        
                        if(closing) break;
                        
                        i = (i + 1) % buffers;
                    }
                    
                    synchronized(this) {
                        notifyAll();
                    } 
                } catch (IOException ex) {
                    status.error("Data transfering failed (" + Utils.getExceptionName(ex) + ")");
                }
                
            }, "Download " + id + " in");
            
            Thread tout = new Thread(()->{
                try {
                    int i = 0;
                    
                    while(true) {
                        
                        while(free[i] && !closing) {
                            synchronized(this) {
                                try {
                                    wait();
                                } catch (InterruptedException ex) { }
                            }
                        }
                        
                        if(closing) break;
                        
                            
                        if(readed[i] == -1) break;
                        out.write(buffer[i], 0, readed[i]);
                        free[i] = true;

                        status.addProgMain(readed[i]);
                        status.progs[id] += readed[i];
                        now += readed[i];
                        
                        synchronized(this) {
                            notifyAll();
                        }                      
                                                
                        i = (i + 1) % buffers;
                    }
                        
                    synchronized(this) {
                        notifyAll();
                    }
                } catch (IOException ex) {
                    status.error("Data transfering failed (" + Utils.getExceptionName(ex) + ")");
                }
                
            }, "Download " + id + " out");
                        
            tin.start();
            tout.start();
            
            try {
                tin.join();
            } catch (InterruptedException ex) { }
            try {
                tout.join();
            } catch (InterruptedException ex) { }
            
            in.close();
            connection.disconnect();
            out.close();
            
            done = true;
            status.setDone(id);
            
        } catch (IOException ex) {
            status.error("Data transfering failed (" + Utils.getExceptionName(ex) + ")");
        }
        
    }

    
    
}
