package sonia.scm.jira.update;

import com.sun.xml.bind.v2.util.ByteArrayOutputStreamEx;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.TypedStoreParameters;

import javax.xml.bind.JAXB;
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
