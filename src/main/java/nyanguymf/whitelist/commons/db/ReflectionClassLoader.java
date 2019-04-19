/**
 * This file is the part of TemporalWhitelist plug-in.
 *
 *  From plug-in LuckPerms under MIT license.
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
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
package nyanguymf.whitelist.commons.db;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/** @author lucko (Luck) */
final class ReflectionClassLoader {
    private static final Method ADD_URL_METHOD;

    static {
        try {
            ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ReflectionClassLoader.ADD_URL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final URLClassLoader classLoader;

    public ReflectionClassLoader(final Object plugin) throws IllegalStateException {
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            this.classLoader = (URLClassLoader) classLoader;
        } else
            throw new IllegalStateException("ClassLoader is not instance of URLClassLoader");
    }

    /**
     * Loads jar from given path into java runtime.
     * <p>
     * Will return <tt>false</tt> if some exception occurred.
     *
     * @param   jarPath     Path to jar to load.
     * @return <tt>true</tt> if loaded successfully.
     */
    public boolean loadJar(final Path jarPath) {
        try {
            ReflectionClassLoader.ADD_URL_METHOD.invoke(classLoader, jarPath.toUri().toURL());
            return true;
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }
}