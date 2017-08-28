// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.core;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author MaanooAk
 */
public class DownloadThreadGroup extends Thread {
    
    protected ArrayList<DownloadThread> threads;
    
    protected DownloadStatus status;
    
    protected URL url;
    protected final File file;
    protected int connections;
    protected final int minbytes;
    protected final int buffers;
    protected final int buffersize;
    
    protected long size;
    
    protected boolean working;
    protected boolean closing;
    
    public DownloadThreadGroup(URL url, File file, int connections, int minbytes, int buffers, int buffersize) {
        this.url = url;
        this.file = file;
        this.connections = connections;
        this.minbytes = minbytes;
        this.buffers = buffers;
        this.buffersize = buffersize;
        
        threads = new ArrayList<>();
        status = new DownloadStatus();
        
        working = true;
    }

    @Override
    public final void run() {

        try {
            size = getSize();
            
            if(size > 0) startDownloading(size);
            else startDownloading();
            
        } catch (IOException ex) {
            status.error("Cannot open connection (" + Utils.getExceptionName(ex) + ")");
        } 
    }
    
    protected long getSize() throws IOException {
        
        HttpURLConnection connection;
        long size;

        connection = Utils.openConnection(url);
        connection.setRequestMethod("HEAD");
        connection.connect();

        int response = connection.getResponseCode();
        
        if(response / 100 == 3) {
            // redirect to Location header field
            url = new URL(connection.getHeaderField("Location"));                        
            return getSize();
        }
        
        if (response / 100 != 2) {
            throw new IOException("ResponseCode: " + response);
        }

        size = connection.getContentLengthLong();
        if (size < 1) {
            size = -1;
        }
        
        return size;
    }
    
    /**
     * Start downloading with known size
     * 
     * @param size the size of the file
     */
    protected void startDownloading(long size) {
        
        if(!file.exists()) Utils.preallocateFile(file, size);            

        createThreads(size);
        
        threads.forEach((Thread t) -> t.start());
        
        threads.forEach((Thread t)->{
            try {
                t.join();
            } catch (InterruptedException ex) { }
        });
        
        status.changeState(closing ? DownloadStatus.State.Closed : DownloadStatus.State.Done);
    }
    
    protected void createThreads(long size) {
        
        if (connections * minbytes > size) {
            connections = (int) (size / minbytes);
            if (connections == 0)  connections = 1;
        }
        status.init(connections, size);

        long subSize = size / connections;

        long start = 0, end = subSize;
        for(int i=0; i<connections; i++) {
            if(i == connections-1) end = size;
            
            DownloadThread dt;
            if(buffers <= 1) {
                dt = new DownloadThread(i, url, file, start, end, status, buffersize);
            }else{
                dt = new DownloadThreadDoubleBuffered(i, url, file, start, end, status, buffersize, buffers);
            }
            threads.add(dt);

            start = end + 1;
            end = start + subSize;
        }
        
    }
    
    /**
     * Start downloading with unknown size
     */
    protected void startDownloading() {
        
        connections = 1;
        status.init(connections, -1);
        
        createThreads();
        
        threads.get(0).start();

        try {
            threads.get(0).join();
        } catch (InterruptedException ex) { }
        
        status.changeState(DownloadStatus.State.Done);        
    }
    
    protected void createThreads() {
        
        DownloadThread dt;
        if(buffers <= 1) {
            dt = new DownloadThread(0, url, file, 0, Long.MAX_VALUE, status, buffersize);
        }else{
            dt = new DownloadThreadDoubleBuffered(0, url, file, 0, Long.MAX_VALUE, status, buffersize, buffers);
        }
        
        threads.add(dt);        
    }
    
    // ===
    
    public void stopWorking() {
        working = false;
        
        threads.forEach((DownloadThread i) -> i.stopWorking());
                
        status.changeState(DownloadStatus.State.Pausing);
    }
    
    public void resumeWorking() {
        working = true;
        
        threads.forEach((DownloadThread i) -> i.resumeWorking());
                
        status.changeState(DownloadStatus.State.Resuming);
    }
    
    public void closeAll() {
        closing = true;
        
        threads.forEach((DownloadThread i) -> i.closeAll());
        threads.forEach((DownloadThread i) -> i.resumeWorking());
                
        status.changeState(DownloadStatus.State.Closing);
    }
    
    // Getters

    public final DownloadStatus getStatus() {
        return status;
    }
    
    public final URL getUrl() {
        return url;
    }

    public final File getFile() {
        return file;
    }

    public final int getConnections() {
        return connections;
    }    

    public boolean isClosing() {
        return closing;
    }
    
    
}
