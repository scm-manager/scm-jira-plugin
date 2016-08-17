/***
 * Copyright (c) 2015, Sebastian Sdorra
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * https://bitbucket.org/sdorra/scm-manager
 * 
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