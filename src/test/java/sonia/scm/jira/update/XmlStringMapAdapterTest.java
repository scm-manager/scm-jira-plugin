/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
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
