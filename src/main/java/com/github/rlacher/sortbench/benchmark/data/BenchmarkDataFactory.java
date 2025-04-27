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
    /** Random instance for use in generating benchmark data. */
    private static Random random = new Random();

    /** Private constructor to prevent instantiation of utility class. */
    private BenchmarkDataFactory() {}

    /**
     * Sets the random instance for benchmark data generation or testing purposes.
     *
     * @param random The random instance to be set.
     * @throws IllegalArgumentException If the provided random instance is {@code null}.
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
     * Creates benchmark data of the specified type and length.
     *
     * <p>This is the single entry point for generating benchmark data using this factory.
     * All requests for benchmark data should be made through this method.</p>
     *
     * @param dataType The type of benchmark data to create.
     * @param dataLength The desired length of the benchmark data. Must be a positive integer.
     * @return An instance of {@link BenchmarkData} containing the generated data.
     * @throws IllegalArgumentException If the {@code dataLength} is not positive.
     * @throws IllegalStateException If an unknown {@code dataType} is provided.
     */
    public static BenchmarkData createData(final BenchmarkData.DataType dataType, final int dataLength)
    {
        if(dataLength <= 0)
        {
            throw new IllegalArgumentException("Data length must be positive.");
        }

        BenchmarkData generatedData;

        switch(dataType)
        {
            case PARTIALLY_SORTED:
                generatedData = createPartiallySortedData(dataLength);
                break;
            case RANDOM:
                generatedData = createRandomData(dataLength);
                break;
            case REVERSED:
                generatedData = createReversedData(dataLength);
                break;
            case SORTED:
                generatedData = createSortedData(dataLength);
                break;
            default:
                throw new IllegalStateException(String.format("Unknown data type %s", dataType));
        }

        return generatedData;
    }

    /**
     * Creates sorted benchmark data of a specified length.
     *
     * <p>{@code dataLength} is guaranteed to be positive as validation is performed
     * by the calling method, {@link BenchmarkDataFactory#createData}.</p>
     *
     * @param dataLength The length of the data array to be created.
     * @return A {@link BenchmarkData} object containing sorted data.
     */
    private static BenchmarkData createSortedData(final int dataLength)
    {
        int[] data = IntStream.range(0, dataLength).toArray();
        return new BenchmarkData(data, BenchmarkData.DataType.SORTED);
    }

    /**
     * Creates reversely sorted benchmark data of a specified length.
     *
     * <p>{@code dataLength} is guaranteed to be positive as validation is performed
     * by the calling method, {@link BenchmarkDataFactory#createData}.</p>
     *
     * @param dataLength The length of the data array to be created.
     * @return A {@link BenchmarkData} object containing sorted data.
     */
    private static BenchmarkData createReversedData(final int dataLength)
    {
        int[] data = IntStream.range(0, dataLength)
            .map(i -> dataLength - i - 1)
            .toArray();
        return new BenchmarkData(data, BenchmarkData.DataType.REVERSED);
    }

    /**
     * Creates random benchmark data of a specified length.
     *
     * <p>{@code dataLength} is guaranteed to be positive as validation is performed
     * by the calling method, {@link BenchmarkDataFactory#createData}.</p>
     *
     * <p>This method uses the shared random number generator field ({@code random})
     * which can be externally set via the {@link #setRandom(Random)} method.</p>
     *
     * @param dataLength The length of the data array to be created.
     * @return A {@link BenchmarkData} object containing sorted data.
     */
    private static BenchmarkData createRandomData(final int dataLength)
    {
        int[] data = IntStream.generate(() -> random.nextInt(dataLength))
            .limit(dataLength)
            .toArray();
        return new BenchmarkData(data, BenchmarkData.DataType.RANDOM);
    }

    /**
     * Creates partially sorted benchmark data of a specified length.
     *
     * <p>{@code dataLength} is guaranteed to be positive as validation is performed
     * by the calling method, {@link BenchmarkDataFactory#createData}.</p>
     *
     * <p>This method uses the shared random number generator field ({@code random})
     * which can be externally set via the {@link #setRandom(Random)} method.</p>
     *
     * @param dataLength The length of the data array to be created.
     * @return A {@link BenchmarkData} object containing sorted data.
     */
    private static BenchmarkData createPartiallySortedData(final int dataLength)
    {
        final int half1Length = dataLength / 2;
        final int half2Length = dataLength - half1Length;

        final int[] sortedData = IntStream.range(0, half1Length).toArray();
        final int[] randomData = random.ints(half2Length, 0, dataLength).toArray();

        int[] data = random.nextBoolean()
            ? IntStream.concat(IntStream.of(sortedData), IntStream.of(randomData)).toArray()
            : IntStream.concat(IntStream.of(randomData), IntStream.of(sortedData)).toArray();

        return new BenchmarkData(data, BenchmarkData.DataType.PARTIALLY_SORTED);
    }
}
