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

import java.util.logging.Logger;

import com.github.rlacher.sortbench.benchmark.Benchmarker;
import com.github.rlacher.sortbench.results.BenchmarkMetric;
import com.github.rlacher.sortbench.sorter.Sorter;
import com.github.rlacher.sortbench.strategies.SortStrategy;

/**
 * Implements the heap sort algorithm as a sorting strategy.
 * 
 * This class implements the {@link SortStrategy} interface and provides a method
 * to perform heap sort on an array of integers.
 */
public class HeapSortStrategy implements SortStrategy
{
    /** 
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(HeapSortStrategy.class.getName());

    /**
     * The benchmarker used for profiling.
     */
    private Benchmarker benchmarker;

    /**
     * Constructor for the HeapSortStrategy class.
     * 
     * @param benchmarker The benchmarker to be used for profiling.
     * @throws IllegalArgumentException If the benchmarker is null.
     */
    public HeapSortStrategy(Benchmarker benchmarker)
    {
        if (benchmarker == null)
        {
            throw new IllegalArgumentException("Benchmarker must not be null.");
        }

        this.benchmarker = benchmarker;
        logger.finest(HeapSortStrategy.class.getSimpleName() + " initialised with benchmarker.");
    }

    /**
     * Returns the unique name of the HeapSortStrategy
     *
     * @return The unique name of the strategy.
     */
    @Override
    public String name()
    {
        return "HeapSort";
    }

    /**
     * Sorts the provided array in ascending order using the heap sort algorithm.
     * 
     * Summary of heap sort algorithm:
     * 
     * Heap Sort initially builds a max heap from the input array using {@link HeapSortStrategy#buildMaxHeap}
     * It then repeatedly extracts the maximum element (the root of the heap) and places it at the end
     * of the array. After each extraction, the heap property is restored for the remaining elements
     * through a {@link HeapSortStrategy#heapify} operation. This continues until the entire array
     * is sorted in ascending order.
     *
     * Input array validation is assumed to be performed by the calling {@link Sorter} class.
     * 
     * @param array The array to be sorted.
     * @return A {@link BenchmarkMetric} containing performance information about the sorting process.
     * @throws NullPointerException If array is null (should not happen).
     */
    @Override
    public BenchmarkMetric sort(final int[] array)
    {
        benchmarker.startProfiling();
        buildMaxHeap(array);
        int heapSize = array.length;
        for(int i = array.length -1; i > 0; --i)
        {
           swap(array, 0, i);
           benchmarker.reportSwap();
           --heapSize;
           heapify(array, heapSize, 0);
        }
        benchmarker.stopProfiling();
        logger.finest(HeapSortStrategy.class.getSimpleName() + " completed sorting.");

        return benchmarker.getMetric();
    }

    /**
     * Builds a max heap (binary heap where the value of each node is greater than
     * or equal to the value of its children) in-place within the given array.
     * 
     * @param array The array to be transformed into a max heap.
     * @throws NullPointerException If array is null.
     */
    private void buildMaxHeap(final int[] array)
    {
       final int length = array.length;
       // Start from last non-leaf node and heapify upwards
       for(int i = (length/2) -1 ; i >= 0; --i)
       {
           heapify(array, length, i);
       }
    }

    /**
     * Maintains the max heap property for a subtree rooted at a given index.
     * 
     * Swaps the root with its largest child if the child is larger, then recursively
     * heapifies the affected subtree to maintain the max heap property.
     * 
     * @param array The array representing the heap.
     * @param heapSize The number of elements in the heap portion of {@code array}.
     * @param root The index of the subtree root to heapify.
     * @throws NullPointerException If array is null.
     */
    private void heapify(final int[] array, final int heapSize, final int root)
    {
       int largest = root;
       int left = 2 * root + 1;
       int right = 2 * root + 2;

       if(left < heapSize && array[left] > array[largest])
       {
           largest = left;
       }
       if(right < heapSize && array[right] > array[largest])
       {
           largest = right;
       }
       
       if(largest != root)
       {
           swap(array, largest, root);
           benchmarker.reportSwap();
           heapify(array, heapSize, largest);
       }
    }
}
 