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

package com.github.rlacher.sortbench.strategies;

import java.util.logging.Logger;

/**
 * A dummy implementation of the {@link SortStrategy} interface.
 * 
 * This class does not perform any sorting.
 */
public class DummySortStrategy implements SortStrategy
{
    /** 
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(DummySortStrategy.class.getName());

    /**
     * Does not sort the provided array. Prints a message to the console instead.
     *
     * @param array The array to be sorted (ignored).
     * returns The number of swaps performed during the sorting process (always 0).
     */
    @Override
    public long sort(final int[] array)
    {
        logger.info("I am a dummy sorter and do not actually sort.");
        return 0;
    }
}
