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

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB adapter to match a comma separated list of key value pairs to a map. It an entry in the list has no value, the
 * key is used as value too.
 * 
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class XmlStringMapAdapter extends XmlAdapter<String, Map<String,String>> {

    private static final char WORD_SEPARATOR = ',';
    private static final char SPACE = ' ';
    private static final char KV_SEPARATOR = '=';

    @Override
    public Map<String, String> unmarshal(String v) {
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

    @Override
    public String marshal(Map<String, String> v) {
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
