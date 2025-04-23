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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.results.BenchmarkMetric;
import com.github.rlacher.sortbench.strategies.SortStrategy;


// Unit tests for the Sorter class.
class SorterTest
{
    private Sorter sorter;
    private SortStrategy mockStrategy;

    @BeforeEach
    void setUp()
    {
        sorter = new Sorter();
        mockStrategy = mock(SortStrategy.class);
    }

    @Test
    void setStrategy_nullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> sorter.setStrategy(null), "setStrategy should throw IllegalArgumentException when strategy is null");
    }

    @Test
    void setSortStrategy_validArgument_callsStrategy()
    {
        assertDoesNotThrow(() -> sorter.setStrategy(mockStrategy), "setStrategy should not throw with valid non-null argument");

        int[] array = { 1, 2, 3 };

        // Return value is unused
        sorter.sort(array);

        verify(mockStrategy).sort(array);
        verifyNoMoreInteractions(mockStrategy);
    }

    @Test
    void sort_nullArray_throwsIllegalArgumentException()
    {
        sorter.setStrategy(mockStrategy);
        assertThrows(IllegalArgumentException.class, () -> sorter.sort(null), "sort should throw IllegalArgumentException when array is null");
    }

    @Test
    void sort_withoutStrategy_throwsIllegalStateException()
    {
        int[] array = { 1, 2, 3 };
        assertThrows(IllegalStateException.class, () -> sorter.sort(array), "Calling sort without a set strategy should throw IllegalStateException.");
    }

    @Test
    void sort_emptyArray_returnsResultZero()
    {
        sorter.setStrategy(mockStrategy);
        BenchmarkMetric metric = sorter.sort(new int[0]);

        assertEquals(0, metric.getValue(), "sort should return 0 for empty array");
        assertEquals(ProfilingMode.NONE, metric.getProfilingMode(), "sort should return NONE for empty array");
    }

    @Test
    void sort_nonEmptyArray_callsSortStrategy()
    {
        sorter.setStrategy(mockStrategy);

        int[] array = { 1, 2, 3 };

        sorter.sort(array);

        verify(mockStrategy).sort(array);
    }
}
