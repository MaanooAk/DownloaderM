// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.core;

import java.util.ArrayList;

/**
 *
 * @author MaanooAk
 */
public final class DownloadStatus {

    public static interface Listener {
        public void onStateChange(State state);
        public void onPauseChange(int id, boolean paused);
        public void onSubDone(int id);        
    }
    
    public static enum State {
        Preparing, 
        Downloading(true), 
        Done, 
        Error, 
        Pausing(true), 
        Pause, 
        Resuming(true),
        Closing(true),
        Closed;
        
        private final boolean updating;

        private State() {
            this.updating = false;
        }

        private State(boolean updating) {
            this.updating = updating;
        }

        public boolean isUpdating() {
            return updating;
        }
        
    }

    private State state;
    private long progMain;
    
    public long progMainMax;
    public int connections;
    
    public long progs[];
    public long progsMax[];
    
    private boolean paused[];
    private boolean done[];

    private String error;
    
    private final ArrayList<Listener> listeners;
    
    public DownloadStatus() {
        listeners = new ArrayList<>();
        
        state = State.Preparing;        
    }
    
    public void init(int connections, long size) {
        changeState(State.Downloading);
        
        this.connections = connections;
        this.progMainMax = size;
        
        progs = new long[connections];
        progsMax = new long[connections];
        paused = new boolean[connections];
        done = new boolean[connections];
        
        for(int i=0; i<connections; i++){
            progs[i] = 0;
            paused[i] = false;
            done[i] = false;
        }
    }
    
    public void error(String error) {
        this.error = error;
        
        changeState(State.Error);
    }
    
    public synchronized void addProgMain(long value) {
        progMain += value;
    }

    public void addListener(Listener l) {
        listeners.add(l);
        l.onStateChange(state);
    }
    public void removeListener(Listener l) {
        listeners.remove(l);
    }
    
    public void changeState(State state) {
        this.state = state;
        
        listeners.forEach((Listener i) -> i.onStateChange(state));
    }
    
    // pause and resume

    public synchronized void setPaused(int id, boolean value) {
        this.paused[id] = value;
        
        listeners.forEach((Listener i) -> i.onPauseChange(id, value));
        
        for(int i=0; i<paused.length; i++) {
            if(paused[i] != value && !done[i]) return;
        }
                
        changeState(value ? State.Pause : State.Downloading);
        
    }

    public boolean getPaused(int id) {
        return paused[id];
    }
    
    // done

    public synchronized void setDone(int id) {
        this.done[id] = true;
        
        listeners.forEach((Listener i) -> i.onSubDone(id));
    }

    public boolean getDone(int id) {
        return done[id];
    }
    
    // Getters for progres percentances
    
    public final double getProgressMain() {
        return getProgress(progMain, progMainMax);
    }
    
    public final double getProgress(int id) {
        return getProgress(progs[id], progsMax[id]);
    }
    
    private static double getProgress(long value, long max) {
        return ((value * 2000L) / max) / 2000.0;
    }
    
    // Getters

    public State getState() {
        return state;
    }
    
    public final long getProgMain() {
        return progMain;
    }

    public String getError() {
        return error;
    }
    
    
}
