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

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * <a href="https://github.com/okx-code/Rankup3">Original source and copyrighter<a>
 *
 * @author okx-code
 */
final class TimeScanner {
    private char[] time;
    private int index = 0;

    public TimeScanner(final String time) {
        this.time = time.toCharArray();
    }

    public boolean hasNext() {
        return index < time.length-1;
    }

    public long nextLong() {
        return Long.parseLong(String.valueOf(next(Character::isDigit)));
    }

    public String nextString() {
        return String.valueOf(next(Character::isAlphabetic));
    }

    private char[] next(final Predicate<Character> whichSatisfies) {
        int startIndex = index;
        while(++index < time.length && whichSatisfies.test(time[index])) {

        }
        return Arrays.copyOfRange(time, startIndex, index);
    }
}
