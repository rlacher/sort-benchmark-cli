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

package com.github.rlacher.sortbench.data;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents benchmark data for sorting algorithms, encapsulating the data array and its type.
 * This class ensures immutability by creating copies of the input data.
 */
public final class BenchmarkData
{
    private final int[] data;
    private final DataType type;

    /**
     * Enumerates the types of benchmark data used for sorting algorithm benchmarking.
     */
    public enum DataType
    {
        RANDOM,
        REVERSED,
        PARTIALLY_SORTED,
        SORTED,
    }

    /**
     * Constructor for creating a BenchmarkData object.
     *
     * This constructor creates a copy of the data array to avoid external modification.
     *
     * @param data The data array to be stored (a copy is created).
     * @param type The type of the data.
     * @throws NullPointerException If the data array is null.
     */
    public BenchmarkData(final int[] data, final DataType type) throws NullPointerException
    {
        Objects.requireNonNull(data, "The data array must not be null.");

        this.data = Arrays.copyOf(data, data.length);
        this.type = type;
    }

    /**
     * Retrieves a copy of the data array.
     *
     * @return A copy of the data array to void modification.
     */
    public int[] getDataCopy()
    {
        return Arrays.copyOf(data, data.length);
    }

    /**
     * Returns the length of the data array.
     *
     * Benchmark data of various lengths are required for performance assessment of the sorting algorithms.
     *
     * @return  The length of the data array.
     */
    public int getLength()
    {
        return data.length;
    }

    /**
     * Returns the type of the data.
     * 
     * Benchmark data of various types help assess the performance of sorting algorithms.
     */
    public DataType getType()
    {
        return type;
    }

    /**
     * Returns a string representation of the BenchmarkData object.
     * 
     * @return A string representation of the BenchmarkData object.
     */
    @Override
    public String toString()
    {
        final int MAX_PRINT_LENGTH = 5;

        return  "{" +
                "length=" + data.length +
                ", type=" + type +
                ", data=" + IntStream.of(data).limit(data.length > MAX_PRINT_LENGTH ? MAX_PRINT_LENGTH : data.length).boxed().map(String::valueOf).collect(Collectors.joining(", ", "[", (data.length > MAX_PRINT_LENGTH) ? ", ...]" : "]")) +
                "}";
    }
}
