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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

// Unit tests for the CustomFormatter class.
public class CustomFormatterTest
{
    @Test
    void format_verboseTrue_returnsVerboseFormat()
    {
        CustomFormatter formatter = new CustomFormatter(true);
        String message = "Test message";
        LogRecord record = new LogRecord(Level.INFO, message);
        record.setInstant(Instant.ofEpochMilli(0));

        String formatted = formatter.format(record);

        assertTrue(formatted.contains("[1970-01-01 01:00:00]"), "Verbose format should contain timestamp.");
        assertTrue(formatted.contains(Level.INFO.toString()), "Verbose format should contain log level.");
        assertTrue(formatted.contains(message), "Verbose format should contain log message.");
    }

    @Test
    void format_verboseFalse_returnsMinimalFormat()
    {
        CustomFormatter formatter = new CustomFormatter(false);
        String message = "Another test";
        LogRecord record = new LogRecord(Level.WARNING, message);

        String formatted = formatter.format(record);

        assertEquals(message + System.lineSeparator(), formatted, "Minimal format should only contain the message.");
    }

    @Test
    void format_nullRecord_throwsIllegalArgumentException()
    {
        CustomFormatter formatter = new CustomFormatter(true);

        assertThrows(IllegalArgumentException.class, () -> formatter.format(null), "Formatting a null LogRecord should throw IllegalArgumentException.");
    }
}
