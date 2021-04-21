/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
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
package sonia.scm.jira;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Util method to simplify auto close word handling.
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class AutoCloseWords {

    private static final char WORD_SEPARATOR = ',';

    private static final char SPACE = ' ';

    private static final char KV_SEPARATOR = '=';

    private AutoCloseWords() {
    }

    /**
     * Matches a comma separated list of key value pairs to a map. It an entry in the list has no value, the
     * key is used as value too.
     * 
     * @param v comma separated list
     * 
     * @return mapped entries
     */
    public static Map<String, String> parse(String v) {
        Map<String, String> map = Maps.newLinkedHashMap();
        for (String acw : Splitter.on(WORD_SEPARATOR).omitEmptyStrings().trimResults().split(v)) {
            Iterator<String> it = Splitter.on(KV_SEPARATOR).limit(2).trimResults().split(acw).iterator();
            String key = it.next();
            String value;
            if (it.hasNext()) {
                value = it.next();
            } else {
                value = key;
            }
            map.put(key, value);
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Maps the keys and values to a separated list of key value pairs.
     * 
     * @param v map
     * 
     * @return comma separated list
     */
    public static String format(Map<String, String> v) {
        StringBuilder buffer = new StringBuilder();
        Iterator<Map.Entry<String, String>> it = v.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> e = it.next();
            buffer.append(e.getKey());
            if (!e.getKey().equals(e.getValue())) {
                buffer.append(KV_SEPARATOR).append(e.getValue());
            }
            if (it.hasNext()) {
                buffer.append(WORD_SEPARATOR).append(SPACE);
            }
        }
        return buffer.toString();
    }

}
