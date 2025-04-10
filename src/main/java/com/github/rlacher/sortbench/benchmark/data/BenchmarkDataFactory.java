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

package com.github.rlacher.sortbench.benchmark.data;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * Factory class for creating benchmark data for sorting algorithm benchmark.
 */
public class BenchmarkDataFactory
{
    /**
     * Random instance for use in generating benchmark data.
     */
    private static Random random = new Random();

    /**
     * Sets the random instance for benchmark data generation or testing purposes.
     * @param random The random instance to be set.
     * @throws IllegalArgumentException If the provided random instance is null.
     */
    public static void setRandom(final Random random)
    {
        if (random == null)
        {
            throw new IllegalArgumentException("Random instance must not be null.");
        }

        BenchmarkDataFactory.random = random;
    }

    /**
     * Creates sorted benchmark data of a specified length.
     * @param length The length of the data array to be created. Must be a greater than 0.
     * @return A BenchmarkData object containing sorted data.
     * @throws IllegalArgumentException If the length is not positive.
     */
    public static BenchmarkData createSortedData(final int length) throws IllegalArgumentException
    {
        if (length <= 0)
        {
            throw new IllegalArgumentException("Length must be positive.");
        }

        int[] data = IntStream.range(0, length).toArray();
        return new BenchmarkData(data, BenchmarkData.DataType.SORTED);
    }

    /**
     * Creates reversed benchmark data of a specified length.
     * 
     * @param length The length of the data array to be created. Must be a greater than 0.
     * @return A BenchmarkData object containing reversed data.
     * @throws IllegalArgumentException If the length is not positive.
     */
    public static BenchmarkData createReversedData(final int length) throws IllegalArgumentException
    {
        if (length <= 0)
        {
            throw new IllegalArgumentException("Length must be positive.");
        }

        int[] data = IntStream.range(0, length).map(i -> length - i - 1).toArray();

        return new BenchmarkData(data, BenchmarkData.DataType.REVERSED);
    }

    /**
     * Creates random benchmark data of a specified length.
     * 
     * @param length The length of the data array to be created. Must be a greater than 0.
     * @return A BenchmarkData object containing random data.
     * @throws IllegalArgumentException If the length is not positive.
     */
    public static BenchmarkData createRandomData(final int length) throws IllegalArgumentException
    {
        if (length <= 0)
        {
            throw new IllegalArgumentException("Length must be positive.");
        }

        int[] data = IntStream.generate(random::nextInt).limit(length).toArray();
        return new BenchmarkData(data, BenchmarkData.DataType.RANDOM);
    }

    /**
     * Creates partially sorted benchmark data of a specified length.
     * 
     * @param length The length of the data array to be created. Must be greater than or equal to 2.
     * @return A BenchmarkData object containing partially sorted data.
     * @throws IllegalArgumentException If the length is less than 2.
     */
    public static BenchmarkData createPartiallySortedData(final int length) throws IllegalArgumentException
    {
        if (length < 2)
        {
            throw new IllegalArgumentException("Length must be larger or equal to 2.");
        }

        final int half1Length = length / 2;
        final int half2Length = length - half1Length;

        final int[] sortedData = IntStream.range(0, half1Length).toArray();
        final int[] randomData = random.ints(half2Length).toArray();

        int[] data = random.nextBoolean()
            ? IntStream.concat(IntStream.of(sortedData), IntStream.of(randomData)).toArray()
            : IntStream.concat(IntStream.of(randomData), IntStream.of(sortedData)).toArray();

        return new BenchmarkData(data, BenchmarkData.DataType.PARTIALLY_SORTED);
    }
}
