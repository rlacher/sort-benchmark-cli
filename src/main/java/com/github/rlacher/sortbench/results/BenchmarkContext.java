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

package com.github.rlacher.sortbench.results;

import java.util.Objects;

import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;

/**
* Represents the contextual parameters of a benchmark run.
*
* This class encapsulates the data type, data length, and sorting algorithm
* used during a benchmark execution. Instances of this class are used to
* create {@link BenchmarkResult} and {@link AggregatedResult} objects.
*
* This class is immutable, ensuring that context parameters cannot be modified after creation.
*/
public final class BenchmarkContext implements Comparable<BenchmarkContext>
{
    /** The type of input data that was sorted. */
    private final BenchmarkData.DataType dataType;

    /** The length of the input data that was sorted. */
    private final int dataLength;

    /** The unique name of the sorting strategy used. */
    private final String sortStrategyName;

    /**
    * Constructs a new BenchmarkContext object.
    *
    * @param dataType The type of input data.
    * @param dataLength The length of the input data.
    * @param sortStrategyName The name of the sorting strategy.
    * @throws IllegalArgumentException If dataType or sortStrategyName are null, or dataLength is negative.
    */
    public BenchmarkContext(final BenchmarkData.DataType dataType, final int dataLength, final String sortStrategyName)
    {
        if (dataType == null)
        {
            throw new IllegalArgumentException("Data type must not be null.");
        }
        if (dataLength < 0)
        {
            throw new IllegalArgumentException("Data length must not be negative.");
        }
        if (sortStrategyName == null)
        {
            throw new IllegalArgumentException("Sort strategy name must not be null.");
        }

        this.dataType = dataType;
        this.dataLength = dataLength;
        this.sortStrategyName = sortStrategyName;
    }

    /**
    * Gets the data type.
    *
    * @return The data type.
    */
    public BenchmarkData.DataType getDataType()
    {
        return dataType;
    }

    /**
    * Gets the data length.
    *
    * @return The data length.
    */
    public int getDataLength()
    {
        return dataLength;
    }

    /**
    * Gets the sort strategy name.
    *
    * @return The sort strategy name.
    */
    public String getSortStrategyName()
    {
        return sortStrategyName;
    }

    /**
    * Returns a string representation of the BenchmarkContext object.
    *
    * @return A string representation of the object.
    */
    @Override
    public String toString()
    {
        return String.format("{dataType=%s, dataLength=%d, sortStrategyName=%s}", dataType, dataLength, sortStrategyName);
    }

    /**
    * Indicates whether some other object is "equal to" this one.
    *
    * @param obj The object with which to compare.
    * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
    */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        BenchmarkContext that = (BenchmarkContext) obj;

        return dataType == that.dataType && dataLength == that.dataLength && sortStrategyName.equals(that.sortStrategyName);
    }

    /**
    * Returns a hash code value for the object.
    *
    * @return A hash code value for this object.
    */
    @Override
    public int hashCode()
    {
        return Objects.hash(dataType, dataLength, sortStrategyName);
    }

    @Override
    /**
     * Compares this BenchmarkContext for order: dataType, dataLength, sortStrategyName.
     *
     * @param other The BenchmarkContext to be compared with.
     * @return Negative, zero, or positive integer for less, equal, or greater.
     * @throws IllegalArgumentException If other is null.
     */
    public int compareTo(BenchmarkContext other)
    {
        if (other == null)
        {
            throw new IllegalArgumentException("Cannot compare to null");
        }

        // Compare by data type first
        int dataTypeComparison = this.dataType.compareTo(other.dataType);
        if (dataTypeComparison != 0)
        {
            return dataTypeComparison;
        }

        // If data types are equal, compare by data length
        int dataLengthComparison = Integer.compare(this.dataLength, other.dataLength);
        if (dataLengthComparison != 0)
        {
            return dataLengthComparison;
        }

        // If data types and data lengths are equal, compare by sort strategy name
        return this.sortStrategyName.compareTo(other.sortStrategyName);
    }
}