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

package com.github.rlacher.sortbench.dummy;

import java.util.Objects;
import java.util.logging.Logger;

import com.github.rlacher.sortbench.core.AbstractSortAlgorithm;

/**
 * A dummy implementation of the {@link AbstractSortAlgorithm} class.
 * 
 * This class does not perform any sorting.
 */
public class DummySorter extends AbstractSortAlgorithm
{
    /** 
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(DummySorter.class.getName());

	/**
     * Does not sort the provided array. Prints a message to the console instead.
     *
     * @param array The array to be sorted (ignored).
     * @throws NullPointerException If the input array is null.
     */
	@Override
	public void sort(final int[] array) throws NullPointerException
	{
		Objects.requireNonNull(array, "Array must not be null");

		logger.info("I am a dummy sorter and do not actually sort.");
	}
}
