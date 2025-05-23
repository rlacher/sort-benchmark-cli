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

import com.github.rlacher.sortbench.strategies.SortStrategy;
import com.github.rlacher.sortbench.benchmark.Benchmarker;

// Unit tests for the BubbleSortStrategy class.
public class BubbleSortStrategyTest
{
    private Benchmarker mockBenchmarker;
    private SortStrategy bubbleSortStrategy;

    @BeforeEach
    void setUp()
    {
        mockBenchmarker = mock(Benchmarker.class);
        bubbleSortStrategy = new BubbleSortStrategy(mockBenchmarker);
    }

    @Test
    void constructor_nullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BubbleSortStrategy(null), "Constructor should throw IllegalArgumentException when benchmarker is null");
    }

    @Test
    void sort_twoElementsOutOfOrder_reportsSwap()
    {
        int[] array = { 2, 1 };
        bubbleSortStrategy.sort(array);
        verify(mockBenchmarker, atLeastOnce()).reportSwap();
    }

    @Test
    void sort_emptyArray_noSwap()
    {
        int[] array = {};
        bubbleSortStrategy.sort(array);

        verify(mockBenchmarker, never()).reportSwap();
    }

    @Test
    void sort_oneElementArray_noSwap()
    {
        int[] array = { 1 };
        bubbleSortStrategy.sort(array);
        verify(mockBenchmarker, never()).reportSwap();
    }
}
