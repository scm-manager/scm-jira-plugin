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

import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB adapter to match a comma separated list of key value pairs to a map. It an entry in the list has no value, the
 * key is used as value too.
 * 
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class XmlStringMapAdapter extends XmlAdapter<String, Map<String,String>> {
    
    @Override
    public Map<String, String> unmarshal(String v) {
        return AutoCloseWords.parse(v);
    }

    @Override
    public String marshal(Map<String, String> v) {
        return AutoCloseWords.format(v);
    }
}
