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

package com.github.rlacher.sortbench.sorter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.rlacher.sortbench.benchmark.BenchmarkResult;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.strategies.SortStrategy;

/**
 * Unit tests for the Sorter class.
 */
class SorterTest
{
    /**
     * Tests the constructor of the Sorter class when argument is null.
     */
    @Test
    void constructor_givenNullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new Sorter(null), "Constructor should throw IllegalArgumentException when strategy is null");
    }

    /**
     * Tests the constructor of the Sorter class when argument is valid.
     */
    @Test
    void constructor_givenValidArgument_doesNotThrow()
    {
        SortStrategy strategy = Mockito.mock(SortStrategy.class);

        assertDoesNotThrow(() -> new Sorter(strategy), "Constructor should not throw with valid non-null argument");
    }

    /**
     * Tests the setStrategy() method when argument is null.
     */
    @Test
    void setSortStrategy_givenNullArgument_throwsIllegalArgumentException()
    {
        SortStrategy strategy = Mockito.mock(SortStrategy.class);
        Sorter sorter = new Sorter(strategy);

        assertThrows(IllegalArgumentException.class, () -> sorter.setStrategy(null), "setStrategy() should throw IllegalArgumentException when strategy is null");
    }

    /**
     * Tests the setStrategy() method when argument is valid.
     */
    @Test
    void setSortStrategy_givenValidArgument_callsNewStrategy()
    {
        SortStrategy strategy = Mockito.mock(SortStrategy.class);
        Sorter sorter = new Sorter(strategy);
        SortStrategy newStrategy = Mockito.mock(SortStrategy.class);

        assertDoesNotThrow(() -> sorter.setStrategy(newStrategy), "setStrategy() should not throw with valid non-null argument");

        int[] array = { 1, 2, 3 };

        // Return value is unused
        sorter.sort(array);

        verify(newStrategy).sort(array);
        verifyNoMoreInteractions(strategy);
    }

    /**
     * Tests the sort() method given a null array.
     */
    @Test
    void sort_givenNullArray_throwsIllegalArgumentException()
    {
        SortStrategy strategy = Mockito.mock(SortStrategy.class);
        Sorter sorter = new Sorter(strategy);

        assertThrows(IllegalArgumentException.class, () -> sorter.sort(null), "sort() should throw IllegalArgumentException when array is null");
    }

    /**
     * Tests the sort() method given an empty array.
     */
    @Test
    void sort_givenEmptyArray_returnsResultZero()
    {
        SortStrategy strategy = Mockito.mock(SortStrategy.class);
        Sorter sorter = new Sorter(strategy);

        BenchmarkResult result = sorter.sort(new int[0]);

        assertNotNull(result, "sort() should return a non-null BenchmarkResult");
        assertEquals(0, result.getValue(), "sort() should return 0 for empty array");
        assertEquals(ProfilingMode.NONE, result.getProfilingMode(), "sort() should return NONE for empty array");
    }

    /**
     * Tests the sort() method given a non-empty array expecting the sort strategy to be called.
     */
    @Test
    void sort_givenNonEmptyArray_callsSortStrategy()
    {
        SortStrategy strategy = Mockito.mock(SortStrategy.class);
        Sorter sorter = new Sorter(strategy);
        int[] array = { 1, 2, 3 };

        sorter.sort(array);

        verify(strategy).sort(array);
    }
}
