// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.core;

/**
 * Calculates the speed of completed bytes by giving the the passed time and the
 * completed bytes at intervals.
 * <p>
 * To update the state, the {@code give} method must be called.
 * <p>
 * The calculated speed is units of bytes unit given per time unit given.
 *
 * @author MaanooAk
 */
public abstract class SpeedMonitor {

    protected long speed;

    /**
     * Give the current values of the passed time and the completed bytes.
     *
     * @param time the time passed
     * @param bytes the bytes completed
     * @return the current speed
     */
    public abstract long give(long time, long bytes);

    /**
     * Get the current speed.
     *
     * @return the speed
     */
    public final long getSpeed() {
        return speed;
    }

    /**
     * Calculates based on the last update.
     *
     * @see SpeedMonitor
     *
     * @author MaanooAk
     */
    public static final class Single extends SpeedMonitor {

        private long time;

        private long bytes;

        /**
         * Initialize with the current values of the passed time and the completed
         * bytes.
         *
         * @param time the time passed
         * @param bytes the bytes completed
         */
        public Single(long time, long bytes) {
            this.time = time;
            this.bytes = bytes;
        }

        @Override
        public long give(long time, long bytes) {
            final long dtime = time - this.time;
            final long dbytes = bytes - this.bytes;
            this.time = time;
            this.bytes = bytes;
            return speed = dbytes / dtime;
        }

    }

    /**
     * Calculates based on the last multiple updates.
     *
     * @see SpeedMonitor
     *
     * @author MaanooAk
     */
    public static final class Group extends SpeedMonitor {

        private final Single group[];

        private int index;

        /**
         * Initialize with the current values of the passed time, the completed bytes
         * and the number of updates used to calculate the speed.
         *
         * @param time the time passed
         * @param bytes the bytes completed
         * @param count the number of updates
         */
        public Group(long time, long bytes, int count) {
            group = new Single[count];

            for (int i = 0; i < count; i++) {
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
