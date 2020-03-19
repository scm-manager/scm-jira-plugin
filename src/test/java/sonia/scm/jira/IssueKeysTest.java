/**
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link IssueKeys}.
 * 
 * @author Sebastian Sdorra <s.sdorra@gmail.com>
 */
public class IssueKeysTest {


    /**
     * Tests {@link IssueKeys#createPattern(java.lang.String)}.
     */
    @Test
    public void testCreatePattern() {
        Pattern pattern = IssueKeys.createPattern("ASD");
        assertEquals("ASD-42", extract(pattern, "test matcher for ASD-42"));
    }

    /**
     * Tests {@link IssueKeys#createPattern(java.lang.String)} with an empty string.
     */    
    @Test
    public void testCreatePatternWithoutConfiguration(){
        Pattern pattern = IssueKeys.createPattern("");
        assertEquals("ASD-42", extract(pattern, "test matcher for ASD-42"));
    }
    
    /**
     * Tests {@link IssueKeys#createPattern(java.lang.String)} with multiple project keys.
     */
    @Test
    public void testCreatePatternWithMultipleProjectKeys(){
        Pattern pattern = IssueKeys.createPattern("SCM, TST,ASD");
        assertEquals("ASD-42", extract(pattern, "test matcher for ASD-42"));
    }

    private String extract(Pattern pattern, String message){
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        return matcher.group(1);
    }
}