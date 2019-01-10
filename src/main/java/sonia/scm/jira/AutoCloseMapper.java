package sonia.scm.jira;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

public class AutoCloseMapper {

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
}
