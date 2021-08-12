/*
 * Copyright 2018 berni3.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.huberb.iostreams;

import org.apache.commons.lang3.StringUtils;

/**
 * A a very simple sample data generator.
 *
 * @author berni3
 */
public class SampleData {

    /**
     * Create a small sample string.
     *
     * @return
     */
    String createSmallSample() {
        return StringUtils.repeat("A", 10);
    }

    /**
     * Create a sample string of length size, repeating parameter data.
     *
     * @param data
     * @param size
     * @return
     */
    String createSample(String data, int size) {
        int r = size / data.length();
        String result;
        if (r > 1) {
            result = StringUtils.repeat(data, r + 1);
        } else {
            result = data;
        }
        if (result.length() > size) {
            result = StringUtils.substring(result, 0, size);
        }
        return result;
    }
}
