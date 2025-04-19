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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for validating and retrieving configuration values from a map.
 */
public class ConfigValidator
{
    /**
     * Validates and retrieves a single configuration value from a map.
     *
     * @param config The map containing the configuration.
     * @param key The key of the configuration value to retrieve.
     * @param expectedType The expected class type of the configuration value.
     * @param <T> The type of the expected configuration value.
     * @return The validated and cast configuration value.
     * @throws IllegalArgumentException If the key is missing, the value is null, or the value is not of the expected type.
     */
    public static <T> T validateAndGet(Map<String, Object> config, String key, Class<T> expectedType)
    {
        Object rawValue = config.get(key);

        if (rawValue == null)
        {
            throw new IllegalArgumentException(String.format("Configuration is missing key: '%s'", key));
        }

        if (!expectedType.isInstance(rawValue))
        {
            throw new IllegalArgumentException(String.format("Configuration key '%s' must be of type %s, but found: %s",
                                                            key, expectedType.getSimpleName(), rawValue.getClass().getSimpleName()));
        }

        // Cast to the now verified, expected type
        return expectedType.cast(rawValue);
    }

    /**
     * Validates and retrieves a list of configuration values from a map.
     *
     * @param config The map containing the configuration.
     * @param key The key of the configuration list to retrieve.
     * @param expectedElementType The expected class type of each element in the list.
     * @param <T> The type of the elements in the expected list.
     * @return The validated and cast list of configuration values.
     * @throws IllegalArgumentException If the key is missing, the value is null, the value is not a list,
     * or if the list contains null elements or elements of the incorrect type.
     */
    public static <T> List<T> validateAndGetList(Map<String, Object> config, String key, Class<T> expectedElementType)
    {
        Object rawObject = config.get(key);

        if (rawObject == null)
        {
            throw new IllegalArgumentException(String.format("Configuration list is missing key: '%s'", key));
        }

        if(!(rawObject instanceof List<?>))
        {
            throw new IllegalArgumentException(String.format("Configuration key '%s' must be of list type, but found: %s",
                                                            key, rawObject.getClass().getSimpleName()));
        }

        // Cast to verified list type
        List<?> rawList = (List<?>) rawObject;
        List<T>  validatedList = new ArrayList<>();
        for(Object element : rawList)
        {
            if(element == null)
            {
                throw new IllegalArgumentException(String.format("Configuration list '%s' contains a null element.", key));
            }
            if(!expectedElementType.isInstance(element))
            {
                throw new IllegalArgumentException(String.format("Elements in configuration list '%s' must be of type %s, but found incorrect type %s",
                                                                key, expectedElementType.getSimpleName(), element.getClass().getSimpleName()));
            }

            validatedList.add(expectedElementType.cast(element));
        }

        return validatedList;
    }
}
