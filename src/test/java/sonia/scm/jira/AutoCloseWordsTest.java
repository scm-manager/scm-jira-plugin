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

import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link AutoCloseWords}.
 * 
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class AutoCloseWordsTest {

    /**
     * Tests {@link AutoCloseWords#find(String, String)}.
     */
    @Test
    public void testFind() {
        assertTrue(AutoCloseWords.find("fixed", "fixed"));
        assertTrue(AutoCloseWords.find("FiXed", "fixed"));
        assertFalse(AutoCloseWords.find("fixed", "fix"));
        assertTrue(AutoCloseWords.find("issue is fixed", "fixed"));
        assertTrue(AutoCloseWords.find("fixed the issue", "fixed"));
        assertTrue(AutoCloseWords.find("auto close the issue", "auto close"));
    }
    
   /**
     * Tests {@link AutoCloseWords#format(Map)}.
     */
    @Test
    public void testFormat(){
        Map<String,String> map = Maps.newLinkedHashMap();
        map.put("auto close", "auto close");
        map.put("close", "close");
        map.put("fixed", "fixed");
        map.put("fix", "fixed");
        String value = AutoCloseWords.format(map);
        assertEquals("auto close, close, fixed, fix=fixed", value);
    }

    /**
     * Tests {@link AutoCloseWords#parse(String)}.
     */    
    @Test
    public void testParse() {
        String value = "auto close, close, fixed, fix=fixed";
        Map<String,String> map = AutoCloseWords.parse(value);
        assertEquals("auto close", map.get("auto close"));
        assertEquals("close", map.get("close"));
        assertEquals("fixed", map.get("fixed"));
        assertEquals("fixed", map.get("fix"));
    }

}