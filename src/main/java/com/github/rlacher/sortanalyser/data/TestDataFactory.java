package com.github.rlacher.sortanalyser.data;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * Factory class for creating test data for sorting algorithm benchmark.
 */
public class TestDataFactory
{
    /**
     * Creates sorted test data of a specified length.
     * @param length The length of the data array to be created.
     * @return A TestData object containing sorted data.
     * @throws IllegalArgumentException If the length is not positive.
     */
    public static TestData createSortedData(final int length) throws IllegalArgumentException
    {
        if (length <= 0)
        {
            throw new IllegalArgumentException("Length must be positive.");
        }

        int[] data = IntStream.range(0, length).toArray();
        return new TestData(data, TestData.DataType.SORTED);
    }

    /**
     * Creates reversed test data of a specified length.
     * 
     * @param length The length of the data array to be created.
     * @return A TestData object containing reversed data.
     * @throws IllegalArgumentException If the length is not positive.
     */
    public static TestData createReversedData(final int length) throws IllegalArgumentException
    {
        if (length <= 0)
        {
            throw new IllegalArgumentException("Length must be positive.");
        }

        int[] data = IntStream.range(0, length).map(i -> length - i - 1).toArray();

        return new TestData(data, TestData.DataType.REVERSED);
    }

    /**
     * Creates random test data of a specified length.
     * 
     * @param length The length of the data array to be created.
     * @return A TestData object containing random data.
     * @throws IllegalArgumentException If the length is not positive.
     */
    public static TestData createRandomData(final int length) throws IllegalArgumentException
    {
        if (length <= 0)
        {
            throw new IllegalArgumentException("Length must be positive.");
        }

        Random random = new Random();
        int[] data = IntStream.generate(random::nextInt).limit(length).toArray();
        return new TestData(data, TestData.DataType.RANDOM);
    }

    /**
     * Creates partially sorted test data of a specified length.
     * 
     * @param length The length of the data array to be created.
     * @return A TestData object containing partially sorted data.
     * @throws IllegalArgumentException If the length is not positive or less than 2.
     */
    public static TestData createPartiallySortedData(final int length) throws IllegalArgumentException
    {
        if (length < 2)
        {
            throw new IllegalArgumentException("Length must be larger or equal to 2.");
        }

        final int half1Length = length / 2;
        final int half2Length = length - half1Length;

        final int[] sortedData = IntStream.range(0, half1Length).toArray();
        final Random random = new Random();
        final int[] randomData = random.ints(half2Length).toArray();

        int[] data = random.nextBoolean()
            ? IntStream.concat(IntStream.of(sortedData), IntStream.of(randomData)).toArray()
            : IntStream.concat(IntStream.of(randomData), IntStream.of(sortedData)).toArray();

        return new TestData(data, TestData.DataType.PARTIALLY_SORTED);
    } 
}
