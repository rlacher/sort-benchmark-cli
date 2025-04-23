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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;

// Unit tests for the BenchmarkContext class.
class BenchmarkContextTest
{
    private BenchmarkData.DataType dataType;
    private int dataLength;
    private String sortStrategyName;
    private BenchmarkContext context;

    @BeforeEach
    void setUp()
    {
        dataType = BenchmarkData.DataType.RANDOM;
        dataLength = 1000;
        sortStrategyName = "BubbleSort";
        context = new BenchmarkContext(dataType, dataLength, sortStrategyName);
    }

    @Test
    void constructor_validArguments_contextCreated()
    {
        assertEquals(dataType, context.getDataType());
        assertEquals(dataLength, context.getDataLength());
        assertEquals(sortStrategyName, context.getSortStrategyName());
    }

    @Test
    void constructor_nullDataType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkContext(null, dataLength, sortStrategyName));
    }

    @Test
    void constructor_negativeDataLength_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkContext(dataType, -1, sortStrategyName));
    }

    @Test
    void constructor_nullSortStrategyName_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkContext(dataType, dataLength, null));
    }

    @Test
    void equals_sameObject_returnsTrue()
    {
        assertEquals(context, context);
    }

    @Test
    void equals_equalObjects_returnsTrue()
    {
        BenchmarkContext context2 = new BenchmarkContext(dataType, dataLength, sortStrategyName);
        assertEquals(context, context2);
    }

    @Test
    void equals_differentObjects_returnsFalse()
    {
        BenchmarkContext context2 = new BenchmarkContext(dataType, dataLength, "Insertion Sort");
        assertNotEquals(context, context2);
    }

    @Test
    void hashCode_equalObjects_returnsSameHashCode()
    {
        BenchmarkContext context2 = new BenchmarkContext(dataType, dataLength, sortStrategyName);
        assertEquals(context.hashCode(), context2.hashCode());
    }

    @Test
    void toString_validContext_returnsStringContainingFields()
    {
        String contextString = context.toString();
        assertTrue(contextString.contains(sortStrategyName), "toString should contain the name of the sort strategy.");
        assertTrue(contextString.contains(String.valueOf(dataLength)), "toString should contain the data length.");
        assertTrue(contextString.contains(dataType.toString()), "toString should contain the string representation of the data type.");
    }

    @Test
    void compareTo_differentDataTypes_correctOrder()
    {
        BenchmarkContext context1 = new BenchmarkContext(BenchmarkData.DataType.RANDOM, 100, "BubbleSort");
        BenchmarkContext context2 = new BenchmarkContext(BenchmarkData.DataType.SORTED, 100, "BubbleSort");
        assertTrue(context1.compareTo(context2) < 0, "RANDOM should come before SORTED");
        assertTrue(context2.compareTo(context1) > 0, "SORTED should come after RANDOM");
    }

    @Test
    void compareTo_differentDataLengths_correctOrder()
    {
        BenchmarkContext context1 = new BenchmarkContext(BenchmarkData.DataType.RANDOM, 100, "BubbleSort");
        BenchmarkContext context2 = new BenchmarkContext(BenchmarkData.DataType.RANDOM, 200, "BubbleSort");
        assertTrue(context1.compareTo(context2) < 0, "100 should come before 200");
        assertTrue(context2.compareTo(context1) > 0, "200 should come after 100");
    }

    @Test
    void compareTo_nullOther_throwsIllegalArgumentException()
    {
        BenchmarkContext context1 = new BenchmarkContext(BenchmarkData.DataType.RANDOM, 100, "BubbleSort");
        assertThrows(IllegalArgumentException.class, () -> context1.compareTo(null), "Should throw IllegalArgumentException when comparing to null");
    }
}
