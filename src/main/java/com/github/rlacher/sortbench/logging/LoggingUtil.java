/*
 * Copyright (c) 2025 Rene Lacher
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
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.rlacher.sortbench.logging;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Logging-related utility class. */
public class LoggingUtil
{
    /** Private constructor to prevent instantiation of utility class. */
    @Deprecated
    private LoggingUtil() {}

    /**
     * Applies a custom formatter to all handlers associated with the root logger.
     *
     * @param formatter The {@link Formatter} to apply.
     * @throws IllegalArgumentException If {@code formatter} is {@code null}.
     */
    public static void setFormatter(final Formatter formatter)
    {
        if(formatter == null)
        {
            throw new IllegalArgumentException("Formatter must not be null.");
        }
        
        Logger rootLogger = Logger.getLogger("");

        for (Handler handler : rootLogger.getHandlers())
        {
            handler.setFormatter(formatter);
        }
    }

    /**
     * Sets the logging level for all handlers associated with the root logger.
     *
     * @param level The {@link Level} to set.
     * @throws IllegalArgumentException If {@code level} is {@code null}.
     */
    public static void setLevel(final Level level)
    {
        if(level == null)
        {
            throw new IllegalArgumentException("Level must not be null.");
        }

        Logger rootLogger = Logger.getLogger("");

        for (Handler handler : rootLogger.getHandlers())
        {
            handler.setLevel(level);
        }
        rootLogger.setLevel(level);
    }
}
