package sonia.scm.jira;

import org.apache.commons.lang.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.MappingTarget;

public class BaseMapper {

  @SuppressWarnings("squid:S2068") // we have no password here
  static final String DUMMY_PASSWORD = "__DUMMY__";

  @AfterMapping
  void mapAutoCloseWords(@MappingTarget JiraConfiguration target, JiraConfigurationDto source) {
    if (source.getAutoCloseWords() != null) {
      target.setAutoCloseWordsForMapping(new XmlStringMapAdapter().unmarshal(source.getAutoCloseWords()));
    }
  }

  @AfterMapping
  void mapAutoCloseWords(@MappingTarget JiraConfigurationDto target, JiraConfiguration source) {
    if (source.getAutoCloseWordsForMapping() != null) {
      target.setAutoCloseWords(new XmlStringMapAdapter().marshal(source.getAutoCloseWordsForMapping()));
    }
  }

  @AfterMapping
  void replaceDummyWithOldPassword(@MappingTarget JiraConfiguration target, @Context JiraConfiguration oldConfiguration) {
    if ("__DUMMY__".equals(target.getPassword())) {
      target.setPassword(oldConfiguration.getPassword());
    }
  }

  @AfterMapping
  void replacePasswordWithDummy(@MappingTarget JiraConfigurationDto target) {
    if (StringUtils.isNotEmpty(target.getPassword())) {
      target.setPassword(DUMMY_PASSWORD);
    }
  }
}
