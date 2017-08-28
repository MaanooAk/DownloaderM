// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.core;

/**
 *
 * @author MaanooAk
 */
public abstract class SpeedMonitor {
    
    protected long speed;
    
    public abstract long give(long time, long bytes);

    public long getSpeed() {
        return speed;
    }
    
    public static class Single extends SpeedMonitor {
        
        private long time;
        private long bytes;
        private long dtime;
        private long dbytes;
        
        public Single(long time, long bytes) {
            this.time = time;
            this.bytes = bytes;
        }

        @Override
        public long give(long time, long bytes) {
            dtime = time - this.time;
            dbytes = bytes - this.bytes;
            this.time = time;
            this.bytes = bytes;
            return speed = dbytes/dtime;
        }
        
    }
    
    public static class Group extends SpeedMonitor {
        
        private Single group[];
        private int index;

        public Group(long time, long bytes, int count) {
            group = new Single[count];
            
            for(int i=0; i<count; i++) {
                group[i] = new Single(time, bytes);
            }
            
            index = 0;
        }

        @Override
        public long give(long time, long bytes) {
            index = (index + 1) % group.length;
            return speed = group[index].give(time, bytes);
        }
        
    }
}
