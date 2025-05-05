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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.rlacher.sortbench.benchmark.Benchmarker;

// Unit tests for the HeapSortStrategy class.
public class HeapSortStrategyTest
{
    private Benchmarker mockBenchmarker;
    private HeapSortStrategy heapSortStrategy;

    @BeforeEach
    void setUp()
    {
        mockBenchmarker = mock(Benchmarker.class);
        heapSortStrategy = new HeapSortStrategy(mockBenchmarker);
    }

    @Test
    void constructor_givenNullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new HeapSortStrategy(null), "Constructor should throw IllegalArgumentException when benchmarker is null");
    }

    @Test
    void sort_twoElementsSorted_reportsTwoSwaps()
    {
        int[] array = { 1, 2 };
        heapSortStrategy.sort(array);
 
        // Two swaps expected: one during heap construction, one to move root to the end.
        verify(mockBenchmarker, times(2)).reportSwap();
    }

    @Test
    void sort_twoElementsOutOfOrder_reportsOneSwap()
    {
        int[] array = { 2, 1 };
        heapSortStrategy.sort(array);
 
        // One swap expected within the heapify process to move root to the end.
        verify(mockBenchmarker, times(1)).reportSwap();
    }
}
