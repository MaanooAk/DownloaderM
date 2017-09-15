// DownloaderM Copyright (c) 2014-2017 DownloaderM author list (see README.md)

package com.maanoo.downloaderm.face;

import com.maanoo.downloaderm.Defs;


/**
 *
 * @author MaanooAk
 */
public class TextUtils {

    private TextUtils() {}

    /**
     * Convert milliseconds to the exact units of days, hours, minutes, seconds.
     * <p>
     * Format: ? s | ? m ? s | ? h ? m ? s | ? d ? h ? m ? s
     *
     * @param time the time in milliseconds
     * @return the formated text
     */
    public static String time(long time) {
        time = (time + 499) / 1000; // round(time/1000)
        if (time < 60) return time + " s";
        else if (time < 3600) return (time / 60) + " m " + (time % 60) + " s";
        else if (time < 86400) return (time / 3600) + " h " + ((time / 60) % 60) + " m " + (time % 60) + " s";
        else return (time / 86400) + " d " + ((time / 3600) % 24) + " h " + ((time / 60) % 60) + " m " + (time % 60)
                + " s";
    }

    /**
     * Convert milliseconds to a rounded units of days, hours, minutes, seconds.
     * <p>
     * Format: ? s | ? m ? s | ? h ? m | ? d ? h
     *
     * @param time the time in milliseconds
     * @return the formated text
     */
    public static String timeRounded(long time) {
        time = (time + 499) / 1000; // round(time/1000)
        if (time < 60) return time + " s";
        else if (time < 3600) return (time / 60) + " m " + (time % 60) + " s";
        else if (time < 86400) return (time / 3600) + " h " + ((time / 60) % 60) + " m ";
        else return (time / 86400) + " d " + ((time / 3600) % 24) + " h ";
    }

    /**
     * Converts bytes per millisecond to a utin of Bps, KBps or MBps.
     * <p>
     * Format: ? Bps | ? KBps | ? MBps
     *
     * @param speed
     * @return the formated text
     */
    public static String speed(long speed) {
        speed *= 1000; // to bytes per seconds
        if (speed < 3024) return integer(speed) + " Bps";
        else if (speed < 3048576) return integer(speed / 1024) + " KBps";
        else return integer(speed / 1048576) + " MBps";
    }

    // Longs to strings

    private static final char[] digits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    public static String integer(long num) {

        if (num == 0) return "0";

        int size = 1;
        char sign = 0;
        final char sep = Defs.NUMBER_SEPARATOR;

        // calculate size

        if (num < 0) {
            sign = '-';
            num = -num;
            size += 2;
        }

        long p = 10;
        while (!(num < p)) {
            size += 1;
            p *= 10;
        }

        size += ((size - 1) / 3);

        // fill text

        final char buf[] = new char[size];
        int index = size - 1;

        while (num >= 1000) {
            buf[index--] = digits[(int) (num % 10)];
            num /= 10;
            buf[index--] = digits[(int) (num % 10)];
            num /= 10;
            buf[index--] = digits[(int) (num % 10)];
            num /= 10;
            buf[index--] = sep;
        }

        while (num > 0) {
            buf[index--] = digits[(int) (num % 10)];
            num /= 10;
        }

        if (sign != 0) {
            buf[0] = sign;
            buf[1] = ' ';
        }

        return new String(buf);
    }

    // Specific

    public static String percent(long x, long max) {
        return integer((100 * x) / max) + " %";
    }

    public static String dash(long x, long max) {
        return integer(x) + " / " + integer(max);
    }

}
