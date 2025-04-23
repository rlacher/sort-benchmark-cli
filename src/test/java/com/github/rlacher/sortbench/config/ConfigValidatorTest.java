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

package com.github.rlacher.sortbench.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigValidatorTest
{
    private Map<String, Object> config;

    @BeforeEach
    void setUp()
    {
        config = new HashMap<>();
        config.put("stringKey", "stringValue");
        config.put("integerKey", 123);
        config.put("booleanKey", true);
        config.put("stringListKey", Arrays.asList("one", "two", "three"));
        config.put("integerListKey", Arrays.asList(1, 2, 3));
        config.put("mixedListKey", Arrays.asList("hello", 42));
        config.put("nullListKey", null);
        config.put("listWithNullElementKey", Arrays.asList("a", null, "c"));
    }

    @Test
    void validateAndGet_validKeyAndType_returnsValue()
    {
        assertEquals("stringValue", ConfigValidator.validateAndGet(config, "stringKey", String.class),
                     "validateAndGet should return the correct string value");
        assertEquals(123, ConfigValidator.validateAndGet(config, "integerKey", Integer.class),
                     "validateAndGet should return the correct integer value");
        assertEquals(true, ConfigValidator.validateAndGet(config, "booleanKey", Boolean.class),
                     "validateAndGet should return the correct boolean value");
    }

    @Test
    void validateAndGet_nullConfig_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(null, "stringKey", String.class),
                     "validateAndGet with a null config should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_nullKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, null, String.class),
                     "validateAndGet with a null key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_blankKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, "  ", String.class),
                     "validateAndGet with a blank key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_nullExpectedType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, "stringKey", null),
                     "validateAndGet with a null expected type should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_missingKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, "missingKey", String.class),
                     "validateAndGet with a missing key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_nullValue_throwsIllegalArgumentException()
    {
        config.put("nullKey", null);
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, "nullKey", String.class),
                     "validateAndGet with a null value should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_incorrectType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, "integerKey", String.class),
                     "validateAndGet with incorrect type should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetList_validKeyAndType_returnsCorrectList()
    {
        List<String> expectedStringList = Arrays.asList("one", "two", "three");
        assertEquals(expectedStringList, ConfigValidator.validateAndGetList(config, "stringListKey", String.class),
                     "validateAndGetList should return the correct string list");

        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3);
        assertEquals(expectedIntegerList, ConfigValidator.validateAndGetList(config, "integerListKey", Integer.class),
                     "validateAndGetList should return the correct integer list");
    }

    @Test
    void validateAndGetList_nullConfig_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetList(null, "stringListKey", String.class),
                     "validateAndGetList with a null config should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetList_nullKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetList(config, null, String.class),
                     "validateAndGetList with a null key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetList_blankKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetList(config, "  ", String.class),
                     "validateAndGetList with a blank key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetList_nullExpectedElementType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetList(config, "stringListKey", null),
                     "validateAndGetList with a null expected element type should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetList_missingKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetList(config, "missingListKey", String.class),
                     "validateAndGetList with a missing key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetList_nullValue_throwsIllegalArgumentException()
    {
        config.put("nullListKey", null);
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetList(config, "nullListKey", String.class),
                     "validateAndGetList with a null value should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetList_incorrectListType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetList(config, "integerKey", String.class),
                     "validateAndGetList with a non-list value should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetList_listWithNullElement_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetList(config, "listWithNullElementKey", String.class),
                     "validateAndGetList with a list containing a null element should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetList_listWithIncorrectElementType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetList(config, "mixedListKey", String.class),
                     "validateAndGetList with a list containing elements of incorrect type should throw an IllegalArgumentException");
    }
}
