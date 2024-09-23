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

import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.TypedStoreParameters;

import jakarta.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

class TestingConfigurationStoreFactory implements ConfigurationStoreFactory {

  private final Map<String,byte[]> storage = new HashMap<>();

  void add(String name, byte[] value) {
    storage.put(name, value);
  }

  @Override
  public <T> ConfigurationStore<T> getStore(TypedStoreParameters<T> storeParameters) {
    return new TestingConfigurationStore<>(storeParameters.getName(), storeParameters.getType());
  }

  public class TestingConfigurationStore<T> implements ConfigurationStore<T> {

    private final String name;
    private final Class<T> type;

    private TestingConfigurationStore(String name, Class<T> type) {
      this.name = name;
      this.type = type;
    }

    @Override
    public T get() {
      byte[] bytes = storage.get(name);
      if (bytes != null) {
        return JAXB.unmarshal(new ByteArrayInputStream(bytes), type);
      }
      return null;
    }

    @Override
    public void set(T object) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      JAXB.marshal(object, baos);
      storage.put(name, baos.toByteArray());
    }
  }
}
