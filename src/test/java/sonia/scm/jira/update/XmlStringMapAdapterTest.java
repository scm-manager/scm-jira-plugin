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
package sonia.scm.jira.update;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link XmlStringMapAdapter}.
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
class XmlStringMapAdapterTest {

  private final XmlStringMapAdapter adapter = new XmlStringMapAdapter();

  @Test
  void shouldMarshal() {
    Map<String, String> map = Maps.newLinkedHashMap();
    map.put("auto close", "auto close");
    map.put("close", "close");
    map.put("fixed", "fixed");
    map.put("fix", "fixed");
    String value = adapter.marshal(map);
    assertThat(value).isEqualTo("auto close, close, fixed, fix=fixed");
  }

  @Test
  void shouldUnmarshal() {
    String value = "auto close, close, fixed, fix=fixed";
    Map<String, String> map = adapter.unmarshal(value);
    assertThat(map)
      .containsEntry("auto close", "auto close")
      .containsEntry("close", "close")
      .containsEntry("fixed", "fixed")
      .containsEntry("fix", "fixed");
  }

}
