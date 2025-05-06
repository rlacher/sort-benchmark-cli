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

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Customises logging format based on verbosity.
 */
public class CustomFormatter extends Formatter
{
    /** Enables verbose logging. */
    private final boolean verbose;

    /**
     * Initialises the object with the specified verbose mode.
     *
     * @param verbose {@code true} for verbose, {@code false} for minimal output.
     */
    public CustomFormatter(final boolean verbose)
    {
        this.verbose = verbose;
    }

    /**
     * Provides a custom logging format.
     *
     * @param record The {@link LogRecord} provided.
     * @return Formatted log message (verbose or minimal).
     * @throws IllegalArgumentException If {@code record} is {@code null}.
     */
    @Override
    public String format(LogRecord record)
    {
        if(record == null)
        {
            throw new IllegalArgumentException("Log record must not be null.");
        }

        if(verbose)
        {
            return getVerboseFormat(record);
        }
        else
        {
            return getMinimalFormat(record);
        }
    }

    /** 
     * Returns the minimal log format (message only).
     *
     * @param record The {@link LogRecord} provided (non-null).
     * @return The log message followed by a line separator.
     */
    private String getMinimalFormat(final LogRecord record)
    {
        return record.getMessage() + System.lineSeparator();
    }

    /**
     * Returns the verbose log format (timestamp, level, message).
     *
     * @param record The {@link LogRecord} provided (non-null).
     * @return The formatted log message including timestamp and log level.
     */
    private String getVerboseFormat(final LogRecord record)
    {
        return String.format("[%1$tF %1$tT] %4$-7s %5$s %n",
                            new Date(record.getMillis()),
                            record.getSourceClassName(),
                            record.getSourceMethodName(),
                            record.getLevel().getLocalizedName(),
                            record.getMessage());
    }
}
