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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Unit tests for the LoggingUtil class.
public class LoggingUtilTest
{
    private Formatter mockFormatter;
    private final Logger rootLogger = Logger.getLogger("");

    @BeforeEach
    void setUp()
    {
        mockFormatter = mock(Formatter.class);
    }

    @AfterEach
    void tearDown()
    {
        // Remove all handlers from the root logger
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers)
        {
            rootLogger.removeHandler(handler);
        }
    }

    @Test
    void setFormatter_validFormatter_appliesToRootLoggerHandlers()
    {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        rootLogger.addHandler(consoleHandler);

        LoggingUtil.setFormatter(mockFormatter);

        assertEquals(mockFormatter, consoleHandler.getFormatter(), "The provided formatter should be set on the console handler");
    }

    @Test
    void setFormatter_nullFormatter_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> LoggingUtil.setFormatter(null), "Setting a null formatter should throw IllegalArgumentException");
    }

    @Test
    void setFormatter_multipleHandlers_appliesToAll()
    {
        ConsoleHandler handler1 = new ConsoleHandler();
        ConsoleHandler handler2 = new ConsoleHandler();
        rootLogger.addHandler(handler1);
        rootLogger.addHandler(handler2);

        LoggingUtil.setFormatter(mockFormatter);

        assertEquals(mockFormatter, handler1.getFormatter(), "The provided formatter should be set on the first handler");
        assertEquals(mockFormatter, handler2.getFormatter(), "The provided formatter should be set on the second handler");
    }
}
