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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        config.put("stringSetKey", new HashSet<String>(Arrays.asList("one", "two", "three")));
    }

    @Test
    void validateAndGet_validKeyAndType_returnsValue()
    {
        assertEquals("stringValue", ConfigValidator.validateAndGet(config, "stringKey", String.class),
                     "validateAndGet() should return the correct string value");
        assertEquals(123, ConfigValidator.validateAndGet(config, "integerKey", Integer.class),
                     "validateAndGet() should return the correct integer value");
        assertEquals(true, ConfigValidator.validateAndGet(config, "booleanKey", Boolean.class),
                     "validateAndGet() should return the correct boolean value");
    }

    @Test
    void validateAndGet_nullConfig_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(null, "stringKey", String.class),
                     "validateAndGet() with a null config should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_nullKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, null, String.class),
                     "validateAndGet() with a null key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_blankKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, "  ", String.class),
                     "validateAndGet() with a blank key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_nullExpectedType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, "stringKey", null),
                     "validateAndGet() with a null expected type should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_missingKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, "missingKey", String.class),
                     "validateAndGet() with a missing key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_nullValue_throwsIllegalArgumentException()
    {
        config.put("nullKey", null);
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, "nullKey", String.class),
                     "validateAndGet() with a null value should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGet_incorrectType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGet(config, "integerKey", String.class),
                     "validateAndGet() with incorrect type should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetCollection_validKeyAndType_returnsCorrectCollection()
    {
        List<String> expectedStringList = Arrays.asList("one", "two", "three");
        assertEquals(expectedStringList, new ArrayList<>(ConfigValidator.validateAndGetCollection(config, "stringListKey", String.class)),
                     "validateAndGetCollection() should return the correct string list when input is a list.");

        Set<String> expectedStringSet = new HashSet<>(Arrays.asList("one", "two", "three"));
        assertEquals(expectedStringSet, new HashSet<>(ConfigValidator.validateAndGetCollection(config, "stringSetKey", String.class)),
        "validateAndGetCollection() should return the correct string set when input is a set.");

        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3);
        assertEquals(expectedIntegerList, new ArrayList<>(ConfigValidator.validateAndGetCollection(config, "integerListKey", Integer.class)),
                     "validateAndGetCollection() should return the correct integer list when input is a list.");
    }

    @Test
    void validateAndGetCollection_nullConfig_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetCollection(null, "stringListKey", String.class),
                     "validateAndGetCollection() with a null config should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetCollection_nullKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetCollection(config, null, String.class),
                     "validateAndGetCollection() with a null key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetCollection_blankKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetCollection(config, "  ", String.class),
                     "validateAndGetCollection() with a blank key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetCollection_nullExpectedElementType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetCollection(config, "stringListKey", null),
                     "validateAndGetCollection() with a null expected element type should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetCollection_missingKey_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetCollection(config, "missingListKey", String.class),
                     "validateAndGetCollection() with a missing key should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetCollection_nullValue_throwsIllegalArgumentException()
    {
        config.put("nullListKey", null);
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetCollection(config, "nullListKey", String.class),
                     "validateAndGetCollection() with a null value should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetCollection_incorrectCollectionType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetCollection(config, "integerKey", String.class),
                     "validateAndGetCollection() with a non-collection value should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetCollection_listWithNullElement_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetCollection(config, "listWithNullElementKey", String.class),
                     "validateAndGetCollection() with a list containing a null element should throw an IllegalArgumentException");
    }

    @Test
    void validateAndGetCollection_listWithIncorrectElementType_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateAndGetCollection(config, "mixedListKey", String.class),
                     "validateAndGetCollection() with a list containing elements of incorrect type should throw an IllegalArgumentException");
    }
}
