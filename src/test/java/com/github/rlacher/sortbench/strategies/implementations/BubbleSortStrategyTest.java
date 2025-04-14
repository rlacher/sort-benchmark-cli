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

package com.github.rlacher.sortbench.strategies.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;


import com.github.rlacher.sortbench.benchmark.Benchmarker;

// Unit tests for the BubbleSortStrategy class.
public class BubbleSortStrategyTest
{
    // Tests the constructor of the BubbleSortStrategy class when the benchmarker is null.
    @Test
    void constructor_givenNullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BubbleSortStrategy(null), "Constructor should throw IllegalArgumentException when benchmarker is null");
    }

    // Tests if the sort() method reports a swap given an out of order array.
    @Test
    void sort_givenTwoElementsOutOfOrder_reportsSwap()
    {
        Benchmarker mockBenchmarker = mock(Benchmarker.class);

        int[] array = { 2, 1 };
        new BubbleSortStrategy(mockBenchmarker).sort(array);

        verify(mockBenchmarker, atLeastOnce()).reportSwap();
    }

    // Tests that the sort() method does report swaps given an empty array.
    @Test
    void sort_givenEmptyArray_noSwap()
    {
        Benchmarker mockBenchmarker = mock(Benchmarker.class);

        int[] array = {};
        new BubbleSortStrategy(mockBenchmarker).sort(array);

        verify(mockBenchmarker, never()).reportSwap();
    }

    // Tests that the sort() method does not report swaps for a one-element array.
    @Test
    void sort_givenOneElement_noSwap()
    {
        Benchmarker mockBenchmarker = mock(Benchmarker.class);

        int[] array = { 1 };
        new BubbleSortStrategy(mockBenchmarker).sort(array);

        verify(mockBenchmarker, never()).reportSwap();
    }
}
