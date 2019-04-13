/**
 * This file is the part of TemporalWhitelist plug-in.
 *
 * Copyright (c) 2019 Vasiliy (NyanGuyMF)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sh.okx.timeapi.api;

import java.util.concurrent.TimeUnit;

/**
 * <a href="https://github.com/okx-code/Rankup3">Original source and copyrighter<a>
 *
 * @author okx-code
 */
public final class TimeAPI {
    private static long DAYS_IN_WEEK = 7;
    private static long DAYS_IN_MONTH = 30;
    private static long DAYS_IN_YEAR = 365;

    private long seconds;

    public TimeAPI(final String time) {
        reparse(time);
    }

    public TimeAPI(final long seconds) {
        this.seconds = seconds;
    }

    public TimeAPI reparse(final String time) {
        seconds = 0;

        TimeScanner scanner = new TimeScanner(time
                .replace(" ", "")
                .replace("and", "")
                .replace(",", "")
                .toLowerCase());

        long next;
        while(scanner.hasNext()) {
            next = scanner.nextLong();
            switch(scanner.nextString()) {
                case "s":
                case "sec":
                case "secs":
                case "second":
                case "seconds":
                    seconds += next;
                    break;
                case "m":
                case "min":
                case "mins":
                case "minute":
                case "minutes":
                    seconds += TimeUnit.MINUTES.toSeconds(next);
                    break;
                case "h":
                case "hr":
                case "hrs":
                case "hour":
                case "hours":
                    seconds += TimeUnit.HOURS.toSeconds(next);
                    break;
                case "d":
                case "dy":
                case "dys":
                case "day":
                case "days":
                    seconds += TimeUnit.DAYS.toSeconds(next);
                    break;
                case "w":
                case "week":
                case "weeks":
                    seconds += TimeUnit.DAYS.toSeconds(next * TimeAPI.DAYS_IN_WEEK);
                    break;
                case "mo":
                case "mon":
                case "mnth":
                case "month":
                case "months":
                    seconds += TimeUnit.DAYS.toSeconds(next * TimeAPI.DAYS_IN_MONTH);
                    break;
                case "y":
                case "yr":
                case "yrs":
                case "year":
                case "years":
                    seconds += TimeUnit.DAYS.toSeconds(next * TimeAPI.DAYS_IN_YEAR);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return this;
    }


    public long getNanoseconds() {
        return TimeUnit.SECONDS.toNanos(seconds);
    }

    public long getMicroseconds() {
        return TimeUnit.SECONDS.toMicros(seconds);
    }

    public long getMilliseconds() {
        return TimeUnit.SECONDS.toMillis(seconds);
    }

    public long getSeconds() {
        return seconds;
    }

    public double getMinutes() {
        return seconds / 60D;
    }

    public double getHours() {
        return seconds / 3600D;
    }

    public double getDays() {
        return seconds / 86400D;
    }

    public double getWeeks() {
        return getDays() / TimeAPI.DAYS_IN_WEEK;
    }

    public double getMonths() {
        return getDays() / TimeAPI.DAYS_IN_MONTH;
    }

    public double getYears() {
        return getDays() / TimeAPI.DAYS_IN_YEAR;
    }
}
