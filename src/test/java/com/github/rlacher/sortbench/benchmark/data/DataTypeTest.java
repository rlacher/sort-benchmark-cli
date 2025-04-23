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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.rlacher.sortbench.benchmark.data.BenchmarkData.DataType;

// Unit tests for the BenchmarkData.DataType enum.
public class DataTypeTest
{
    @Test
    void fromString_validValues_returnsCorrectEnum()
    {
        assertEquals(DataType.PARTIALLY_SORTED, DataType.fromString("partially_sorted"), "fromString with 'partially_sorted' should return PARTIALLY_SORTED");
        assertEquals(DataType.RANDOM, DataType.fromString("RANDOM"), "fromString with 'RANDOM' should return RANDOM");
        assertEquals(DataType.REVERSED, DataType.fromString("reversed"), "fromString with 'reversed' should return REVERSED");
        assertEquals(DataType.SORTED, DataType.fromString("Sorted"), "fromString with 'Sorted' (case-insensitive) should return SORTED");
        assertEquals(DataType.SORTED, DataType.fromString(" sorted "), "fromString with ' sorted ' (whitespace) should return SORTED");
    }

    @Test
    void fromString_nullValue_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> DataType.fromString(null), "fromString with null should throw an IllegalArgumentException");
    }

    @Test
    void fromString_blankValue_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> DataType.fromString(""), "fromString with empty string should throw an IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> DataType.fromString(" "), "fromString with whitespace should throw an IllegalArgumentException");
    }

    @Test
    void fromString_invalidValue_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> DataType.fromString("invalid"), "fromString with 'invalid' should throw an IllegalArgumentException");
    }
}
