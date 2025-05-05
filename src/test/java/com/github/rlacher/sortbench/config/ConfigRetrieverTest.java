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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// Unit tests for the ConfigRetriever class.
class ConfigRetrieverTest
{
    @Mock
    private Map<String, Object> config;
    private Set<String> keys;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp()
    {
        openMocks = MockitoAnnotations.openMocks(this);
        keys = Collections.emptySet();
    }

    @AfterEach
    void tearDown() throws Exception
    {
        openMocks.close();
    }

    @Test
    void validateAndFilterMap_configNull_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigRetriever.validateAndFilterMap(null, keys, false),
            "Should throw IllegalArgumentException when config is null");
    }

    @Test
    void validateAndFilterMap_keysNull_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> ConfigRetriever.validateAndFilterMap(config, null, false),
            "Should throw IllegalArgumentException when keys is null");
    }

    @Test
    void validateAndFilterMap_keysEmpty_returnsEmptyMap()
    {
        when(config.keySet()).thenReturn(Collections.emptySet());

        Map<String, Object> result = ConfigRetriever.validateAndFilterMap(config, keys, false);

        assertEquals(Collections.emptyMap(), result, "Should return empty map when keys is empty");
    }

    @Test
    void validateAndFilterMap_keysFound_returnsValues()
    {
        keys = Set.of("key1", "Key2");
        when(config.keySet()).thenReturn(Set.of("key1", "Key2"));
        when(config.get("key1")).thenReturn("value1");
        when(config.get("Key2")).thenReturn("value2");

        Map<String, Object> result = ConfigRetriever.validateAndFilterMap(config, keys, false);

        Map<String, Object> expected = new HashMap<>();
        expected.put("key1", "value1");
        expected.put("Key2", "value2");
        assertEquals(expected, result, "Should return correct values when keys are found");
    }

    @Test
    void validateAndFilterMap_keyNotFoundAndThrowIsSet_throwsIllegalArgumentException()
    {
        keys = Set.of("nonExistentKey");
        when(config.keySet()).thenReturn(Collections.emptySet());
        final boolean throwIfNotFound = true;

        assertThrows(IllegalArgumentException.class, () -> ConfigRetriever.validateAndFilterMap(config, keys, throwIfNotFound),
            "Should throw IllegalArgumentException when key is not found and throw is set");
    }

    @Test
    void validateAndFilterMap_keyNotFoundAndThrowIsFalse_returnsEmptyMap()
    {
        keys = Set.of("nonExistentKey");
        when(config.keySet()).thenReturn(Collections.emptySet());

        Map<String, Object> result = ConfigRetriever.validateAndFilterMap(config, keys, false);

        assertEquals(Collections.emptyMap(), result, "Should not throw error when key is not found and throw is false");
    }

    @Test
    void validateAndFilterMap_keyInKeysNull_throwsIllegalArgumentException()
    {
        keys = new HashSet<>();
        keys.add(null);

        assertThrows(IllegalArgumentException.class, () -> ConfigRetriever.validateAndFilterMap(config, keys, false),
            "Should throw IllegalArgumentException when a key in keys is null");
    }

    @Test
    void validateAndFilterMap_keyInKeysBlank_throwsIllegalArgumentException()
    {
        keys = Set.of(" ");

        assertThrows(IllegalArgumentException.class, () -> ConfigRetriever.validateAndFilterMap(config, keys, false),
            "Should throw IllegalArgumentException when a key in keys is blank");
    }

    @Test
    void validateAndFilterMap_valueInConfigNull_throwsIllegalArgumentException()
    {
        keys = Set.of("key1");
        when(config.keySet()).thenReturn(Set.of("key1"));
        when(config.get("key1")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> ConfigRetriever.validateAndFilterMap(config, keys, false),
            "Should throw IllegalArgumentException when a value in config is null");
    }
}
