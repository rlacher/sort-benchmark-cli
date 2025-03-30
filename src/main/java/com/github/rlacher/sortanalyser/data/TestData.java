package com.github.rlacher.sortanalyser.data;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents test data for sorting algorithms, encapsulating the data array and its type.
 * This class ensures immutability by creating copies of the input data.
 */
public final class TestData
{
    private final int[] data;
    private final DataType type;

    /**
     * Enumerates the types of test data used for sorting algorithm benchmarking.
     */
    public enum DataType
    {
        RANDOM,
        REVERSED,
        PARTIALLY_SORTED,
        SORTED,
    }

    /**
     * Constructor for creating a TestData object.
     *
     * This constructor creates a copy of the data array to avoid external modification.
     *
     * @param data The data array to be stored (a copy is created).
     * @param type The type of the data.
     * @throws NullPointerException If the data array is null.
     */
    public TestData(final int[] data, final DataType type) throws NullPointerException
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
     * Test data of various lengths are required for performance assessment of the sorting algorithms.
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
     * Test data of various types help assess the performance of sorting algorithms.
     */
    public DataType getType()
    {
        return type;
    }

    /**
     * Returns a string representation of the TestData object.
     * 
     * @return A string representation of the TestData object.
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
